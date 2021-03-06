/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.core.historicaltimeseries.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.LocalDate;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.core.change.ChangeManager;
import com.opengamma.core.change.DummyChangeManager;
import com.opengamma.core.historicaltimeseries.HistoricalTimeSeries;
import com.opengamma.core.historicaltimeseries.HistoricalTimeSeriesSource;
import com.opengamma.id.ExternalId;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.id.UniqueId;
import com.opengamma.timeseries.date.localdate.ImmutableLocalDateDoubleTimeSeries;
import com.opengamma.timeseries.date.localdate.LocalDateDoubleTimeSeries;
import com.opengamma.timeseries.date.localdate.LocalDateDoubleTimeSeriesBuilder;
import com.opengamma.timeseries.date.localdate.LocalDateToIntConverter;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.ParallelArrayBinarySort;
import com.opengamma.util.metric.OpenGammaMetricRegistry;
import com.opengamma.util.tuple.Pair;
import com.opengamma.util.tuple.Pairs;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * An extremely minimal and lightweight {@code HistoricalTimeSeriesSource} that pulls data
 * directly from Redis for situations where versioning, multiple fields, multiple data sources,
 * and identifier management is not necessary.
 * <p>
 * The following constraints must hold for this Source to be of any utility whatsoever:
 * <ul>
 *   <li>Historical lookups are not required. Because they are not supported.</li>
 *   <li>Version corrections are not required. Because they are not supported.</li>
 *   <li>Each time series has a <b>single</b> {@link ExternalId} which then acts
 *       as the {@link UniqueId} internally.</li>
 *   <li>Each external ID has a single time series (thus there is not the capacity to store
 *       different Data Source, Data Provider, Observation Time, Data Field series).</li>
 * </ul>
 * <p>
 * Where a method is not supported semantically, an {@link UnsupportedOperationException}
 * will be thrown. Where use indicates that this class may be being used incorrectly,
 * a log message will be written at {@code WARN} level.
 */
public class NonVersionedRedisHistoricalTimeSeriesSource implements HistoricalTimeSeriesSource {
  private static final Logger LOGGER = LoggerFactory.getLogger(NonVersionedRedisHistoricalTimeSeriesSource.class);
  private final JedisPool _jedisPool;
  private final String _redisPrefix;
  // ChangeManager is only returned to satisfy the interface and allow this source to be used with the engine, no notifications will be sent
  private final ChangeManager _changeManager = DummyChangeManager.INSTANCE;

  private Timer _getSeriesTimer = new Timer();
  private Timer _updateSeriesTimer = new Timer();
  private Timer _existsSeriesTimer = new Timer();

  public NonVersionedRedisHistoricalTimeSeriesSource(final JedisPool jedisPool) {
    this(jedisPool, "");
  }

  public NonVersionedRedisHistoricalTimeSeriesSource(final JedisPool jedisPool, final String redisPrefix) {
    this(jedisPool, redisPrefix, "NonVersionedRedisHistoricalTimeSeriesSource");
  }

  protected NonVersionedRedisHistoricalTimeSeriesSource(final JedisPool jedisPool, final String redisPrefix, final String metricsName) {
    ArgumentChecker.notNull(jedisPool, "jedisPool");
    ArgumentChecker.notNull(redisPrefix, "redisPrefix");
    ArgumentChecker.notNull(metricsName, "metricsName");
    _jedisPool = jedisPool;
    _redisPrefix = redisPrefix;
    registerMetrics(OpenGammaMetricRegistry.getSummaryInstance(), OpenGammaMetricRegistry.getDetailedInstance(), metricsName);
  }

  /**
   * Gets the jedisPool.
   * @return the jedisPool
   */
  protected JedisPool getJedisPool() {
    return _jedisPool;
  }

  /**
   * Gets the redisPrefix.
   * @return the redisPrefix
   */
  protected String getRedisPrefix() {
    return _redisPrefix;
  }

  public void registerMetrics(final MetricRegistry summaryRegistry, final MetricRegistry detailRegistry, final String namePrefix) {
    _getSeriesTimer = summaryRegistry.timer(namePrefix + ".get");
    _updateSeriesTimer = summaryRegistry.timer(namePrefix + ".update");
    _existsSeriesTimer = summaryRegistry.timer(namePrefix + ".exists");
  }

  /**
   * Add a timeseries to Redis.
   *
   * If the timerseries does not exist, it is created otherwise updated.
   *
   * @param uniqueId the uniqueId, not null.
   * @param timeseries the timeseries, not null.
   */
  public void updateTimeSeries(final UniqueId uniqueId, final LocalDateDoubleTimeSeries timeseries) {
    ArgumentChecker.notNull(uniqueId, "uniqueId");
    ArgumentChecker.notNull(timeseries, "timeseries");

    updateTimeSeries(toRedisKey(uniqueId), timeseries, false);
  }

  /**
   * Remove all current entries for the given ID, and store the given time series.
   *
   * If the timeseries does not exist, it is created.
   *
   * @param uniqueId the uniqueId of the timeseries, not null.
   * @param timeSeries the timeseries to store
   */
  public void replaceTimeSeries(final UniqueId uniqueId, final LocalDateDoubleTimeSeries timeSeries) {
    ArgumentChecker.notNull(uniqueId, "uniqueId");
    ArgumentChecker.notNull(timeSeries, "timeSeries");

    updateTimeSeries(toRedisKey(uniqueId), timeSeries, true, 5);
  }

  protected void updateTimeSeries(final String redisKey, final LocalDateDoubleTimeSeries timeseries, final boolean clear) {
    updateTimeSeries(redisKey, timeseries, clear, 5);
  }

  protected void updateTimeSeries(final String redisKey, final LocalDateDoubleTimeSeries timeseries, final boolean clear, final int attempts) {
    try (Timer.Context context = _updateSeriesTimer.time()) {
      final Jedis jedis = getJedisPool().getResource();
      try {
        final Map<String, String> htsMap = Maps.newHashMap();
        final BiMap<Double, String> dates = HashBiMap.create();
        for (final Entry<LocalDate, Double> entry : timeseries) {
          final String dateAsIntText = Integer.toString(LocalDateToIntConverter.convertToInt(entry.getKey()));
          htsMap.put(dateAsIntText, Double.toString(entry.getValue()));
          dates.put(Double.valueOf(dateAsIntText), dateAsIntText);
        }

        final String redisHtsDatapointKey = toRedisHtsDatapointKey(redisKey);
        jedis.hmset(redisHtsDatapointKey, htsMap);

        final String redisHtsDaysKey = toRedisHtsDaysKey(redisKey);
        if (clear) {
          jedis.del(redisHtsDaysKey);
        } else {
          jedis.zrem(redisHtsDaysKey, dates.inverse().keySet().toArray(new String[dates.size()]));
        }
        jedis.zadd(redisHtsDaysKey, dates.inverse());
        getJedisPool().close();
      } catch (final Throwable e) {
        getJedisPool().close();
        if (attempts > 0) {
          LOGGER.warn("Unable to put timeseries with id, will retry: " + redisKey, e);
          updateTimeSeries(redisKey, timeseries, clear, attempts - 1);
        }
        throw new OpenGammaRuntimeException("Unable to put timeseries with id: " + redisKey, e);
      }
    }
  }

  private static String toRedisHtsDaysKey(final String redisKey) {
    return redisKey + ":hts.days";
  }

  private static String toRedisHtsDatapointKey(final String redisKey) {
    return redisKey + ":hts.datapoint";
  }

  /**
   * Updates a datapoint in a timeseries.
   *
   * If the timeseries does not exist, one is created with the single data point.
   *
   * @param uniqueId the uniqueId of the timeseries, not null.
   * @param valueDate the data point date, not null
   * @param value the data point value
   */
  public void updateTimeSeriesPoint(final UniqueId uniqueId, final LocalDate valueDate, final double value) {
    ArgumentChecker.notNull(uniqueId, "uniqueId");
    ArgumentChecker.notNull(valueDate, "valueDate");

    updateTimeSeriesPoint(toRedisKey(uniqueId), valueDate, value);
  }

  protected void updateTimeSeriesPoint(final String redisKey, final LocalDate valueDate, final double value) {
    final LocalDateDoubleTimeSeriesBuilder builder = ImmutableLocalDateDoubleTimeSeries.builder();
    builder.put(valueDate, value);
    updateTimeSeries(redisKey, builder.build(), false);
  }

  /**
   * Completely empty the underlying Redis server.
   * You should only call this if you really know what you're doing.
   */
  public void completelyClearRedis() {
    final Jedis jedis = getJedisPool().getResource();
    try {
      jedis.flushDB();
      getJedisPool().close();
    } catch (final Exception e) {
      LOGGER.error("Unable to clear database", e);
      getJedisPool().close();
      throw new OpenGammaRuntimeException("Unable to clear database", e);
    }
  }

  protected String toRedisKey(final UniqueId uniqueId) {
    return toRedisKey(uniqueId, null);
  }

  protected String toRedisKey(final ExternalIdBundle identifierBundle) {
    return toRedisKey(toUniqueId(identifierBundle));
  }

  protected String toRedisKey(final UniqueId uniqueId, final LocalDate simulationExecutionDate) {
    final StringBuilder sb = new StringBuilder();
    final String redisPrefix = StringUtils.trimToNull(getRedisPrefix());
    if (redisPrefix != null) {
      sb.append(getRedisPrefix());
      sb.append(':');
    }
    sb.append(LocalDateDoubleTimeSeries.class.getSimpleName());
    sb.append(':');
    sb.append(uniqueId.getScheme());
    sb.append('~');
    sb.append(uniqueId.getValue());
    if (simulationExecutionDate != null) {
      sb.append(':');
      sb.append(simulationExecutionDate.toString());
    }

    return sb.toString();
  }

  protected UniqueId toUniqueId(final ExternalIdBundle identifierBundle) {
    if (identifierBundle.size() != 1) {
      LOGGER.warn("Using NonVersionedRedisHistoricalTimeSeriesSource with bundle {} other than 1. Probable misuse.", identifierBundle);
    }
    final ExternalId id = identifierBundle.iterator().next();
    final UniqueId uniqueId = UniqueId.of(id.getScheme().getName(), id.getValue());
    return uniqueId;
  }

  public boolean exists(final UniqueId uniqueId, final LocalDate simulationExecutionDate) {
    try (Timer.Context context = _existsSeriesTimer.time()) {
      final String redisKey = toRedisKey(uniqueId, simulationExecutionDate);
      final String redisHtsDaysKey = toRedisHtsDaysKey(redisKey);
      boolean exists = false;
      final Jedis jedis = getJedisPool().getResource();
      try {
        exists = jedis.exists(redisHtsDaysKey);
        getJedisPool().close();
      } catch (final Exception e) {
        LOGGER.error("Unable to check for existance", e);
        getJedisPool().close();
        throw new OpenGammaRuntimeException("Unable to check for existance", e);
      }
      return exists;
    }
  }

  public boolean exists(final UniqueId uniqueId) {
    return exists(uniqueId, null);
  }

  public boolean exists(final ExternalIdBundle identifierBundle) {
    return exists(toUniqueId(identifierBundle));
  }

  // ------------------------------------------------------------------------
  // SUPPORTED HISTORICAL TIME SERIES SOURCE OPERATIONS:
  // ------------------------------------------------------------------------

  protected LocalDateDoubleTimeSeries loadTimeSeriesFromRedis(final String redisKey, final LocalDate start, final LocalDate end) {
    // This is the only method that needs implementation.
    try (Timer.Context context = _getSeriesTimer.time()) {
      final Jedis jedis = getJedisPool().getResource();
      LocalDateDoubleTimeSeries ts = null;
      try {
        final String redisHtsDaysKey = toRedisHtsDaysKey(redisKey);
        double min = Double.NEGATIVE_INFINITY;
        double max = Double.POSITIVE_INFINITY;
        if (start != null) {
          min = localDateToDouble(start);
        }
        if (end != null) {
          max = localDateToDouble(end);
        }
        final Set<String> dateTexts = jedis.zrangeByScore(redisHtsDaysKey, min, max);
        if (!dateTexts.isEmpty()) {
          final String redisHtsDatapointKey = toRedisHtsDatapointKey(redisKey);
          final List<String> valueTexts = jedis.hmget(redisHtsDatapointKey, dateTexts.toArray(new String[dateTexts.size()]));

          final int[] times = new int[dateTexts.size()];
          final double[] values = new double[valueTexts.size()];

          final Iterator<String> dateItr = dateTexts.iterator();
          final Iterator<String> valueItr = valueTexts.iterator();

          int i = 0;
          int j = 0;
          while (dateItr.hasNext()) {
            final String dateAsIntText = dateItr.next();
            final String valueText = StringUtils.trimToNull(valueItr.next());
            if (valueText != null) {
              times[i++] = Integer.parseInt(dateAsIntText);
              values[j++] = Double.parseDouble(valueText);
            }
          }
          ParallelArrayBinarySort.parallelBinarySort(times, values);
          ts = ImmutableLocalDateDoubleTimeSeries.of(times, values);
        }
        getJedisPool().close();
      } catch (final Exception e) {
        LOGGER.error("Unable to load points from redis for " + redisKey, e);
        getJedisPool().close();
        throw new OpenGammaRuntimeException("Unable to load points from redis for " + redisKey, e);
      }
      return ts;
    }
  }

  private static double localDateToDouble(final LocalDate date) {
    final String dateAsIntText = Integer.toString(LocalDateToIntConverter.convertToInt(date));
    return Double.parseDouble(dateAsIntText);
  }

  @Override
  public HistoricalTimeSeries getHistoricalTimeSeries(final UniqueId uniqueId, final LocalDate start, final boolean includeStart,
      final LocalDate end, final boolean includeEnd) {
    ArgumentChecker.notNull(uniqueId, "uniqueId");

    LocalDate actualStart = null;
    LocalDate actualEnd = null;

    if (start != null) {
      if (includeStart) {
        actualStart = start;
      } else {
        actualStart = start.plusDays(1);
      }
    }

    if (end != null) {
      if (includeEnd) {
        actualEnd = end;
      } else {
        actualEnd = end.minusDays(1);
      }
    }

    final LocalDateDoubleTimeSeries ts = loadTimeSeriesFromRedis(toRedisKey(uniqueId), actualStart, actualEnd);
    SimpleHistoricalTimeSeries result = null;
    if (ts != null) {
      result = new SimpleHistoricalTimeSeries(uniqueId, ts);
    }
    return result;
  }

  @Override
  public HistoricalTimeSeries getHistoricalTimeSeries(final String dataField, final ExternalIdBundle identifierBundle,
      final String resolutionKey, final LocalDate start, final boolean includeStart, final LocalDate end,
                                                      final boolean includeEnd, final int maxPoints) {
    ArgumentChecker.notNull(identifierBundle, "identifierBundle");

    if (identifierBundle.isEmpty()) {
      return null;
    }
    final ExternalId id = identifierBundle.iterator().next();
    final UniqueId uniqueId = UniqueId.of(id.getScheme().getName(), id.getValue());
    return getHistoricalTimeSeries(uniqueId, start, includeStart, end, includeEnd);
  }

  @Override
  public HistoricalTimeSeries getHistoricalTimeSeries(final UniqueId uniqueId) {
    ArgumentChecker.notNull(uniqueId, "uniqueId");

    final LocalDateDoubleTimeSeries ts = loadTimeSeriesFromRedis(toRedisKey(uniqueId), null, null);
    if (ts == null) {
      return null;
    }
    return new SimpleHistoricalTimeSeries(uniqueId, ts);
  }

  @Override
  public ExternalIdBundle getExternalIdBundle(final UniqueId uniqueId) {
    return ExternalId.of(uniqueId.getScheme(), uniqueId.getValue()).toBundle();
  }

  @Override
  public Pair<LocalDate, Double> getLatestDataPoint(final UniqueId uniqueId) {
    ArgumentChecker.notNull(uniqueId, "uniqueId");

    Pair<LocalDate, Double> latestPoint = null;
    final LocalDateDoubleTimeSeries ts = loadTimeSeriesFromRedis(toRedisKey(uniqueId), null, null);
    if (ts != null) {
      latestPoint = Pairs.of(ts.getLatestTime(), ts.getLatestValue());
    }
    return latestPoint;
  }

  @Override
  public Pair<LocalDate, Double> getLatestDataPoint(final UniqueId uniqueId, final LocalDate start, final boolean includeStart,
      final LocalDate end, final boolean includeEnd) {
    ArgumentChecker.notNull(uniqueId, "uniqueId");

    final HistoricalTimeSeries hts = getHistoricalTimeSeries(uniqueId, start, includeStart, end, includeEnd);
    Pair<LocalDate, Double> latestPoint = null;
    if (hts != null && hts.getTimeSeries() != null) {
      latestPoint = Pairs.of(hts.getTimeSeries().getLatestTime(), hts.getTimeSeries().getLatestValue());
    }
    return latestPoint;
  }

  protected LocalDateDoubleTimeSeries getLocalDateDoubleTimeSeries(final ExternalIdBundle identifierBundle) {
    return loadTimeSeriesFromRedis(toRedisKey(identifierBundle), null, null);
  }

  protected HistoricalTimeSeries getHistoricalTimeSeries(final ExternalIdBundle identifierBundle) {
    final UniqueId uniqueId = toUniqueId(identifierBundle);

    final LocalDateDoubleTimeSeries ts = getLocalDateDoubleTimeSeries(identifierBundle);
    if (ts == null) {
      return null;
    }
    final HistoricalTimeSeries hts = new SimpleHistoricalTimeSeries(uniqueId, ts);
    return hts;
  }

  @Override
  public HistoricalTimeSeries getHistoricalTimeSeries(final ExternalIdBundle identifierBundle, final String dataSource,
      final String dataProvider, final String dataField) {
    return getHistoricalTimeSeries(identifierBundle);
  }

  @Override
  public HistoricalTimeSeries getHistoricalTimeSeries(final ExternalIdBundle identifierBundle, final LocalDate identifierValidityDate,
      final String dataSource, final String dataProvider, final String dataField) {
    return getHistoricalTimeSeries(identifierBundle);
  }

  @Override
  public Pair<LocalDate, Double> getLatestDataPoint(final ExternalIdBundle identifierBundle, final LocalDate identifierValidityDate,
      final String dataSource, final String dataProvider, final String dataField) {
    final UniqueId uniqueId = toUniqueId(identifierBundle);
    return getLatestDataPoint(uniqueId);
  }

  @Override
  public Pair<LocalDate, Double> getLatestDataPoint(final ExternalIdBundle identifierBundle, final String dataSource,
      final String dataProvider, final String dataField) {
    final UniqueId uniqueId = toUniqueId(identifierBundle);
    return getLatestDataPoint(uniqueId);
  }

  @Override
  public HistoricalTimeSeries getHistoricalTimeSeries(final String dataField, final ExternalIdBundle identifierBundle, final String resolutionKey) {
    final UniqueId uniqueId = toUniqueId(identifierBundle);
    return getHistoricalTimeSeries(uniqueId);
  }

  @Override
  public HistoricalTimeSeries getHistoricalTimeSeries(final String dataField, final ExternalIdBundle identifierBundle,
      final LocalDate identifierValidityDate, final String resolutionKey) {
    final UniqueId uniqueId = toUniqueId(identifierBundle);
    return getHistoricalTimeSeries(uniqueId);
  }

  @Override
  public Pair<LocalDate, Double> getLatestDataPoint(final String dataField, final ExternalIdBundle identifierBundle, final String resolutionKey) {
    final UniqueId uniqueId = toUniqueId(identifierBundle);
    return getLatestDataPoint(uniqueId);
  }

  @Override
  public Pair<LocalDate, Double> getLatestDataPoint(final String dataField, final ExternalIdBundle identifierBundle,
      final LocalDate identifierValidityDate, final String resolutionKey) {
    final UniqueId uniqueId = toUniqueId(identifierBundle);
    return getLatestDataPoint(uniqueId);
  }

  @Override
  public ChangeManager changeManager() {
    return _changeManager;
  }

  @Override
  public HistoricalTimeSeries getHistoricalTimeSeries(final ExternalIdBundle identifierBundle, final String dataSource,
      final String dataProvider, final String dataField, final LocalDate start, final boolean includeStart,
      final LocalDate end, final boolean includeEnd) {
    final UniqueId uniqueId = toUniqueId(identifierBundle);
    return getHistoricalTimeSeries(uniqueId, start, includeStart, end, includeEnd);
  }

  @Override
  public HistoricalTimeSeries getHistoricalTimeSeries(final String dataField, final ExternalIdBundle identifierBundle,
      final String resolutionKey, final LocalDate start, final boolean includeStart, final LocalDate end,
      final boolean includeEnd) {
    final UniqueId uniqueId = toUniqueId(identifierBundle);
    return getHistoricalTimeSeries(uniqueId, start, includeStart, end, includeEnd);
  }

  @Override
  public HistoricalTimeSeries getHistoricalTimeSeries(final String dataField, final ExternalIdBundle identifierBundle,
      final LocalDate identifierValidityDate, final String resolutionKey, final LocalDate start,
      final boolean includeStart, final LocalDate end, final boolean includeEnd) {
    final UniqueId uniqueId = toUniqueId(identifierBundle);
    return getHistoricalTimeSeries(uniqueId, start, includeStart, end, includeEnd);
  }

  @Override
  public HistoricalTimeSeries getHistoricalTimeSeries(final UniqueId uniqueId, final LocalDate start, final boolean includeStart,
      final LocalDate end, final boolean includeEnd, final int maxPoints) {
    return getHistoricalTimeSeries(uniqueId, start, includeStart, end, includeEnd);
  }

  @Override
  public HistoricalTimeSeries getHistoricalTimeSeries(final ExternalIdBundle identifierBundle, final String dataSource,
      final String dataProvider, final String dataField, final LocalDate start, final boolean includeStart,
      final LocalDate end, final boolean includeEnd, final int maxPoints) {
    final UniqueId uniqueId = toUniqueId(identifierBundle);
    return getHistoricalTimeSeries(uniqueId, start, includeStart, end, includeEnd);
  }

  @Override
  public HistoricalTimeSeries getHistoricalTimeSeries(final ExternalIdBundle identifierBundle, final LocalDate identifierValidityDate,
      final String dataSource, final String dataProvider, final String dataField, final LocalDate start,
      final boolean includeStart, final LocalDate end, final boolean includeEnd) {
    final UniqueId uniqueId = toUniqueId(identifierBundle);
    return getHistoricalTimeSeries(uniqueId, start, includeStart, end, includeEnd);
  }

  @Override
  public HistoricalTimeSeries getHistoricalTimeSeries(final ExternalIdBundle identifierBundle, final LocalDate identifierValidityDate,
      final String dataSource, final String dataProvider, final String dataField, final LocalDate start,
      final boolean includeStart, final LocalDate end, final boolean includeEnd, final int maxPoints) {
    final UniqueId uniqueId = toUniqueId(identifierBundle);
    return getHistoricalTimeSeries(uniqueId, start, includeStart, end, includeEnd);
  }

  @Override
  public Pair<LocalDate, Double> getLatestDataPoint(final ExternalIdBundle identifierBundle, final LocalDate identifierValidityDate,
      final String dataSource, final String dataProvider, final String dataField, final LocalDate start,
      final boolean includeStart, final LocalDate end, final boolean includeEnd) {
    final UniqueId uniqueId = toUniqueId(identifierBundle);
    return getLatestDataPoint(uniqueId, start, includeStart, end, includeEnd);
  }

  @Override
  public Pair<LocalDate, Double> getLatestDataPoint(final ExternalIdBundle identifierBundle, final String dataSource,
      final String dataProvider, final String dataField, final LocalDate start, final boolean includeStart, final LocalDate end,
      final boolean includeEnd) {
    final UniqueId uniqueId = toUniqueId(identifierBundle);
    return getLatestDataPoint(uniqueId, start, includeStart, end, includeEnd);
  }

  @Override
  public HistoricalTimeSeries getHistoricalTimeSeries(final String dataField, final ExternalIdBundle identifierBundle,
      final LocalDate identifierValidityDate, final String resolutionKey, final LocalDate start,
      final boolean includeStart, final LocalDate end, final boolean includeEnd, final int maxPoints) {
    final UniqueId uniqueId = toUniqueId(identifierBundle);
    return getHistoricalTimeSeries(uniqueId, start, includeStart, end, includeEnd);
  }

  @Override
  public Pair<LocalDate, Double> getLatestDataPoint(final String dataField, final ExternalIdBundle identifierBundle, final String resolutionKey,
      final LocalDate start, final boolean includeStart, final LocalDate end, final boolean includeEnd) {
    final UniqueId uniqueId = toUniqueId(identifierBundle);
    return getLatestDataPoint(uniqueId, start, includeStart, end, includeEnd);
  }

  @Override
  public Pair<LocalDate, Double> getLatestDataPoint(final String dataField, final ExternalIdBundle identifierBundle,
      final LocalDate identifierValidityDate, final String resolutionKey, final LocalDate start, final boolean includeStart,
      final LocalDate end, final boolean includeEnd) {
    final UniqueId uniqueId = toUniqueId(identifierBundle);
    return getLatestDataPoint(uniqueId, start, includeStart, end, includeEnd);
  }

  @Override
  public Map<ExternalIdBundle, HistoricalTimeSeries> getHistoricalTimeSeries(final Set<ExternalIdBundle> identifierSet,
      final String dataSource, final String dataProvider, final String dataField, final LocalDate start,
      final boolean includeStart, final LocalDate end, final boolean includeEnd) {
    final ImmutableMap.Builder<ExternalIdBundle, HistoricalTimeSeries> map = ImmutableMap.builder();
    for (final ExternalIdBundle bundle : identifierSet) {
      final HistoricalTimeSeries series = getHistoricalTimeSeries(bundle, dataSource, dataProvider, dataField, start, includeStart, end, includeEnd);
      map.put(bundle, series);
    }
    return map.build();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final NonVersionedRedisHistoricalTimeSeriesSource that = (NonVersionedRedisHistoricalTimeSeriesSource) o;
    return _jedisPool.equals(that._jedisPool) && _redisPrefix.equals(that._redisPrefix);
  }

  @Override
  public int hashCode() {
    final int result = _jedisPool.hashCode();
    return 31 * result + _redisPrefix.hashCode();
  }
}

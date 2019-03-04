/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.convention;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.opengamma.core.historicaltimeseries.HistoricalTimeSeries;
import com.opengamma.core.historicaltimeseries.HistoricalTimeSeriesSource;
import com.opengamma.id.ExternalId;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.id.ExternalScheme;
import com.opengamma.master.historicaltimeseries.HistoricalTimeSeriesLoader;
import com.opengamma.util.ArgumentChecker;

/**
 * Populates an historical time-series master with missing time-series for each
 * instrument referenced by the {@link InMemoryConventionBundleMaster}.
 *
 * @deprecated This loaders uses {@link ConventionBundle}s, which are deprecated
 */
@Deprecated
public class ConventionInstrumentTimeSeriesLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConventionInstrumentTimeSeriesLoader.class);

  private final InMemoryConventionBundleMaster _conventionMaster;

  private final HistoricalTimeSeriesSource _htsSource;
  private final HistoricalTimeSeriesLoader _htsLoader;
  private final String _dataSource;
  private final String _dataProvider;
  private final String _dataField;
  private final ExternalScheme _identifierScheme;
  private final boolean _updateExisting;

  public ConventionInstrumentTimeSeriesLoader(final HistoricalTimeSeriesSource htsSource,
      final HistoricalTimeSeriesLoader htsLoader, final String dataSource, final String dataProvider, final String dataField,
      final ExternalScheme identifierScheme, final boolean updateExisting) {
    ArgumentChecker.notNull(htsSource, "htsSource");
    ArgumentChecker.notNull(htsLoader, "htsLoader");
    ArgumentChecker.notNull(dataSource, "dataSource");
    ArgumentChecker.notNull(dataProvider, "dataProvider");
    ArgumentChecker.notNull(dataField, "dataField");
    ArgumentChecker.notNull(identifierScheme, "identifierScheme");
    _conventionMaster = new InMemoryConventionBundleMaster();
    _htsSource = htsSource;
    _htsLoader = htsLoader;
    _dataSource = dataSource;
    _dataProvider = dataProvider;
    _dataField = dataField;
    _identifierScheme = identifierScheme;
    _updateExisting = updateExisting;
  }

  private InMemoryConventionBundleMaster getConventionMaster() {
    return _conventionMaster;
  }

  private HistoricalTimeSeriesSource getHistoricalTimeSeriesSource() {
    return _htsSource;
  }

  private HistoricalTimeSeriesLoader getHistoricalTimeSeriesLoader() {
    return _htsLoader;
  }

  private String getDataSource() {
    return _dataSource;
  }

  private String getDataProvider() {
    return _dataProvider;
  }

  private String getDataField() {
    return _dataField;
  }

  private ExternalScheme getIdentifierScheme() {
    return _identifierScheme;
  }

  private boolean isUpdateExisting() {
    return _updateExisting;
  }

  //-------------------------------------------------------------------------
  public void run() {
    final Collection<ConventionBundle> conventions = getConventionMaster().getAll();
    final Set<ExternalId> externalIds = new HashSet<>();
    for (final ConventionBundle convention : conventions) {
      addExternalId(convention.getSwapFloatingLegInitialRate(), externalIds);
    }
    LOGGER.info("Checking {} time-series: {}", externalIds.size(), externalIds);
    for (final ExternalId externalId : externalIds) {
      ensureTimeseries(externalId);
    }
  }

  private void addExternalId(final ExternalId externalId, final Set<ExternalId> externalIds) {
    if (externalId == null) {
      return;
    }
    ExternalId eid = null;
    if (externalId.isNotScheme(getIdentifierScheme())) {
      final ConventionBundleSearchResult result = getConventionMaster().searchConventionBundle(new ConventionBundleSearchRequest(externalId));
      if (result.getResults().size() == 0) {
        LOGGER.warn("Unable to find mapping from {} to identifier with scheme {}", externalId, getIdentifierScheme());
        return;
      }
      if (result.getResults().size() > 1) {
        LOGGER.warn("Found multiple conventions for {}, with potentially ambiguous mappings to scheme {}", externalId, getIdentifierScheme());
        return;
      }
      final ConventionBundleDocument searchResult = Iterables.getOnlyElement(result.getResults());
      eid = searchResult.getConventionSet().getIdentifiers().getExternalId(getIdentifierScheme());
      if (eid == null) {
        LOGGER.warn("Convention for {} does not include a mapping to an identifier with scheme {}", eid, getIdentifierScheme());
        return;
      }
    }
    externalIds.add(eid);
  }

  private void ensureTimeseries(final ExternalId externalId) {
    LOGGER.info("Checking time-series for {}", externalId);
    try {
      final HistoricalTimeSeries hts = getHistoricalTimeSeriesSource().getHistoricalTimeSeries(ExternalIdBundle.of(externalId), getDataSource(), getDataProvider(), getDataField());
      if (hts == null) {
        LOGGER.info("Adding time-series for {}", externalId);
        getHistoricalTimeSeriesLoader().loadTimeSeries(ImmutableSet.of(externalId), getDataProvider(), getDataField(), null, null);
      } else if (isUpdateExisting()) {
        LOGGER.info("Updating time-series for {} with identifier {}", externalId, hts.getUniqueId());
        getHistoricalTimeSeriesLoader().updateTimeSeries(hts.getUniqueId());
      }
    } catch (final Exception e) {
      LOGGER.error("Error with time-series for " + externalId, e);
    }
  }

}

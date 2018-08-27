/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.component.factory.source;

import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.threeten.bp.LocalDate;

import com.opengamma.component.ComponentInfo;
import com.opengamma.component.ComponentRepository;
import com.opengamma.component.factory.AbstractComponentFactory;
import com.opengamma.component.factory.ComponentInfoAttributes;
import com.opengamma.core.historicaltimeseries.HistoricalTimeSeriesSource;
import com.opengamma.core.historicaltimeseries.impl.DataHistoricalTimeSeriesSourceResource;
import com.opengamma.core.historicaltimeseries.impl.RedisSimulationSeriesSource;
import com.opengamma.core.historicaltimeseries.impl.RemoteHistoricalTimeSeriesSource;
import com.opengamma.master.historicaltimeseries.HistoricalTimeSeriesResolver;
import com.opengamma.master.historicaltimeseries.impl.DataHistoricalTimeSeriesResolverResource;
import com.opengamma.master.historicaltimeseries.impl.RedisSimulationSeriesResolver;
import com.opengamma.master.historicaltimeseries.impl.RemoteHistoricalTimeSeriesResolver;
import com.opengamma.util.fudgemsg.OpenGammaFudgeContext;
import com.opengamma.util.redis.RedisConnector;

import redis.clients.jedis.JedisPool;

/**
 * A component factory to build {@code RedisSimulationSeriesSource} instances.
 */
@BeanDefinition
public class RedisSimulationSeriesSourceComponentFactory extends AbstractComponentFactory {

  /**
   * The classifier that the factory should publish under.
   */
  @PropertyDefinition(validate = "notNull")
  private String _classifier;
  /**
   * Connector to the Redis server.
   */
  @PropertyDefinition
  private RedisConnector _redisConnector;
  /**
   * prefix to append to redis keys when stored
   */
  @PropertyDefinition
  private String _redisPrefix = "";
  /**
   * The redis database to connect to
   */
  @PropertyDefinition
  private Integer _database;
  /**
   * The initial value to use for the simulation date - this may be modified later during runtime.
   */
  @PropertyDefinition
  private LocalDate _simulationDate;
  /**
   * The flag determining whether the component should be published by REST (default true).
   */
  @PropertyDefinition
  private boolean _publishRest = true;

  //-------------------------------------------------------------------------
  @Override
  public void init(final ComponentRepository repo, final LinkedHashMap<String, String> configuration) throws Exception {
    final JedisPool jedisPool = getRedisConnector().getJedisPool();
    final RedisSimulationSeriesSource  instance = new RedisSimulationSeriesSource(jedisPool, getRedisPrefix());
    if (_simulationDate != null) {
      instance.setCurrentSimulationExecutionDate(_simulationDate);
    }
    final HistoricalTimeSeriesResolver resolver = new RedisSimulationSeriesResolver(instance);

    final ComponentInfo infoResolver = new ComponentInfo(HistoricalTimeSeriesResolver.class, getClassifier());
    infoResolver.addAttribute(ComponentInfoAttributes.LEVEL, 1);
    if (isPublishRest()) {
      infoResolver.addAttribute(ComponentInfoAttributes.REMOTE_CLIENT_JAVA, RemoteHistoricalTimeSeriesResolver.class);
    }
    repo.registerComponent(infoResolver, resolver);
    final ComponentInfo infoSource = new ComponentInfo(HistoricalTimeSeriesSource.class, getClassifier());
    infoSource.addAttribute(ComponentInfoAttributes.LEVEL, 1);
    if (isPublishRest()) {
      infoSource.addAttribute(ComponentInfoAttributes.REMOTE_CLIENT_JAVA, RemoteHistoricalTimeSeriesSource.class);
    }
    repo.registerComponent(infoSource, instance);
    final ComponentInfo infoRedis = new ComponentInfo(RedisSimulationSeriesSource.class, getClassifier());
    infoRedis.addAttribute(ComponentInfoAttributes.LEVEL, 1);
    repo.registerComponent(infoRedis, instance);

    // Is caching needed? assume no for now
    if (_publishRest) {
      repo.getRestComponents().publish(infoResolver, new DataHistoricalTimeSeriesResolverResource(resolver, OpenGammaFudgeContext.getInstance()));
      repo.getRestComponents().publish(infoSource, new DataHistoricalTimeSeriesSourceResource(instance));
    }
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code RedisSimulationSeriesSourceComponentFactory}.
   * @return the meta-bean, not null
   */
  public static RedisSimulationSeriesSourceComponentFactory.Meta meta() {
    return RedisSimulationSeriesSourceComponentFactory.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(RedisSimulationSeriesSourceComponentFactory.Meta.INSTANCE);
  }

  @Override
  public RedisSimulationSeriesSourceComponentFactory.Meta metaBean() {
    return RedisSimulationSeriesSourceComponentFactory.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the classifier that the factory should publish under.
   * @return the value of the property, not null
   */
  public String getClassifier() {
    return _classifier;
  }

  /**
   * Sets the classifier that the factory should publish under.
   * @param classifier  the new value of the property, not null
   */
  public void setClassifier(String classifier) {
    JodaBeanUtils.notNull(classifier, "classifier");
    this._classifier = classifier;
  }

  /**
   * Gets the the {@code classifier} property.
   * @return the property, not null
   */
  public final Property<String> classifier() {
    return metaBean().classifier().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets connector to the Redis server.
   * @return the value of the property
   */
  public RedisConnector getRedisConnector() {
    return _redisConnector;
  }

  /**
   * Sets connector to the Redis server.
   * @param redisConnector  the new value of the property
   */
  public void setRedisConnector(RedisConnector redisConnector) {
    this._redisConnector = redisConnector;
  }

  /**
   * Gets the the {@code redisConnector} property.
   * @return the property, not null
   */
  public final Property<RedisConnector> redisConnector() {
    return metaBean().redisConnector().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets prefix to append to redis keys when stored
   * @return the value of the property
   */
  public String getRedisPrefix() {
    return _redisPrefix;
  }

  /**
   * Sets prefix to append to redis keys when stored
   * @param redisPrefix  the new value of the property
   */
  public void setRedisPrefix(String redisPrefix) {
    this._redisPrefix = redisPrefix;
  }

  /**
   * Gets the the {@code redisPrefix} property.
   * @return the property, not null
   */
  public final Property<String> redisPrefix() {
    return metaBean().redisPrefix().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the redis database to connect to
   * @return the value of the property
   */
  public Integer getDatabase() {
    return _database;
  }

  /**
   * Sets the redis database to connect to
   * @param database  the new value of the property
   */
  public void setDatabase(Integer database) {
    this._database = database;
  }

  /**
   * Gets the the {@code database} property.
   * @return the property, not null
   */
  public final Property<Integer> database() {
    return metaBean().database().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the initial value to use for the simulation date - this may be modified later during runtime.
   * @return the value of the property
   */
  public LocalDate getSimulationDate() {
    return _simulationDate;
  }

  /**
   * Sets the initial value to use for the simulation date - this may be modified later during runtime.
   * @param simulationDate  the new value of the property
   */
  public void setSimulationDate(LocalDate simulationDate) {
    this._simulationDate = simulationDate;
  }

  /**
   * Gets the the {@code simulationDate} property.
   * @return the property, not null
   */
  public final Property<LocalDate> simulationDate() {
    return metaBean().simulationDate().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the flag determining whether the component should be published by REST (default true).
   * @return the value of the property
   */
  public boolean isPublishRest() {
    return _publishRest;
  }

  /**
   * Sets the flag determining whether the component should be published by REST (default true).
   * @param publishRest  the new value of the property
   */
  public void setPublishRest(boolean publishRest) {
    this._publishRest = publishRest;
  }

  /**
   * Gets the the {@code publishRest} property.
   * @return the property, not null
   */
  public final Property<Boolean> publishRest() {
    return metaBean().publishRest().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public RedisSimulationSeriesSourceComponentFactory clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      RedisSimulationSeriesSourceComponentFactory other = (RedisSimulationSeriesSourceComponentFactory) obj;
      return JodaBeanUtils.equal(getClassifier(), other.getClassifier()) &&
          JodaBeanUtils.equal(getRedisConnector(), other.getRedisConnector()) &&
          JodaBeanUtils.equal(getRedisPrefix(), other.getRedisPrefix()) &&
          JodaBeanUtils.equal(getDatabase(), other.getDatabase()) &&
          JodaBeanUtils.equal(getSimulationDate(), other.getSimulationDate()) &&
          (isPublishRest() == other.isPublishRest()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = hash * 31 + JodaBeanUtils.hashCode(getClassifier());
    hash = hash * 31 + JodaBeanUtils.hashCode(getRedisConnector());
    hash = hash * 31 + JodaBeanUtils.hashCode(getRedisPrefix());
    hash = hash * 31 + JodaBeanUtils.hashCode(getDatabase());
    hash = hash * 31 + JodaBeanUtils.hashCode(getSimulationDate());
    hash = hash * 31 + JodaBeanUtils.hashCode(isPublishRest());
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(224);
    buf.append("RedisSimulationSeriesSourceComponentFactory{");
    int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  @Override
  protected void toString(StringBuilder buf) {
    super.toString(buf);
    buf.append("classifier").append('=').append(JodaBeanUtils.toString(getClassifier())).append(',').append(' ');
    buf.append("redisConnector").append('=').append(JodaBeanUtils.toString(getRedisConnector())).append(',').append(' ');
    buf.append("redisPrefix").append('=').append(JodaBeanUtils.toString(getRedisPrefix())).append(',').append(' ');
    buf.append("database").append('=').append(JodaBeanUtils.toString(getDatabase())).append(',').append(' ');
    buf.append("simulationDate").append('=').append(JodaBeanUtils.toString(getSimulationDate())).append(',').append(' ');
    buf.append("publishRest").append('=').append(JodaBeanUtils.toString(isPublishRest())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code RedisSimulationSeriesSourceComponentFactory}.
   */
  public static class Meta extends AbstractComponentFactory.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code classifier} property.
     */
    private final MetaProperty<String> _classifier = DirectMetaProperty.ofReadWrite(
        this, "classifier", RedisSimulationSeriesSourceComponentFactory.class, String.class);
    /**
     * The meta-property for the {@code redisConnector} property.
     */
    private final MetaProperty<RedisConnector> _redisConnector = DirectMetaProperty.ofReadWrite(
        this, "redisConnector", RedisSimulationSeriesSourceComponentFactory.class, RedisConnector.class);
    /**
     * The meta-property for the {@code redisPrefix} property.
     */
    private final MetaProperty<String> _redisPrefix = DirectMetaProperty.ofReadWrite(
        this, "redisPrefix", RedisSimulationSeriesSourceComponentFactory.class, String.class);
    /**
     * The meta-property for the {@code database} property.
     */
    private final MetaProperty<Integer> _database = DirectMetaProperty.ofReadWrite(
        this, "database", RedisSimulationSeriesSourceComponentFactory.class, Integer.class);
    /**
     * The meta-property for the {@code simulationDate} property.
     */
    private final MetaProperty<LocalDate> _simulationDate = DirectMetaProperty.ofReadWrite(
        this, "simulationDate", RedisSimulationSeriesSourceComponentFactory.class, LocalDate.class);
    /**
     * The meta-property for the {@code publishRest} property.
     */
    private final MetaProperty<Boolean> _publishRest = DirectMetaProperty.ofReadWrite(
        this, "publishRest", RedisSimulationSeriesSourceComponentFactory.class, Boolean.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "classifier",
        "redisConnector",
        "redisPrefix",
        "database",
        "simulationDate",
        "publishRest");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          return _classifier;
        case -745461486:  // redisConnector
          return _redisConnector;
        case -2024915987:  // redisPrefix
          return _redisPrefix;
        case 1789464955:  // database
          return _database;
        case 1652633173:  // simulationDate
          return _simulationDate;
        case -614707837:  // publishRest
          return _publishRest;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends RedisSimulationSeriesSourceComponentFactory> builder() {
      return new DirectBeanBuilder<RedisSimulationSeriesSourceComponentFactory>(new RedisSimulationSeriesSourceComponentFactory());
    }

    @Override
    public Class<? extends RedisSimulationSeriesSourceComponentFactory> beanType() {
      return RedisSimulationSeriesSourceComponentFactory.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code classifier} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> classifier() {
      return _classifier;
    }

    /**
     * The meta-property for the {@code redisConnector} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<RedisConnector> redisConnector() {
      return _redisConnector;
    }

    /**
     * The meta-property for the {@code redisPrefix} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> redisPrefix() {
      return _redisPrefix;
    }

    /**
     * The meta-property for the {@code database} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Integer> database() {
      return _database;
    }

    /**
     * The meta-property for the {@code simulationDate} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<LocalDate> simulationDate() {
      return _simulationDate;
    }

    /**
     * The meta-property for the {@code publishRest} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Boolean> publishRest() {
      return _publishRest;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          return ((RedisSimulationSeriesSourceComponentFactory) bean).getClassifier();
        case -745461486:  // redisConnector
          return ((RedisSimulationSeriesSourceComponentFactory) bean).getRedisConnector();
        case -2024915987:  // redisPrefix
          return ((RedisSimulationSeriesSourceComponentFactory) bean).getRedisPrefix();
        case 1789464955:  // database
          return ((RedisSimulationSeriesSourceComponentFactory) bean).getDatabase();
        case 1652633173:  // simulationDate
          return ((RedisSimulationSeriesSourceComponentFactory) bean).getSimulationDate();
        case -614707837:  // publishRest
          return ((RedisSimulationSeriesSourceComponentFactory) bean).isPublishRest();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          ((RedisSimulationSeriesSourceComponentFactory) bean).setClassifier((String) newValue);
          return;
        case -745461486:  // redisConnector
          ((RedisSimulationSeriesSourceComponentFactory) bean).setRedisConnector((RedisConnector) newValue);
          return;
        case -2024915987:  // redisPrefix
          ((RedisSimulationSeriesSourceComponentFactory) bean).setRedisPrefix((String) newValue);
          return;
        case 1789464955:  // database
          ((RedisSimulationSeriesSourceComponentFactory) bean).setDatabase((Integer) newValue);
          return;
        case 1652633173:  // simulationDate
          ((RedisSimulationSeriesSourceComponentFactory) bean).setSimulationDate((LocalDate) newValue);
          return;
        case -614707837:  // publishRest
          ((RedisSimulationSeriesSourceComponentFactory) bean).setPublishRest((Boolean) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((RedisSimulationSeriesSourceComponentFactory) bean)._classifier, "classifier");
      super.validate(bean);
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

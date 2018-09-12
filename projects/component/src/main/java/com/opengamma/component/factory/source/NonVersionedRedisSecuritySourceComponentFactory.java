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

import com.opengamma.component.ComponentInfo;
import com.opengamma.component.ComponentRepository;
import com.opengamma.component.factory.AbstractComponentFactory;
import com.opengamma.component.factory.ComponentInfoAttributes;
import com.opengamma.core.security.SecuritySource;
import com.opengamma.core.security.impl.DataSecuritySourceResource;
import com.opengamma.core.security.impl.NonVersionedEHCachingSecuritySource;
import com.opengamma.core.security.impl.NonVersionedRedisSecuritySource;
import com.opengamma.core.security.impl.RemoteSecuritySource;
import com.opengamma.util.redis.RedisConnector;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

/**
 * Component factory providing {@link NonVersionedRedisSecuritySource}.
 */
@BeanDefinition
public class NonVersionedRedisSecuritySourceComponentFactory extends AbstractComponentFactory {

  /**
   * The classifier that the factory should publish the raw Redis source under.
   */
  @PropertyDefinition(validate = "notNull")
  private String _classifier;
  /**
   * Connector to the underlying Redis instance to use.
   */
  @PropertyDefinition(validate = "notNull")
  private RedisConnector _redisConnector;
  /**
   * If set (optional) prefixes all Redis keys with the specified value.
   */
  @PropertyDefinition(validate = "notNull")
  private String _redisPrefix = "";
  /**
   * The flag determining whether the component should be published by REST (default true).
   */
  @PropertyDefinition
  private boolean _publishRest = true;
  /**
   * The classifier that the factory should publish a caching source under.
   */
  @PropertyDefinition
  private String _cachingClassifier;
  /**
   * The cache manager to use if an LRU cache is enabled.
   */
  @PropertyDefinition
  private CacheManager _cacheManager;
  /**
   * The number of elements for an LRU cache.
   */
  @PropertyDefinition
  private int _lruCacheElements;

  @Override
  public void init(final ComponentRepository repo, final LinkedHashMap<String, String> configuration) throws Exception {
    final NonVersionedRedisSecuritySource source = new NonVersionedRedisSecuritySource(getRedisConnector().getJedisPool(), getRedisPrefix());
    SecuritySource rawSource = source;

    if (getCacheManager() != null && getLruCacheElements() > 0) {
      final Cache cache =
          new Cache("NonVersionedRedisSecuritySource", getLruCacheElements(), MemoryStoreEvictionPolicy.LRU, false, null, true, -1L, -1L, false, -1L, null);
      getCacheManager().addCache(cache);
      rawSource = new NonVersionedEHCachingSecuritySource(source, cache);
    }

    final String rawClassifier = getCachingClassifier() != null ? getCachingClassifier() : getClassifier();

    final ComponentInfo sourceInfo = new ComponentInfo(SecuritySource.class, rawClassifier);
    sourceInfo.addAttribute(ComponentInfoAttributes.LEVEL, 1);
    if (isPublishRest()) {
      sourceInfo.addAttribute(ComponentInfoAttributes.REMOTE_CLIENT_JAVA, RemoteSecuritySource.class);
    }
    repo.registerComponent(sourceInfo, rawSource);

    final ComponentInfo redisInfo = new ComponentInfo(NonVersionedRedisSecuritySource.class, getClassifier());
    redisInfo.addAttribute(ComponentInfoAttributes.LEVEL, 1);
    repo.registerComponent(redisInfo, source);

    if (isPublishRest()) {
      repo.getRestComponents().publish(sourceInfo, new DataSecuritySourceResource(rawSource));
    }
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code NonVersionedRedisSecuritySourceComponentFactory}.
   * @return the meta-bean, not null
   */
  public static NonVersionedRedisSecuritySourceComponentFactory.Meta meta() {
    return NonVersionedRedisSecuritySourceComponentFactory.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(NonVersionedRedisSecuritySourceComponentFactory.Meta.INSTANCE);
  }

  @Override
  public NonVersionedRedisSecuritySourceComponentFactory.Meta metaBean() {
    return NonVersionedRedisSecuritySourceComponentFactory.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the classifier that the factory should publish the raw Redis source under.
   * @return the value of the property, not null
   */
  public String getClassifier() {
    return _classifier;
  }

  /**
   * Sets the classifier that the factory should publish the raw Redis source under.
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
   * Gets connector to the underlying Redis instance to use.
   * @return the value of the property, not null
   */
  public RedisConnector getRedisConnector() {
    return _redisConnector;
  }

  /**
   * Sets connector to the underlying Redis instance to use.
   * @param redisConnector  the new value of the property, not null
   */
  public void setRedisConnector(RedisConnector redisConnector) {
    JodaBeanUtils.notNull(redisConnector, "redisConnector");
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
   * Gets if set (optional) prefixes all Redis keys with the specified value.
   * @return the value of the property, not null
   */
  public String getRedisPrefix() {
    return _redisPrefix;
  }

  /**
   * Sets if set (optional) prefixes all Redis keys with the specified value.
   * @param redisPrefix  the new value of the property, not null
   */
  public void setRedisPrefix(String redisPrefix) {
    JodaBeanUtils.notNull(redisPrefix, "redisPrefix");
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
  /**
   * Gets the classifier that the factory should publish a caching source under.
   * @return the value of the property
   */
  public String getCachingClassifier() {
    return _cachingClassifier;
  }

  /**
   * Sets the classifier that the factory should publish a caching source under.
   * @param cachingClassifier  the new value of the property
   */
  public void setCachingClassifier(String cachingClassifier) {
    this._cachingClassifier = cachingClassifier;
  }

  /**
   * Gets the the {@code cachingClassifier} property.
   * @return the property, not null
   */
  public final Property<String> cachingClassifier() {
    return metaBean().cachingClassifier().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the cache manager to use if an LRU cache is enabled.
   * @return the value of the property
   */
  public CacheManager getCacheManager() {
    return _cacheManager;
  }

  /**
   * Sets the cache manager to use if an LRU cache is enabled.
   * @param cacheManager  the new value of the property
   */
  public void setCacheManager(CacheManager cacheManager) {
    this._cacheManager = cacheManager;
  }

  /**
   * Gets the the {@code cacheManager} property.
   * @return the property, not null
   */
  public final Property<CacheManager> cacheManager() {
    return metaBean().cacheManager().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the number of elements for an LRU cache.
   * @return the value of the property
   */
  public int getLruCacheElements() {
    return _lruCacheElements;
  }

  /**
   * Sets the number of elements for an LRU cache.
   * @param lruCacheElements  the new value of the property
   */
  public void setLruCacheElements(int lruCacheElements) {
    this._lruCacheElements = lruCacheElements;
  }

  /**
   * Gets the the {@code lruCacheElements} property.
   * @return the property, not null
   */
  public final Property<Integer> lruCacheElements() {
    return metaBean().lruCacheElements().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public NonVersionedRedisSecuritySourceComponentFactory clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      NonVersionedRedisSecuritySourceComponentFactory other = (NonVersionedRedisSecuritySourceComponentFactory) obj;
      return JodaBeanUtils.equal(getClassifier(), other.getClassifier()) &&
          JodaBeanUtils.equal(getRedisConnector(), other.getRedisConnector()) &&
          JodaBeanUtils.equal(getRedisPrefix(), other.getRedisPrefix()) &&
          (isPublishRest() == other.isPublishRest()) &&
          JodaBeanUtils.equal(getCachingClassifier(), other.getCachingClassifier()) &&
          JodaBeanUtils.equal(getCacheManager(), other.getCacheManager()) &&
          (getLruCacheElements() == other.getLruCacheElements()) &&
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
    hash = hash * 31 + JodaBeanUtils.hashCode(isPublishRest());
    hash = hash * 31 + JodaBeanUtils.hashCode(getCachingClassifier());
    hash = hash * 31 + JodaBeanUtils.hashCode(getCacheManager());
    hash = hash * 31 + JodaBeanUtils.hashCode(getLruCacheElements());
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(256);
    buf.append("NonVersionedRedisSecuritySourceComponentFactory{");
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
    buf.append("publishRest").append('=').append(JodaBeanUtils.toString(isPublishRest())).append(',').append(' ');
    buf.append("cachingClassifier").append('=').append(JodaBeanUtils.toString(getCachingClassifier())).append(',').append(' ');
    buf.append("cacheManager").append('=').append(JodaBeanUtils.toString(getCacheManager())).append(',').append(' ');
    buf.append("lruCacheElements").append('=').append(JodaBeanUtils.toString(getLruCacheElements())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code NonVersionedRedisSecuritySourceComponentFactory}.
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
        this, "classifier", NonVersionedRedisSecuritySourceComponentFactory.class, String.class);
    /**
     * The meta-property for the {@code redisConnector} property.
     */
    private final MetaProperty<RedisConnector> _redisConnector = DirectMetaProperty.ofReadWrite(
        this, "redisConnector", NonVersionedRedisSecuritySourceComponentFactory.class, RedisConnector.class);
    /**
     * The meta-property for the {@code redisPrefix} property.
     */
    private final MetaProperty<String> _redisPrefix = DirectMetaProperty.ofReadWrite(
        this, "redisPrefix", NonVersionedRedisSecuritySourceComponentFactory.class, String.class);
    /**
     * The meta-property for the {@code publishRest} property.
     */
    private final MetaProperty<Boolean> _publishRest = DirectMetaProperty.ofReadWrite(
        this, "publishRest", NonVersionedRedisSecuritySourceComponentFactory.class, Boolean.TYPE);
    /**
     * The meta-property for the {@code cachingClassifier} property.
     */
    private final MetaProperty<String> _cachingClassifier = DirectMetaProperty.ofReadWrite(
        this, "cachingClassifier", NonVersionedRedisSecuritySourceComponentFactory.class, String.class);
    /**
     * The meta-property for the {@code cacheManager} property.
     */
    private final MetaProperty<CacheManager> _cacheManager = DirectMetaProperty.ofReadWrite(
        this, "cacheManager", NonVersionedRedisSecuritySourceComponentFactory.class, CacheManager.class);
    /**
     * The meta-property for the {@code lruCacheElements} property.
     */
    private final MetaProperty<Integer> _lruCacheElements = DirectMetaProperty.ofReadWrite(
        this, "lruCacheElements", NonVersionedRedisSecuritySourceComponentFactory.class, Integer.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "classifier",
        "redisConnector",
        "redisPrefix",
        "publishRest",
        "cachingClassifier",
        "cacheManager",
        "lruCacheElements");

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
        case -614707837:  // publishRest
          return _publishRest;
        case -1676966080:  // cachingClassifier
          return _cachingClassifier;
        case -1452875317:  // cacheManager
          return _cacheManager;
        case 316175146:  // lruCacheElements
          return _lruCacheElements;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends NonVersionedRedisSecuritySourceComponentFactory> builder() {
      return new DirectBeanBuilder<NonVersionedRedisSecuritySourceComponentFactory>(new NonVersionedRedisSecuritySourceComponentFactory());
    }

    @Override
    public Class<? extends NonVersionedRedisSecuritySourceComponentFactory> beanType() {
      return NonVersionedRedisSecuritySourceComponentFactory.class;
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
     * The meta-property for the {@code publishRest} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Boolean> publishRest() {
      return _publishRest;
    }

    /**
     * The meta-property for the {@code cachingClassifier} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> cachingClassifier() {
      return _cachingClassifier;
    }

    /**
     * The meta-property for the {@code cacheManager} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<CacheManager> cacheManager() {
      return _cacheManager;
    }

    /**
     * The meta-property for the {@code lruCacheElements} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Integer> lruCacheElements() {
      return _lruCacheElements;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          return ((NonVersionedRedisSecuritySourceComponentFactory) bean).getClassifier();
        case -745461486:  // redisConnector
          return ((NonVersionedRedisSecuritySourceComponentFactory) bean).getRedisConnector();
        case -2024915987:  // redisPrefix
          return ((NonVersionedRedisSecuritySourceComponentFactory) bean).getRedisPrefix();
        case -614707837:  // publishRest
          return ((NonVersionedRedisSecuritySourceComponentFactory) bean).isPublishRest();
        case -1676966080:  // cachingClassifier
          return ((NonVersionedRedisSecuritySourceComponentFactory) bean).getCachingClassifier();
        case -1452875317:  // cacheManager
          return ((NonVersionedRedisSecuritySourceComponentFactory) bean).getCacheManager();
        case 316175146:  // lruCacheElements
          return ((NonVersionedRedisSecuritySourceComponentFactory) bean).getLruCacheElements();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          ((NonVersionedRedisSecuritySourceComponentFactory) bean).setClassifier((String) newValue);
          return;
        case -745461486:  // redisConnector
          ((NonVersionedRedisSecuritySourceComponentFactory) bean).setRedisConnector((RedisConnector) newValue);
          return;
        case -2024915987:  // redisPrefix
          ((NonVersionedRedisSecuritySourceComponentFactory) bean).setRedisPrefix((String) newValue);
          return;
        case -614707837:  // publishRest
          ((NonVersionedRedisSecuritySourceComponentFactory) bean).setPublishRest((Boolean) newValue);
          return;
        case -1676966080:  // cachingClassifier
          ((NonVersionedRedisSecuritySourceComponentFactory) bean).setCachingClassifier((String) newValue);
          return;
        case -1452875317:  // cacheManager
          ((NonVersionedRedisSecuritySourceComponentFactory) bean).setCacheManager((CacheManager) newValue);
          return;
        case 316175146:  // lruCacheElements
          ((NonVersionedRedisSecuritySourceComponentFactory) bean).setLruCacheElements((Integer) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((NonVersionedRedisSecuritySourceComponentFactory) bean)._classifier, "classifier");
      JodaBeanUtils.notNull(((NonVersionedRedisSecuritySourceComponentFactory) bean)._redisConnector, "redisConnector");
      JodaBeanUtils.notNull(((NonVersionedRedisSecuritySourceComponentFactory) bean)._redisPrefix, "redisPrefix");
      super.validate(bean);
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

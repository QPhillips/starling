/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.master.security;

import java.util.Map;
import java.util.Map.Entry;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBean;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.google.common.collect.Maps;
import com.opengamma.core.security.Security;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.id.ObjectId;
import com.opengamma.id.UniqueId;
import com.opengamma.util.PublicSPI;

/**
 * Result from loading one or more securities.
 * <p>
 * This class is mutable and not thread-safe.
 */
@PublicSPI
@BeanDefinition
public class SecurityLoaderResult extends DirectBean {

  /**
   * The unique IDs of the securities that were obtained.
   * The bundle is the original search key, it is not updated with additional external IDs.
   */
  @PropertyDefinition
  private final Map<ExternalIdBundle, UniqueId> _resultMap = Maps.newHashMap();
  /**
   * The loaded securities, keyed by unique ID.
   * This map is optional, and is only returned if the flag in the request is set to true.
   */
  @PropertyDefinition
  private final Map<UniqueId, Security> _securityMap = Maps.newHashMap();

  /**
   * Creates an instance.
   */
  public SecurityLoaderResult() {
  }

  /**
   * Creates an instance.
   *
   * @param result  the map of results, not null
   * @param fullResults  whether to store the full results
   */
  public SecurityLoaderResult(final Map<ExternalIdBundle, Security> result, final boolean fullResults) {
    for (final Entry<ExternalIdBundle, Security> entry : result.entrySet()) {
      final ExternalIdBundle bundle = entry.getKey();
      final Security security = entry.getValue();
      if (security == null) {
        getResultMap().put(bundle, null);
      } else {
        getResultMap().put(bundle, security.getUniqueId());
        if (fullResults) {
          getSecurityMap().put(security.getUniqueId(), security);
        }
      }
    }
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the result map in terms of {@code ObjectId}.
   *
   * @return the map of object identifiers, not null
   */
  public Map<ExternalIdBundle, ObjectId> getResultMapAsObjectId() {
    final Map<ExternalIdBundle, ObjectId> resultMap = Maps.newHashMapWithExpectedSize(getResultMap().size());
    for (final Entry<ExternalIdBundle, UniqueId> entry : getResultMap().entrySet()) {
      final UniqueId uid = entry.getValue();
      if (uid == null) {
        resultMap.put(entry.getKey(), null);
      } else {
        resultMap.put(entry.getKey(), uid.getObjectId());
      }
    }
    return resultMap;
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code SecurityLoaderResult}.
   * @return the meta-bean, not null
   */
  public static SecurityLoaderResult.Meta meta() {
    return SecurityLoaderResult.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(SecurityLoaderResult.Meta.INSTANCE);
  }

  @Override
  public SecurityLoaderResult.Meta metaBean() {
    return SecurityLoaderResult.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the unique IDs of the securities that were obtained.
   * The bundle is the original search key, it is not updated with additional external IDs.
   * @return the value of the property, not null
   */
  public Map<ExternalIdBundle, UniqueId> getResultMap() {
    return _resultMap;
  }

  /**
   * Sets the unique IDs of the securities that were obtained.
   * The bundle is the original search key, it is not updated with additional external IDs.
   * @param resultMap  the new value of the property, not null
   */
  public void setResultMap(Map<ExternalIdBundle, UniqueId> resultMap) {
    JodaBeanUtils.notNull(resultMap, "resultMap");
    this._resultMap.clear();
    this._resultMap.putAll(resultMap);
  }

  /**
   * Gets the the {@code resultMap} property.
   * The bundle is the original search key, it is not updated with additional external IDs.
   * @return the property, not null
   */
  public final Property<Map<ExternalIdBundle, UniqueId>> resultMap() {
    return metaBean().resultMap().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the loaded securities, keyed by unique ID.
   * This map is optional, and is only returned if the flag in the request is set to true.
   * @return the value of the property, not null
   */
  public Map<UniqueId, Security> getSecurityMap() {
    return _securityMap;
  }

  /**
   * Sets the loaded securities, keyed by unique ID.
   * This map is optional, and is only returned if the flag in the request is set to true.
   * @param securityMap  the new value of the property, not null
   */
  public void setSecurityMap(Map<UniqueId, Security> securityMap) {
    JodaBeanUtils.notNull(securityMap, "securityMap");
    this._securityMap.clear();
    this._securityMap.putAll(securityMap);
  }

  /**
   * Gets the the {@code securityMap} property.
   * This map is optional, and is only returned if the flag in the request is set to true.
   * @return the property, not null
   */
  public final Property<Map<UniqueId, Security>> securityMap() {
    return metaBean().securityMap().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public SecurityLoaderResult clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      SecurityLoaderResult other = (SecurityLoaderResult) obj;
      return JodaBeanUtils.equal(getResultMap(), other.getResultMap()) &&
          JodaBeanUtils.equal(getSecurityMap(), other.getSecurityMap());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getResultMap());
    hash = hash * 31 + JodaBeanUtils.hashCode(getSecurityMap());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("SecurityLoaderResult{");
    int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  protected void toString(StringBuilder buf) {
    buf.append("resultMap").append('=').append(JodaBeanUtils.toString(getResultMap())).append(',').append(' ');
    buf.append("securityMap").append('=').append(JodaBeanUtils.toString(getSecurityMap())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code SecurityLoaderResult}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code resultMap} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<Map<ExternalIdBundle, UniqueId>> _resultMap = DirectMetaProperty.ofReadWrite(
        this, "resultMap", SecurityLoaderResult.class, (Class) Map.class);
    /**
     * The meta-property for the {@code securityMap} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<Map<UniqueId, Security>> _securityMap = DirectMetaProperty.ofReadWrite(
        this, "securityMap", SecurityLoaderResult.class, (Class) Map.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "resultMap",
        "securityMap");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1819569153:  // resultMap
          return _resultMap;
        case 1550085628:  // securityMap
          return _securityMap;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends SecurityLoaderResult> builder() {
      return new DirectBeanBuilder<SecurityLoaderResult>(new SecurityLoaderResult());
    }

    @Override
    public Class<? extends SecurityLoaderResult> beanType() {
      return SecurityLoaderResult.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code resultMap} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Map<ExternalIdBundle, UniqueId>> resultMap() {
      return _resultMap;
    }

    /**
     * The meta-property for the {@code securityMap} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Map<UniqueId, Security>> securityMap() {
      return _securityMap;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -1819569153:  // resultMap
          return ((SecurityLoaderResult) bean).getResultMap();
        case 1550085628:  // securityMap
          return ((SecurityLoaderResult) bean).getSecurityMap();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -1819569153:  // resultMap
          ((SecurityLoaderResult) bean).setResultMap((Map<ExternalIdBundle, UniqueId>) newValue);
          return;
        case 1550085628:  // securityMap
          ((SecurityLoaderResult) bean).setSecurityMap((Map<UniqueId, Security>) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((SecurityLoaderResult) bean)._resultMap, "resultMap");
      JodaBeanUtils.notNull(((SecurityLoaderResult) bean)._securityMap, "securityMap");
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

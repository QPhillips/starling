/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.provider.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.opengamma.id.ExternalId;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.PublicSPI;

/**
 * Request to get one or more securities.
 * <p>
 * This class is mutable and not thread-safe.
 */
@PublicSPI
@BeanDefinition
public class SecurityProviderRequest extends DirectBean {

  /**
   * The set of security external identifiers to get.
   */
  @PropertyDefinition
  private final Set<ExternalIdBundle> ExternalIdBundles = Sets.newHashSet();
  /**
   * The data source to use, null to be smart.
   */
  @PropertyDefinition
  private String _dataSource;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance to get a single security.
   *
   * @param externalIdBundle  the identifier bundle, not null
   * @param dataSource  the data source, null to be smart
   * @return the request, not null
   */
  public static SecurityProviderRequest createGet(
      final ExternalIdBundle externalIdBundle, final String dataSource) {
    final SecurityProviderRequest request = new SecurityProviderRequest();
    request.addExternalIds(externalIdBundle);
    request.setDataSource(dataSource);
    return request;
  }

  /**
   * Obtains an instance to get multiple securities.
   *
   * @param externalIdBundles  the identifier bundle, not null
   * @param dataSource  the data source, null to be smart
   * @return the request, not null
   */
  public static SecurityProviderRequest createGet(
      final Iterable<ExternalIdBundle> externalIdBundles, final String dataSource) {
    final SecurityProviderRequest request = new SecurityProviderRequest();
    request.addExternalIds(externalIdBundles);
    request.setDataSource(dataSource);
    return request;
  }

  //-------------------------------------------------------------------------
  /**
   * Creates an instance.
   */
  protected SecurityProviderRequest() {
  }

  //-------------------------------------------------------------------------
  /**
   * Adds an array of security external identifiers to the collection to load.
   *
   * @param externalIds  the security identifiers to load, not null
   */
  public void addExternalIds(final ExternalId... externalIds) {
    ArgumentChecker.notNull(externalIds, "externalIds");
    final List<ExternalIdBundle> list = new ArrayList<>();
    for (final ExternalId externalId : externalIds) {
      list.add(ExternalIdBundle.of(externalId));
    }
    getExternalIdBundles().addAll(list);
  }

  /**
   * Adds an array of security external identifiers to the collection to load.
   *
   * @param externalIdBundles  the security identifiers to load, not null
   */
  public void addExternalIds(final ExternalIdBundle... externalIdBundles) {
    ArgumentChecker.notNull(externalIdBundles, "externalIdBundles");
    getExternalIdBundles().addAll(Arrays.asList(externalIdBundles));
  }

  /**
   * Adds a collection of security external identifiers to the collection to load.
   *
   * @param externalIdBundles  the security identifiers to load, not null
   */
  public void addExternalIds(final Iterable<ExternalIdBundle> externalIdBundles) {
    ArgumentChecker.notNull(externalIdBundles, "externalIdBundles");
    Iterables.addAll(getExternalIdBundles(), externalIdBundles);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code SecurityProviderRequest}.
   * @return the meta-bean, not null
   */
  public static SecurityProviderRequest.Meta meta() {
    return SecurityProviderRequest.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(SecurityProviderRequest.Meta.INSTANCE);
  }

  @Override
  public SecurityProviderRequest.Meta metaBean() {
    return SecurityProviderRequest.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the set of security external identifiers to get.
   * @return the value of the property, not null
   */
  public Set<ExternalIdBundle> getExternalIdBundles() {
    return ExternalIdBundles;
  }

  /**
   * Sets the set of security external identifiers to get.
   * @param ExternalIdBundles  the new value of the property, not null
   */
  public void setExternalIdBundles(Set<ExternalIdBundle> ExternalIdBundles) {
    JodaBeanUtils.notNull(ExternalIdBundles, "ExternalIdBundles");
    this.ExternalIdBundles.clear();
    this.ExternalIdBundles.addAll(ExternalIdBundles);
  }

  /**
   * Gets the the {@code ExternalIdBundles} property.
   * @return the property, not null
   */
  public final Property<Set<ExternalIdBundle>> ExternalIdBundles() {
    return metaBean().ExternalIdBundles().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the data source to use, null to be smart.
   * @return the value of the property
   */
  public String getDataSource() {
    return _dataSource;
  }

  /**
   * Sets the data source to use, null to be smart.
   * @param dataSource  the new value of the property
   */
  public void setDataSource(String dataSource) {
    this._dataSource = dataSource;
  }

  /**
   * Gets the the {@code dataSource} property.
   * @return the property, not null
   */
  public final Property<String> dataSource() {
    return metaBean().dataSource().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public SecurityProviderRequest clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      SecurityProviderRequest other = (SecurityProviderRequest) obj;
      return JodaBeanUtils.equal(getExternalIdBundles(), other.getExternalIdBundles()) &&
          JodaBeanUtils.equal(getDataSource(), other.getDataSource());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getExternalIdBundles());
    hash = hash * 31 + JodaBeanUtils.hashCode(getDataSource());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("SecurityProviderRequest{");
    int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  protected void toString(StringBuilder buf) {
    buf.append("ExternalIdBundles").append('=').append(JodaBeanUtils.toString(getExternalIdBundles())).append(',').append(' ');
    buf.append("dataSource").append('=').append(JodaBeanUtils.toString(getDataSource())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code SecurityProviderRequest}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code ExternalIdBundles} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<Set<ExternalIdBundle>> _ExternalIdBundles = DirectMetaProperty.ofReadWrite(
        this, "ExternalIdBundles", SecurityProviderRequest.class, (Class) Set.class);
    /**
     * The meta-property for the {@code dataSource} property.
     */
    private final MetaProperty<String> _dataSource = DirectMetaProperty.ofReadWrite(
        this, "dataSource", SecurityProviderRequest.class, String.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "ExternalIdBundles",
        "dataSource");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1725982997:  // ExternalIdBundles
          return _ExternalIdBundles;
        case 1272470629:  // dataSource
          return _dataSource;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends SecurityProviderRequest> builder() {
      return new DirectBeanBuilder<SecurityProviderRequest>(new SecurityProviderRequest());
    }

    @Override
    public Class<? extends SecurityProviderRequest> beanType() {
      return SecurityProviderRequest.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code ExternalIdBundles} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Set<ExternalIdBundle>> ExternalIdBundles() {
      return _ExternalIdBundles;
    }

    /**
     * The meta-property for the {@code dataSource} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> dataSource() {
      return _dataSource;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -1725982997:  // ExternalIdBundles
          return ((SecurityProviderRequest) bean).getExternalIdBundles();
        case 1272470629:  // dataSource
          return ((SecurityProviderRequest) bean).getDataSource();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -1725982997:  // ExternalIdBundles
          ((SecurityProviderRequest) bean).setExternalIdBundles((Set<ExternalIdBundle>) newValue);
          return;
        case 1272470629:  // dataSource
          ((SecurityProviderRequest) bean).setDataSource((String) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((SecurityProviderRequest) bean).ExternalIdBundles, "ExternalIdBundles");
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

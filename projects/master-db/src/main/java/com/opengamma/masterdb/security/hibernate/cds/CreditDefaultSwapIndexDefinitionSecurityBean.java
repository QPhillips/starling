/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.masterdb.security.hibernate.cds;

import java.util.Map;
import java.util.Set;

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

import com.google.common.collect.Sets;
import com.opengamma.masterdb.security.hibernate.CDSIndexFamilyBean;
import com.opengamma.masterdb.security.hibernate.CurrencyBean;
import com.opengamma.masterdb.security.hibernate.SecurityBean;
import com.opengamma.masterdb.security.hibernate.TenorBean;

/**
 * A Hibernate bean representation of
 * {@link com.opengamma.financial.security.cds.CreditDefaultSwapIndexDefinitionSecurity}.
 */
@BeanDefinition
public class CreditDefaultSwapIndexDefinitionSecurityBean extends SecurityBean {

  @PropertyDefinition
  private String _name;
  @PropertyDefinition
  private String _version;
  @PropertyDefinition
  private String _series;
  @PropertyDefinition
  private CDSIndexFamilyBean _family;
  @PropertyDefinition
  private CurrencyBean _currency;
  @PropertyDefinition
  private Double _recoveryRate;
  @PropertyDefinition(set = "set", validate = "notNull")
  private Set<TenorBean> _tenors = Sets.newHashSet();
  @PropertyDefinition(set = "set", validate = "notNull")
  private Set<CDSIndexComponentBean> _components = Sets.newHashSet();

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code CreditDefaultSwapIndexDefinitionSecurityBean}.
   * @return the meta-bean, not null
   */
  public static CreditDefaultSwapIndexDefinitionSecurityBean.Meta meta() {
    return CreditDefaultSwapIndexDefinitionSecurityBean.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(CreditDefaultSwapIndexDefinitionSecurityBean.Meta.INSTANCE);
  }

  @Override
  public CreditDefaultSwapIndexDefinitionSecurityBean.Meta metaBean() {
    return CreditDefaultSwapIndexDefinitionSecurityBean.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the name.
   * @return the value of the property
   */
  public String getName() {
    return _name;
  }

  /**
   * Sets the name.
   * @param name  the new value of the property
   */
  public void setName(String name) {
    this._name = name;
  }

  /**
   * Gets the the {@code name} property.
   * @return the property, not null
   */
  public final Property<String> name() {
    return metaBean().name().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the version.
   * @return the value of the property
   */
  public String getVersion() {
    return _version;
  }

  /**
   * Sets the version.
   * @param version  the new value of the property
   */
  public void setVersion(String version) {
    this._version = version;
  }

  /**
   * Gets the the {@code version} property.
   * @return the property, not null
   */
  public final Property<String> version() {
    return metaBean().version().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the series.
   * @return the value of the property
   */
  public String getSeries() {
    return _series;
  }

  /**
   * Sets the series.
   * @param series  the new value of the property
   */
  public void setSeries(String series) {
    this._series = series;
  }

  /**
   * Gets the the {@code series} property.
   * @return the property, not null
   */
  public final Property<String> series() {
    return metaBean().series().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the family.
   * @return the value of the property
   */
  public CDSIndexFamilyBean getFamily() {
    return _family;
  }

  /**
   * Sets the family.
   * @param family  the new value of the property
   */
  public void setFamily(CDSIndexFamilyBean family) {
    this._family = family;
  }

  /**
   * Gets the the {@code family} property.
   * @return the property, not null
   */
  public final Property<CDSIndexFamilyBean> family() {
    return metaBean().family().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the currency.
   * @return the value of the property
   */
  public CurrencyBean getCurrency() {
    return _currency;
  }

  /**
   * Sets the currency.
   * @param currency  the new value of the property
   */
  public void setCurrency(CurrencyBean currency) {
    this._currency = currency;
  }

  /**
   * Gets the the {@code currency} property.
   * @return the property, not null
   */
  public final Property<CurrencyBean> currency() {
    return metaBean().currency().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the recoveryRate.
   * @return the value of the property
   */
  public Double getRecoveryRate() {
    return _recoveryRate;
  }

  /**
   * Sets the recoveryRate.
   * @param recoveryRate  the new value of the property
   */
  public void setRecoveryRate(Double recoveryRate) {
    this._recoveryRate = recoveryRate;
  }

  /**
   * Gets the the {@code recoveryRate} property.
   * @return the property, not null
   */
  public final Property<Double> recoveryRate() {
    return metaBean().recoveryRate().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the tenors.
   * @return the value of the property, not null
   */
  public Set<TenorBean> getTenors() {
    return _tenors;
  }

  /**
   * Sets the tenors.
   * @param tenors  the new value of the property, not null
   */
  public void setTenors(Set<TenorBean> tenors) {
    JodaBeanUtils.notNull(tenors, "tenors");
    this._tenors = tenors;
  }

  /**
   * Gets the the {@code tenors} property.
   * @return the property, not null
   */
  public final Property<Set<TenorBean>> tenors() {
    return metaBean().tenors().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the components.
   * @return the value of the property, not null
   */
  public Set<CDSIndexComponentBean> getComponents() {
    return _components;
  }

  /**
   * Sets the components.
   * @param components  the new value of the property, not null
   */
  public void setComponents(Set<CDSIndexComponentBean> components) {
    JodaBeanUtils.notNull(components, "components");
    this._components = components;
  }

  /**
   * Gets the the {@code components} property.
   * @return the property, not null
   */
  public final Property<Set<CDSIndexComponentBean>> components() {
    return metaBean().components().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public CreditDefaultSwapIndexDefinitionSecurityBean clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      CreditDefaultSwapIndexDefinitionSecurityBean other = (CreditDefaultSwapIndexDefinitionSecurityBean) obj;
      return JodaBeanUtils.equal(getName(), other.getName()) &&
          JodaBeanUtils.equal(getVersion(), other.getVersion()) &&
          JodaBeanUtils.equal(getSeries(), other.getSeries()) &&
          JodaBeanUtils.equal(getFamily(), other.getFamily()) &&
          JodaBeanUtils.equal(getCurrency(), other.getCurrency()) &&
          JodaBeanUtils.equal(getRecoveryRate(), other.getRecoveryRate()) &&
          JodaBeanUtils.equal(getTenors(), other.getTenors()) &&
          JodaBeanUtils.equal(getComponents(), other.getComponents()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = hash * 31 + JodaBeanUtils.hashCode(getName());
    hash = hash * 31 + JodaBeanUtils.hashCode(getVersion());
    hash = hash * 31 + JodaBeanUtils.hashCode(getSeries());
    hash = hash * 31 + JodaBeanUtils.hashCode(getFamily());
    hash = hash * 31 + JodaBeanUtils.hashCode(getCurrency());
    hash = hash * 31 + JodaBeanUtils.hashCode(getRecoveryRate());
    hash = hash * 31 + JodaBeanUtils.hashCode(getTenors());
    hash = hash * 31 + JodaBeanUtils.hashCode(getComponents());
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(288);
    buf.append("CreditDefaultSwapIndexDefinitionSecurityBean{");
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
    buf.append("name").append('=').append(JodaBeanUtils.toString(getName())).append(',').append(' ');
    buf.append("version").append('=').append(JodaBeanUtils.toString(getVersion())).append(',').append(' ');
    buf.append("series").append('=').append(JodaBeanUtils.toString(getSeries())).append(',').append(' ');
    buf.append("family").append('=').append(JodaBeanUtils.toString(getFamily())).append(',').append(' ');
    buf.append("currency").append('=').append(JodaBeanUtils.toString(getCurrency())).append(',').append(' ');
    buf.append("recoveryRate").append('=').append(JodaBeanUtils.toString(getRecoveryRate())).append(',').append(' ');
    buf.append("tenors").append('=').append(JodaBeanUtils.toString(getTenors())).append(',').append(' ');
    buf.append("components").append('=').append(JodaBeanUtils.toString(getComponents())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code CreditDefaultSwapIndexDefinitionSecurityBean}.
   */
  public static class Meta extends SecurityBean.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code name} property.
     */
    private final MetaProperty<String> _name = DirectMetaProperty.ofReadWrite(
        this, "name", CreditDefaultSwapIndexDefinitionSecurityBean.class, String.class);
    /**
     * The meta-property for the {@code version} property.
     */
    private final MetaProperty<String> _version = DirectMetaProperty.ofReadWrite(
        this, "version", CreditDefaultSwapIndexDefinitionSecurityBean.class, String.class);
    /**
     * The meta-property for the {@code series} property.
     */
    private final MetaProperty<String> _series = DirectMetaProperty.ofReadWrite(
        this, "series", CreditDefaultSwapIndexDefinitionSecurityBean.class, String.class);
    /**
     * The meta-property for the {@code family} property.
     */
    private final MetaProperty<CDSIndexFamilyBean> _family = DirectMetaProperty.ofReadWrite(
        this, "family", CreditDefaultSwapIndexDefinitionSecurityBean.class, CDSIndexFamilyBean.class);
    /**
     * The meta-property for the {@code currency} property.
     */
    private final MetaProperty<CurrencyBean> _currency = DirectMetaProperty.ofReadWrite(
        this, "currency", CreditDefaultSwapIndexDefinitionSecurityBean.class, CurrencyBean.class);
    /**
     * The meta-property for the {@code recoveryRate} property.
     */
    private final MetaProperty<Double> _recoveryRate = DirectMetaProperty.ofReadWrite(
        this, "recoveryRate", CreditDefaultSwapIndexDefinitionSecurityBean.class, Double.class);
    /**
     * The meta-property for the {@code tenors} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<Set<TenorBean>> _tenors = DirectMetaProperty.ofReadWrite(
        this, "tenors", CreditDefaultSwapIndexDefinitionSecurityBean.class, (Class) Set.class);
    /**
     * The meta-property for the {@code components} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<Set<CDSIndexComponentBean>> _components = DirectMetaProperty.ofReadWrite(
        this, "components", CreditDefaultSwapIndexDefinitionSecurityBean.class, (Class) Set.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "name",
        "version",
        "series",
        "family",
        "currency",
        "recoveryRate",
        "tenors",
        "components");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          return _name;
        case 351608024:  // version
          return _version;
        case -905838985:  // series
          return _series;
        case -1281860764:  // family
          return _family;
        case 575402001:  // currency
          return _currency;
        case 2002873877:  // recoveryRate
          return _recoveryRate;
        case -877322829:  // tenors
          return _tenors;
        case -447446250:  // components
          return _components;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends CreditDefaultSwapIndexDefinitionSecurityBean> builder() {
      return new DirectBeanBuilder<CreditDefaultSwapIndexDefinitionSecurityBean>(new CreditDefaultSwapIndexDefinitionSecurityBean());
    }

    @Override
    public Class<? extends CreditDefaultSwapIndexDefinitionSecurityBean> beanType() {
      return CreditDefaultSwapIndexDefinitionSecurityBean.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code name} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> name() {
      return _name;
    }

    /**
     * The meta-property for the {@code version} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> version() {
      return _version;
    }

    /**
     * The meta-property for the {@code series} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> series() {
      return _series;
    }

    /**
     * The meta-property for the {@code family} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<CDSIndexFamilyBean> family() {
      return _family;
    }

    /**
     * The meta-property for the {@code currency} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<CurrencyBean> currency() {
      return _currency;
    }

    /**
     * The meta-property for the {@code recoveryRate} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Double> recoveryRate() {
      return _recoveryRate;
    }

    /**
     * The meta-property for the {@code tenors} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Set<TenorBean>> tenors() {
      return _tenors;
    }

    /**
     * The meta-property for the {@code components} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Set<CDSIndexComponentBean>> components() {
      return _components;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          return ((CreditDefaultSwapIndexDefinitionSecurityBean) bean).getName();
        case 351608024:  // version
          return ((CreditDefaultSwapIndexDefinitionSecurityBean) bean).getVersion();
        case -905838985:  // series
          return ((CreditDefaultSwapIndexDefinitionSecurityBean) bean).getSeries();
        case -1281860764:  // family
          return ((CreditDefaultSwapIndexDefinitionSecurityBean) bean).getFamily();
        case 575402001:  // currency
          return ((CreditDefaultSwapIndexDefinitionSecurityBean) bean).getCurrency();
        case 2002873877:  // recoveryRate
          return ((CreditDefaultSwapIndexDefinitionSecurityBean) bean).getRecoveryRate();
        case -877322829:  // tenors
          return ((CreditDefaultSwapIndexDefinitionSecurityBean) bean).getTenors();
        case -447446250:  // components
          return ((CreditDefaultSwapIndexDefinitionSecurityBean) bean).getComponents();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          ((CreditDefaultSwapIndexDefinitionSecurityBean) bean).setName((String) newValue);
          return;
        case 351608024:  // version
          ((CreditDefaultSwapIndexDefinitionSecurityBean) bean).setVersion((String) newValue);
          return;
        case -905838985:  // series
          ((CreditDefaultSwapIndexDefinitionSecurityBean) bean).setSeries((String) newValue);
          return;
        case -1281860764:  // family
          ((CreditDefaultSwapIndexDefinitionSecurityBean) bean).setFamily((CDSIndexFamilyBean) newValue);
          return;
        case 575402001:  // currency
          ((CreditDefaultSwapIndexDefinitionSecurityBean) bean).setCurrency((CurrencyBean) newValue);
          return;
        case 2002873877:  // recoveryRate
          ((CreditDefaultSwapIndexDefinitionSecurityBean) bean).setRecoveryRate((Double) newValue);
          return;
        case -877322829:  // tenors
          ((CreditDefaultSwapIndexDefinitionSecurityBean) bean).setTenors((Set<TenorBean>) newValue);
          return;
        case -447446250:  // components
          ((CreditDefaultSwapIndexDefinitionSecurityBean) bean).setComponents((Set<CDSIndexComponentBean>) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((CreditDefaultSwapIndexDefinitionSecurityBean) bean)._tenors, "tenors");
      JodaBeanUtils.notNull(((CreditDefaultSwapIndexDefinitionSecurityBean) bean)._components, "components");
      super.validate(bean);
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

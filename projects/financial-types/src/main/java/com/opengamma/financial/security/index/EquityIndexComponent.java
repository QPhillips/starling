/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security.index;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

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

import com.opengamma.id.ExternalIdBundle;

/**
 * Class representing a member of an equity index.
 */
@BeanDefinition
public class EquityIndexComponent extends DirectBean implements Serializable {

  /** Serialization version */
  private static final long serialVersionUID = 1L;

  /**
   * The equity identifier.
   */
  @PropertyDefinition(validate = "notNull")
  private ExternalIdBundle _equityIdentifier;

  /**
   * The weight.
   */
  @PropertyDefinition(validate = "notNull")
  private BigDecimal _weight;

  /**
   * For the builder.
   */
  /* package */ EquityIndexComponent() {
    super();
  }

  /**
   * @param equityIdentifier The equity identifier bundle, not null
   * @param weight The weight, not null
   */
  public EquityIndexComponent(final ExternalIdBundle equityIdentifier, final BigDecimal weight) {
    setEquityIdentifier(equityIdentifier);
    setWeight(weight);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code EquityIndexComponent}.
   * @return the meta-bean, not null
   */
  public static EquityIndexComponent.Meta meta() {
    return EquityIndexComponent.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(EquityIndexComponent.Meta.INSTANCE);
  }

  @Override
  public EquityIndexComponent.Meta metaBean() {
    return EquityIndexComponent.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the equity identifier.
   * @return the value of the property, not null
   */
  public ExternalIdBundle getEquityIdentifier() {
    return _equityIdentifier;
  }

  /**
   * Sets the equity identifier.
   * @param equityIdentifier  the new value of the property, not null
   */
  public void setEquityIdentifier(ExternalIdBundle equityIdentifier) {
    JodaBeanUtils.notNull(equityIdentifier, "equityIdentifier");
    this._equityIdentifier = equityIdentifier;
  }

  /**
   * Gets the the {@code equityIdentifier} property.
   * @return the property, not null
   */
  public final Property<ExternalIdBundle> equityIdentifier() {
    return metaBean().equityIdentifier().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the weight.
   * @return the value of the property, not null
   */
  public BigDecimal getWeight() {
    return _weight;
  }

  /**
   * Sets the weight.
   * @param weight  the new value of the property, not null
   */
  public void setWeight(BigDecimal weight) {
    JodaBeanUtils.notNull(weight, "weight");
    this._weight = weight;
  }

  /**
   * Gets the the {@code weight} property.
   * @return the property, not null
   */
  public final Property<BigDecimal> weight() {
    return metaBean().weight().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public EquityIndexComponent clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      EquityIndexComponent other = (EquityIndexComponent) obj;
      return JodaBeanUtils.equal(getEquityIdentifier(), other.getEquityIdentifier()) &&
          JodaBeanUtils.equal(getWeight(), other.getWeight());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getEquityIdentifier());
    hash = hash * 31 + JodaBeanUtils.hashCode(getWeight());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("EquityIndexComponent{");
    int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  protected void toString(StringBuilder buf) {
    buf.append("equityIdentifier").append('=').append(JodaBeanUtils.toString(getEquityIdentifier())).append(',').append(' ');
    buf.append("weight").append('=').append(JodaBeanUtils.toString(getWeight())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code EquityIndexComponent}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code equityIdentifier} property.
     */
    private final MetaProperty<ExternalIdBundle> _equityIdentifier = DirectMetaProperty.ofReadWrite(
        this, "equityIdentifier", EquityIndexComponent.class, ExternalIdBundle.class);
    /**
     * The meta-property for the {@code weight} property.
     */
    private final MetaProperty<BigDecimal> _weight = DirectMetaProperty.ofReadWrite(
        this, "weight", EquityIndexComponent.class, BigDecimal.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "equityIdentifier",
        "weight");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1452067506:  // equityIdentifier
          return _equityIdentifier;
        case -791592328:  // weight
          return _weight;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends EquityIndexComponent> builder() {
      return new DirectBeanBuilder<EquityIndexComponent>(new EquityIndexComponent());
    }

    @Override
    public Class<? extends EquityIndexComponent> beanType() {
      return EquityIndexComponent.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code equityIdentifier} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ExternalIdBundle> equityIdentifier() {
      return _equityIdentifier;
    }

    /**
     * The meta-property for the {@code weight} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<BigDecimal> weight() {
      return _weight;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -1452067506:  // equityIdentifier
          return ((EquityIndexComponent) bean).getEquityIdentifier();
        case -791592328:  // weight
          return ((EquityIndexComponent) bean).getWeight();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -1452067506:  // equityIdentifier
          ((EquityIndexComponent) bean).setEquityIdentifier((ExternalIdBundle) newValue);
          return;
        case -791592328:  // weight
          ((EquityIndexComponent) bean).setWeight((BigDecimal) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((EquityIndexComponent) bean)._equityIdentifier, "equityIdentifier");
      JodaBeanUtils.notNull(((EquityIndexComponent) bean)._weight, "weight");
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security.cds;

import java.io.Serializable;
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

import com.opengamma.id.ExternalId;
import com.opengamma.util.ArgumentChecker;

/**
 * Represents a member of a Credit Default Swap Index
 */
@BeanDefinition
public class CreditDefaultSwapIndexComponent extends DirectBean implements Comparable<CreditDefaultSwapIndexComponent>, Serializable {

  /** Serialization version. */
  private static final long serialVersionUID = 2138042002689914578L;

  /**
   * The obligor red code identifier
   */
  @PropertyDefinition(validate = "notNull")
  private ExternalId _obligorRedCode;
  /**
   * The weight
   */
  @PropertyDefinition(validate = "notNull")
  private Double _weight;
  /**
   * The optional bond identifier;
   */
  @PropertyDefinition
  private ExternalId _bondId;
  /**
   * The entity name
   */
  @PropertyDefinition(validate = "notNull")
  private String _name;

  /**
   * Creates an instance
   *
   * @param name
   *          the entity name, not null
   * @param obligorId
   *          the obligor red code, not null
   * @param weight
   *          the index weight, not null
   * @param bondId
   *          the option bond identifier
   */
  public CreditDefaultSwapIndexComponent(final String name, final ExternalId obligorId, final Double weight, final ExternalId bondId) {
    setName(name);
    setObligorRedCode(obligorId);
    setWeight(weight);
    setBondId(bondId);
  }

  /**
   * Default constructor for Bean builder
   */
  CreditDefaultSwapIndexComponent() {
  }


  //-------------------------------------------------------------------------
  /**
   * Compares the cdsIndex component, sorting by weight followed by name alphabetically.
   *
   * @param other  the other cdsIndex component, not null
   * @return negative if this is less, zero if equal, positive if greater
   */
  @Override
  public int compareTo(final CreditDefaultSwapIndexComponent other) {
    ArgumentChecker.notNull(other, "creditswapindex component");
    final int cmp = _weight.compareTo(other._weight);
    if (cmp != 0) {
      return cmp;
    }
    return _name.compareTo(other._name);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code CreditDefaultSwapIndexComponent}.
   * @return the meta-bean, not null
   */
  public static CreditDefaultSwapIndexComponent.Meta meta() {
    return CreditDefaultSwapIndexComponent.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(CreditDefaultSwapIndexComponent.Meta.INSTANCE);
  }

  @Override
  public CreditDefaultSwapIndexComponent.Meta metaBean() {
    return CreditDefaultSwapIndexComponent.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the obligor red code identifier
   * @return the value of the property, not null
   */
  public ExternalId getObligorRedCode() {
    return _obligorRedCode;
  }

  /**
   * Sets the obligor red code identifier
   * @param obligorRedCode  the new value of the property, not null
   */
  public void setObligorRedCode(ExternalId obligorRedCode) {
    JodaBeanUtils.notNull(obligorRedCode, "obligorRedCode");
    this._obligorRedCode = obligorRedCode;
  }

  /**
   * Gets the the {@code obligorRedCode} property.
   * @return the property, not null
   */
  public final Property<ExternalId> obligorRedCode() {
    return metaBean().obligorRedCode().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the weight
   * @return the value of the property, not null
   */
  public Double getWeight() {
    return _weight;
  }

  /**
   * Sets the weight
   * @param weight  the new value of the property, not null
   */
  public void setWeight(Double weight) {
    JodaBeanUtils.notNull(weight, "weight");
    this._weight = weight;
  }

  /**
   * Gets the the {@code weight} property.
   * @return the property, not null
   */
  public final Property<Double> weight() {
    return metaBean().weight().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the optional bond identifier;
   * @return the value of the property
   */
  public ExternalId getBondId() {
    return _bondId;
  }

  /**
   * Sets the optional bond identifier;
   * @param bondId  the new value of the property
   */
  public void setBondId(ExternalId bondId) {
    this._bondId = bondId;
  }

  /**
   * Gets the the {@code bondId} property.
   * @return the property, not null
   */
  public final Property<ExternalId> bondId() {
    return metaBean().bondId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the entity name
   * @return the value of the property, not null
   */
  public String getName() {
    return _name;
  }

  /**
   * Sets the entity name
   * @param name  the new value of the property, not null
   */
  public void setName(String name) {
    JodaBeanUtils.notNull(name, "name");
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
  @Override
  public CreditDefaultSwapIndexComponent clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      CreditDefaultSwapIndexComponent other = (CreditDefaultSwapIndexComponent) obj;
      return JodaBeanUtils.equal(getObligorRedCode(), other.getObligorRedCode()) &&
          JodaBeanUtils.equal(getWeight(), other.getWeight()) &&
          JodaBeanUtils.equal(getBondId(), other.getBondId()) &&
          JodaBeanUtils.equal(getName(), other.getName());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getObligorRedCode());
    hash = hash * 31 + JodaBeanUtils.hashCode(getWeight());
    hash = hash * 31 + JodaBeanUtils.hashCode(getBondId());
    hash = hash * 31 + JodaBeanUtils.hashCode(getName());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(160);
    buf.append("CreditDefaultSwapIndexComponent{");
    int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  protected void toString(StringBuilder buf) {
    buf.append("obligorRedCode").append('=').append(JodaBeanUtils.toString(getObligorRedCode())).append(',').append(' ');
    buf.append("weight").append('=').append(JodaBeanUtils.toString(getWeight())).append(',').append(' ');
    buf.append("bondId").append('=').append(JodaBeanUtils.toString(getBondId())).append(',').append(' ');
    buf.append("name").append('=').append(JodaBeanUtils.toString(getName())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code CreditDefaultSwapIndexComponent}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code obligorRedCode} property.
     */
    private final MetaProperty<ExternalId> _obligorRedCode = DirectMetaProperty.ofReadWrite(
        this, "obligorRedCode", CreditDefaultSwapIndexComponent.class, ExternalId.class);
    /**
     * The meta-property for the {@code weight} property.
     */
    private final MetaProperty<Double> _weight = DirectMetaProperty.ofReadWrite(
        this, "weight", CreditDefaultSwapIndexComponent.class, Double.class);
    /**
     * The meta-property for the {@code bondId} property.
     */
    private final MetaProperty<ExternalId> _bondId = DirectMetaProperty.ofReadWrite(
        this, "bondId", CreditDefaultSwapIndexComponent.class, ExternalId.class);
    /**
     * The meta-property for the {@code name} property.
     */
    private final MetaProperty<String> _name = DirectMetaProperty.ofReadWrite(
        this, "name", CreditDefaultSwapIndexComponent.class, String.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "obligorRedCode",
        "weight",
        "bondId",
        "name");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 122314948:  // obligorRedCode
          return _obligorRedCode;
        case -791592328:  // weight
          return _weight;
        case -1383424194:  // bondId
          return _bondId;
        case 3373707:  // name
          return _name;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends CreditDefaultSwapIndexComponent> builder() {
      return new DirectBeanBuilder<CreditDefaultSwapIndexComponent>(new CreditDefaultSwapIndexComponent());
    }

    @Override
    public Class<? extends CreditDefaultSwapIndexComponent> beanType() {
      return CreditDefaultSwapIndexComponent.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code obligorRedCode} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ExternalId> obligorRedCode() {
      return _obligorRedCode;
    }

    /**
     * The meta-property for the {@code weight} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Double> weight() {
      return _weight;
    }

    /**
     * The meta-property for the {@code bondId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ExternalId> bondId() {
      return _bondId;
    }

    /**
     * The meta-property for the {@code name} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> name() {
      return _name;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 122314948:  // obligorRedCode
          return ((CreditDefaultSwapIndexComponent) bean).getObligorRedCode();
        case -791592328:  // weight
          return ((CreditDefaultSwapIndexComponent) bean).getWeight();
        case -1383424194:  // bondId
          return ((CreditDefaultSwapIndexComponent) bean).getBondId();
        case 3373707:  // name
          return ((CreditDefaultSwapIndexComponent) bean).getName();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 122314948:  // obligorRedCode
          ((CreditDefaultSwapIndexComponent) bean).setObligorRedCode((ExternalId) newValue);
          return;
        case -791592328:  // weight
          ((CreditDefaultSwapIndexComponent) bean).setWeight((Double) newValue);
          return;
        case -1383424194:  // bondId
          ((CreditDefaultSwapIndexComponent) bean).setBondId((ExternalId) newValue);
          return;
        case 3373707:  // name
          ((CreditDefaultSwapIndexComponent) bean).setName((String) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((CreditDefaultSwapIndexComponent) bean)._obligorRedCode, "obligorRedCode");
      JodaBeanUtils.notNull(((CreditDefaultSwapIndexComponent) bean)._weight, "weight");
      JodaBeanUtils.notNull(((CreditDefaultSwapIndexComponent) bean)._name, "name");
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

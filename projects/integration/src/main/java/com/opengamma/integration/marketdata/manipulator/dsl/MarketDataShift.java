/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.integration.marketdata.manipulator.dsl;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.ImmutableConstructor;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.engine.function.FunctionExecutionContext;
import com.opengamma.engine.marketdata.manipulator.function.StructureManipulator;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.util.ArgumentChecker;

/**
 * Shifts a market data value.
 * If the shift type is {@link ScenarioShiftType#ABSOLUTE absolute} the shift amount is added to the value.
 * If the shift type is {@link ScenarioShiftType#RELATIVE relative} the value is multiplied by (1 + shift amount). This
 * means a shift of +10% scales a value by 1.1 and a shift of -20% scales a value by 0.8.
 */
@BeanDefinition
public final class MarketDataShift implements StructureManipulator<Double>, ImmutableBean {

  /** How the shift amount should be applied. */
  @PropertyDefinition(validate = "notNull")
  private final ScenarioShiftType _shiftType;

  /** Absolute shift added to the market data value. */
  @PropertyDefinition
  private final double _shift;

  @ImmutableConstructor
  /* package */ MarketDataShift(final ScenarioShiftType shiftType, final double shift) {
    _shiftType = ArgumentChecker.notNull(shiftType, "shiftType");
    if (Double.isInfinite(shift) || Double.isNaN(shift)) {
      throw new IllegalArgumentException("shift must not be infinite or NaN. value=" + shift);
    }
    _shift = shift;
  }

  @Override
  public Double execute(final Double value, final ValueSpecification valueSpecification, final FunctionExecutionContext executionContext) {
    switch (_shiftType) {
      case ABSOLUTE:
        return value + _shift;
      case RELATIVE:
        return value * (1 + _shift);
      default:
        throw new IllegalArgumentException("Unexpected shift type " + _shiftType);
    }
  }

  @Override
  public Class<Double> getExpectedType() {
    return Double.class;
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code MarketDataShift}.
   * @return the meta-bean, not null
   */
  public static MarketDataShift.Meta meta() {
    return MarketDataShift.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(MarketDataShift.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static MarketDataShift.Builder builder() {
    return new MarketDataShift.Builder();
  }

  @Override
  public MarketDataShift.Meta metaBean() {
    return MarketDataShift.Meta.INSTANCE;
  }

  @Override
  public <R> Property<R> property(String propertyName) {
    return metaBean().<R>metaProperty(propertyName).createProperty(this);
  }

  @Override
  public Set<String> propertyNames() {
    return metaBean().metaPropertyMap().keySet();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets how the shift amount should be applied.
   * @return the value of the property, not null
   */
  public ScenarioShiftType getShiftType() {
    return _shiftType;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets absolute shift added to the market data value.
   * @return the value of the property
   */
  public double getShift() {
    return _shift;
  }

  //-----------------------------------------------------------------------
  /**
   * Returns a builder that allows this bean to be mutated.
   * @return the mutable builder, not null
   */
  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      MarketDataShift other = (MarketDataShift) obj;
      return JodaBeanUtils.equal(getShiftType(), other.getShiftType()) &&
          JodaBeanUtils.equal(getShift(), other.getShift());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getShiftType());
    hash = hash * 31 + JodaBeanUtils.hashCode(getShift());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("MarketDataShift{");
    buf.append("shiftType").append('=').append(getShiftType()).append(',').append(' ');
    buf.append("shift").append('=').append(JodaBeanUtils.toString(getShift()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code MarketDataShift}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code shiftType} property.
     */
    private final MetaProperty<ScenarioShiftType> _shiftType = DirectMetaProperty.ofImmutable(
        this, "shiftType", MarketDataShift.class, ScenarioShiftType.class);
    /**
     * The meta-property for the {@code shift} property.
     */
    private final MetaProperty<Double> _shift = DirectMetaProperty.ofImmutable(
        this, "shift", MarketDataShift.class, Double.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "shiftType",
        "shift");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 893345500:  // shiftType
          return _shiftType;
        case 109407362:  // shift
          return _shift;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public MarketDataShift.Builder builder() {
      return new MarketDataShift.Builder();
    }

    @Override
    public Class<? extends MarketDataShift> beanType() {
      return MarketDataShift.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code shiftType} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ScenarioShiftType> shiftType() {
      return _shiftType;
    }

    /**
     * The meta-property for the {@code shift} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Double> shift() {
      return _shift;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 893345500:  // shiftType
          return ((MarketDataShift) bean).getShiftType();
        case 109407362:  // shift
          return ((MarketDataShift) bean).getShift();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      metaProperty(propertyName);
      if (quiet) {
        return;
      }
      throw new UnsupportedOperationException("Property cannot be written: " + propertyName);
    }

  }

  //-----------------------------------------------------------------------
  /**
   * The bean-builder for {@code MarketDataShift}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<MarketDataShift> {

    private ScenarioShiftType _shiftType;
    private double _shift;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(MarketDataShift beanToCopy) {
      this._shiftType = beanToCopy.getShiftType();
      this._shift = beanToCopy.getShift();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 893345500:  // shiftType
          return _shiftType;
        case 109407362:  // shift
          return _shift;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 893345500:  // shiftType
          this._shiftType = (ScenarioShiftType) newValue;
          break;
        case 109407362:  // shift
          this._shift = (Double) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public Builder set(MetaProperty<?> property, Object value) {
      super.set(property, value);
      return this;
    }

    @Override
    public Builder setString(String propertyName, String value) {
      setString(meta().metaProperty(propertyName), value);
      return this;
    }

    @Override
    public Builder setString(MetaProperty<?> property, String value) {
      super.setString(property, value);
      return this;
    }

    @Override
    public Builder setAll(Map<String, ? extends Object> propertyValueMap) {
      super.setAll(propertyValueMap);
      return this;
    }

    @Override
    public MarketDataShift build() {
      return new MarketDataShift(
          _shiftType,
          _shift);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets how the shift amount should be applied.
     * @param shiftType  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder shiftType(ScenarioShiftType shiftType) {
      JodaBeanUtils.notNull(shiftType, "shiftType");
      this._shiftType = shiftType;
      return this;
    }

    /**
     * Sets absolute shift added to the market data value.
     * @param shift  the new value
     * @return this, for chaining, not null
     */
    public Builder shift(double shift) {
      this._shift = shift;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(96);
      buf.append("MarketDataShift.Builder{");
      buf.append("shiftType").append('=').append(JodaBeanUtils.toString(_shiftType)).append(',').append(' ');
      buf.append("shift").append('=').append(JodaBeanUtils.toString(_shift));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

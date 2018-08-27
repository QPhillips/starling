/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security.swap;

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

import com.opengamma.util.money.Currency;

/**
 * Notional for variance swaps.
 */
@BeanDefinition
public class VarianceSwapNotional extends Notional {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * The currency.
   */
  @PropertyDefinition(validate = "notNull")
  private Currency _currency;
  /**
   * The amount.
   */
  @PropertyDefinition
  private double _amount;

  @Override
  public <T> T accept(final NotionalVisitor<T> visitor) {
    return visitor.visitVarianceSwapNotional(this);
  }

  /**
   * Creates an instance.
   *
   * @param currency The notional curency, not null
   * @param amount The notional amount
   */
  public VarianceSwapNotional(final Currency currency, final double amount) {
    _currency = currency;
    _amount = amount;
    setCurrency(currency);
    setAmount(amount);
  }

  /**
   * Creates an empty instance.
   */
  private VarianceSwapNotional() {
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code VarianceSwapNotional}.
   * @return the meta-bean, not null
   */
  public static VarianceSwapNotional.Meta meta() {
    return VarianceSwapNotional.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(VarianceSwapNotional.Meta.INSTANCE);
  }

  @Override
  public VarianceSwapNotional.Meta metaBean() {
    return VarianceSwapNotional.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the currency.
   * @return the value of the property, not null
   */
  public Currency getCurrency() {
    return _currency;
  }

  /**
   * Sets the currency.
   * @param currency  the new value of the property, not null
   */
  public void setCurrency(Currency currency) {
    JodaBeanUtils.notNull(currency, "currency");
    this._currency = currency;
  }

  /**
   * Gets the the {@code currency} property.
   * @return the property, not null
   */
  public final Property<Currency> currency() {
    return metaBean().currency().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the amount.
   * @return the value of the property
   */
  public double getAmount() {
    return _amount;
  }

  /**
   * Sets the amount.
   * @param amount  the new value of the property
   */
  public void setAmount(double amount) {
    this._amount = amount;
  }

  /**
   * Gets the the {@code amount} property.
   * @return the property, not null
   */
  public final Property<Double> amount() {
    return metaBean().amount().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public VarianceSwapNotional clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      VarianceSwapNotional other = (VarianceSwapNotional) obj;
      return JodaBeanUtils.equal(getCurrency(), other.getCurrency()) &&
          JodaBeanUtils.equal(getAmount(), other.getAmount()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = hash * 31 + JodaBeanUtils.hashCode(getCurrency());
    hash = hash * 31 + JodaBeanUtils.hashCode(getAmount());
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("VarianceSwapNotional{");
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
    buf.append("currency").append('=').append(JodaBeanUtils.toString(getCurrency())).append(',').append(' ');
    buf.append("amount").append('=').append(JodaBeanUtils.toString(getAmount())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code VarianceSwapNotional}.
   */
  public static class Meta extends Notional.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code currency} property.
     */
    private final MetaProperty<Currency> _currency = DirectMetaProperty.ofReadWrite(
        this, "currency", VarianceSwapNotional.class, Currency.class);
    /**
     * The meta-property for the {@code amount} property.
     */
    private final MetaProperty<Double> _amount = DirectMetaProperty.ofReadWrite(
        this, "amount", VarianceSwapNotional.class, Double.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "currency",
        "amount");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 575402001:  // currency
          return _currency;
        case -1413853096:  // amount
          return _amount;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends VarianceSwapNotional> builder() {
      return new DirectBeanBuilder<VarianceSwapNotional>(new VarianceSwapNotional());
    }

    @Override
    public Class<? extends VarianceSwapNotional> beanType() {
      return VarianceSwapNotional.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code currency} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Currency> currency() {
      return _currency;
    }

    /**
     * The meta-property for the {@code amount} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Double> amount() {
      return _amount;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 575402001:  // currency
          return ((VarianceSwapNotional) bean).getCurrency();
        case -1413853096:  // amount
          return ((VarianceSwapNotional) bean).getAmount();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 575402001:  // currency
          ((VarianceSwapNotional) bean).setCurrency((Currency) newValue);
          return;
        case -1413853096:  // amount
          ((VarianceSwapNotional) bean).setAmount((Double) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((VarianceSwapNotional) bean)._currency, "currency");
      super.validate(bean);
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

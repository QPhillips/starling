/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.security.swap;

import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.threeten.bp.ZonedDateTime;

import com.opengamma.financial.security.FinancialSecurityVisitor;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.time.Tenor;

/**
 * A security for a zero-coupon inflation swap.
 */
@BeanDefinition
public class YearOnYearInflationSwapSecurity extends SwapSecurity {

  /** Serialization version */
  private static final long serialVersionUID = 1L;

  /**
   * The security type.
   */
  public static final String SECURITY_TYPE = "YEAR_ON_YEAR_INFLATION_SWAP";

  /**
   * The tenor of the swap.
   */
  @PropertyDefinition(validate = "notNull")
  private Tenor _tenor;

  /**
   * For the builder
   */
  /* package */YearOnYearInflationSwapSecurity() {
    super(SECURITY_TYPE);
  }

  /**
   * @param tradeDate The trade date, not null
   * @param effectiveDate The effective date, not null
   * @param maturityDate The maturity date, not null
   * @param counterparty The counterparty, not null
   * @param payLeg The pay leg, not null
   * @param receiveLeg The receive leg, not null
   * @param tenor The tenor, not null
   */
  public YearOnYearInflationSwapSecurity(final ZonedDateTime tradeDate, final ZonedDateTime effectiveDate, final ZonedDateTime maturityDate,
      final String counterparty, final SwapLeg payLeg, final SwapLeg receiveLeg, final Tenor tenor) {
    super(SECURITY_TYPE, tradeDate, effectiveDate, maturityDate, counterparty, payLeg, receiveLeg);
    setTenor(tenor);
  }

  @Override
  public <T> T accept(final FinancialSecurityVisitor<T> visitor) {
    ArgumentChecker.notNull(visitor, "visitor");
    return visitor.visitYearOnYearInflationSwapSecurity(this);
  }
  
  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code YearOnYearInflationSwapSecurity}.
   * @return the meta-bean, not null
   */
  public static YearOnYearInflationSwapSecurity.Meta meta() {
    return YearOnYearInflationSwapSecurity.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(YearOnYearInflationSwapSecurity.Meta.INSTANCE);
  }

  @Override
  public YearOnYearInflationSwapSecurity.Meta metaBean() {
    return YearOnYearInflationSwapSecurity.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 110246592:  // tenor
        return getTenor();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 110246592:  // tenor
        setTenor((Tenor) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  protected void validate() {
    JodaBeanUtils.notNull(_tenor, "tenor");
    super.validate();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      YearOnYearInflationSwapSecurity other = (YearOnYearInflationSwapSecurity) obj;
      return JodaBeanUtils.equal(getTenor(), other.getTenor()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getTenor());
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the tenor of the swap.
   * @return the value of the property, not null
   */
  public Tenor getTenor() {
    return _tenor;
  }

  /**
   * Sets the tenor of the swap.
   * @param tenor  the new value of the property, not null
   */
  public void setTenor(Tenor tenor) {
    JodaBeanUtils.notNull(tenor, "tenor");
    this._tenor = tenor;
  }

  /**
   * Gets the the {@code tenor} property.
   * @return the property, not null
   */
  public final Property<Tenor> tenor() {
    return metaBean().tenor().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code YearOnYearInflationSwapSecurity}.
   */
  public static class Meta extends SwapSecurity.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code tenor} property.
     */
    private final MetaProperty<Tenor> _tenor = DirectMetaProperty.ofReadWrite(
        this, "tenor", YearOnYearInflationSwapSecurity.class, Tenor.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "tenor");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 110246592:  // tenor
          return _tenor;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends YearOnYearInflationSwapSecurity> builder() {
      return new DirectBeanBuilder<YearOnYearInflationSwapSecurity>(new YearOnYearInflationSwapSecurity());
    }

    @Override
    public Class<? extends YearOnYearInflationSwapSecurity> beanType() {
      return YearOnYearInflationSwapSecurity.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code tenor} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Tenor> tenor() {
      return _tenor;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
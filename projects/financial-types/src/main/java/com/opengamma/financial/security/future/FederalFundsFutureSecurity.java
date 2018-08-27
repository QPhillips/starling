/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security.future;

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

import com.opengamma.financial.security.FinancialSecurityVisitor;
import com.opengamma.id.ExternalId;
import com.opengamma.util.money.Currency;
import com.opengamma.util.time.Expiry;

/**
 * A security for Federal funds futures.
 */
@BeanDefinition
public class FederalFundsFutureSecurity extends FutureSecurity {

  /** Serialization version */
  private static final long serialVersionUID = 1L;

  /**
   * The underlying identifier.
   */
  @PropertyDefinition(validate = "notNull")
  private ExternalId _underlyingId;

  /**
   * Creates an empty instance.
   */
  protected FederalFundsFutureSecurity() {
    super();
  }

  public FederalFundsFutureSecurity(final Expiry expiry, final String tradingExchange, final String settlementExchange, final Currency currency, final double unitAmount, final ExternalId underlyingIdentifier, final String category) {
    super(expiry, tradingExchange, settlementExchange, currency, unitAmount, category);
    setUnderlyingId(underlyingIdentifier);
  }

  //-------------------------------------------------------------------------
  @Override
  public <T> T accept(final FinancialSecurityVisitor<T> visitor) {
    return visitor.visitFederalFundsFutureSecurity(this);
  }
  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code FederalFundsFutureSecurity}.
   * @return the meta-bean, not null
   */
  public static FederalFundsFutureSecurity.Meta meta() {
    return FederalFundsFutureSecurity.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(FederalFundsFutureSecurity.Meta.INSTANCE);
  }

  @Override
  public FederalFundsFutureSecurity.Meta metaBean() {
    return FederalFundsFutureSecurity.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the underlying identifier.
   * @return the value of the property, not null
   */
  public ExternalId getUnderlyingId() {
    return _underlyingId;
  }

  /**
   * Sets the underlying identifier.
   * @param underlyingId  the new value of the property, not null
   */
  public void setUnderlyingId(ExternalId underlyingId) {
    JodaBeanUtils.notNull(underlyingId, "underlyingId");
    this._underlyingId = underlyingId;
  }

  /**
   * Gets the the {@code underlyingId} property.
   * @return the property, not null
   */
  public final Property<ExternalId> underlyingId() {
    return metaBean().underlyingId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public FederalFundsFutureSecurity clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      FederalFundsFutureSecurity other = (FederalFundsFutureSecurity) obj;
      return JodaBeanUtils.equal(getUnderlyingId(), other.getUnderlyingId()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = hash * 31 + JodaBeanUtils.hashCode(getUnderlyingId());
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(64);
    buf.append("FederalFundsFutureSecurity{");
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
    buf.append("underlyingId").append('=').append(JodaBeanUtils.toString(getUnderlyingId())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code FederalFundsFutureSecurity}.
   */
  public static class Meta extends FutureSecurity.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code underlyingId} property.
     */
    private final MetaProperty<ExternalId> _underlyingId = DirectMetaProperty.ofReadWrite(
        this, "underlyingId", FederalFundsFutureSecurity.class, ExternalId.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "underlyingId");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -771625640:  // underlyingId
          return _underlyingId;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends FederalFundsFutureSecurity> builder() {
      return new DirectBeanBuilder<FederalFundsFutureSecurity>(new FederalFundsFutureSecurity());
    }

    @Override
    public Class<? extends FederalFundsFutureSecurity> beanType() {
      return FederalFundsFutureSecurity.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code underlyingId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ExternalId> underlyingId() {
      return _underlyingId;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -771625640:  // underlyingId
          return ((FederalFundsFutureSecurity) bean).getUnderlyingId();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -771625640:  // underlyingId
          ((FederalFundsFutureSecurity) bean).setUnderlyingId((ExternalId) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((FederalFundsFutureSecurity) bean)._underlyingId, "underlyingId");
      super.validate(bean);
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

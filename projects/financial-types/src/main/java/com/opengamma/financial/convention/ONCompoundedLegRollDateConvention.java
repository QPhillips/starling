/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.convention;

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

import com.opengamma.core.convention.ConventionGroups;
import com.opengamma.core.convention.ConventionMetaData;
import com.opengamma.core.convention.ConventionType;
import com.opengamma.id.ExternalId;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.time.Tenor;

/**
 * Convention for an ON (simple rate, compounded) leg based on roll date adjuster. This convention should be used only for IMM swaps.
 */
@ConventionMetaData(description = "Overnight compounded IMM swap leg", group = ConventionGroups.ROLL_DATE_CONVENTION)
@BeanDefinition
public class ONCompoundedLegRollDateConvention extends FinancialConvention {

  /**
   * Type of the convention.
   */
  public static final ConventionType TYPE = ConventionType.of("ONCompoundedLegRollDate");

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * The overnight index convention.
   */
  @PropertyDefinition(validate = "notNull")
  private ExternalId _overnightIndexConvention;
  /**
   * The payment tenor.
   */
  @PropertyDefinition(validate = "notNull")
  private Tenor _paymentTenor;
  /**
   * The stub type.
   */
  @PropertyDefinition(validate = "notNull")
  private StubType _stubType;
  /**
   * Whether the notional exchanged.
   */
  @PropertyDefinition
  private boolean _isExchangeNotional;
  /**
   * The payment lag in days.
   */
  @PropertyDefinition
  private int _paymentLag;

  /**
   * Creates an instance.
   */
  protected ONCompoundedLegRollDateConvention() {
    super();
  }

  /**
   * Creates an instance.
   *
   * @param name
   *          the convention name, not null
   * @param externalIdBundle
   *          the external identifiers for this convention, not null
   * @param overnightIndexConvention
   *          the underlying overnight index convention, not null
   * @param paymentTenor
   *          the reset tenor, not null
   * @param stubType
   *          the stub type, not null
   * @param isExchangeNotional
   *          true if notional is to be exchanged
   * @param paymentLag
   *          the payment lag in days
   */
  public ONCompoundedLegRollDateConvention(final String name, final ExternalIdBundle externalIdBundle, final ExternalId overnightIndexConvention,
      final Tenor paymentTenor, final StubType stubType, final boolean isExchangeNotional, final int paymentLag) {
    super(name, externalIdBundle);
    setOvernightIndexConvention(overnightIndexConvention);
    setPaymentTenor(paymentTenor);
    setStubType(stubType);
    setIsExchangeNotional(isExchangeNotional);
    setPaymentLag(paymentLag);
  }

  // -------------------------------------------------------------------------
  /**
   * Gets the type identifying this convention.
   *
   * @return the {@link #TYPE} constant, not null
   */
  @Override
  public ConventionType getConventionType() {
    return TYPE;
  }

  /**
   * Accepts a visitor to manage traversal of the hierarchy.
   *
   * @param <T>
   *          the result type of the visitor
   * @param visitor
   *          the visitor, not null
   * @return the result
   */
  @Override
  public <T> T accept(final FinancialConventionVisitor<T> visitor) {
    ArgumentChecker.notNull(visitor, "visitor");
    return visitor.visitONCompoundedLegRollDateConvention(this);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code ONCompoundedLegRollDateConvention}.
   * @return the meta-bean, not null
   */
  public static ONCompoundedLegRollDateConvention.Meta meta() {
    return ONCompoundedLegRollDateConvention.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(ONCompoundedLegRollDateConvention.Meta.INSTANCE);
  }

  @Override
  public ONCompoundedLegRollDateConvention.Meta metaBean() {
    return ONCompoundedLegRollDateConvention.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the overnight index convention.
   * @return the value of the property, not null
   */
  public ExternalId getOvernightIndexConvention() {
    return _overnightIndexConvention;
  }

  /**
   * Sets the overnight index convention.
   * @param overnightIndexConvention  the new value of the property, not null
   */
  public void setOvernightIndexConvention(ExternalId overnightIndexConvention) {
    JodaBeanUtils.notNull(overnightIndexConvention, "overnightIndexConvention");
    this._overnightIndexConvention = overnightIndexConvention;
  }

  /**
   * Gets the the {@code overnightIndexConvention} property.
   * @return the property, not null
   */
  public final Property<ExternalId> overnightIndexConvention() {
    return metaBean().overnightIndexConvention().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the payment tenor.
   * @return the value of the property, not null
   */
  public Tenor getPaymentTenor() {
    return _paymentTenor;
  }

  /**
   * Sets the payment tenor.
   * @param paymentTenor  the new value of the property, not null
   */
  public void setPaymentTenor(Tenor paymentTenor) {
    JodaBeanUtils.notNull(paymentTenor, "paymentTenor");
    this._paymentTenor = paymentTenor;
  }

  /**
   * Gets the the {@code paymentTenor} property.
   * @return the property, not null
   */
  public final Property<Tenor> paymentTenor() {
    return metaBean().paymentTenor().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the stub type.
   * @return the value of the property, not null
   */
  public StubType getStubType() {
    return _stubType;
  }

  /**
   * Sets the stub type.
   * @param stubType  the new value of the property, not null
   */
  public void setStubType(StubType stubType) {
    JodaBeanUtils.notNull(stubType, "stubType");
    this._stubType = stubType;
  }

  /**
   * Gets the the {@code stubType} property.
   * @return the property, not null
   */
  public final Property<StubType> stubType() {
    return metaBean().stubType().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets whether the notional exchanged.
   * @return the value of the property
   */
  public boolean isIsExchangeNotional() {
    return _isExchangeNotional;
  }

  /**
   * Sets whether the notional exchanged.
   * @param isExchangeNotional  the new value of the property
   */
  public void setIsExchangeNotional(boolean isExchangeNotional) {
    this._isExchangeNotional = isExchangeNotional;
  }

  /**
   * Gets the the {@code isExchangeNotional} property.
   * @return the property, not null
   */
  public final Property<Boolean> isExchangeNotional() {
    return metaBean().isExchangeNotional().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the payment lag in days.
   * @return the value of the property
   */
  public int getPaymentLag() {
    return _paymentLag;
  }

  /**
   * Sets the payment lag in days.
   * @param paymentLag  the new value of the property
   */
  public void setPaymentLag(int paymentLag) {
    this._paymentLag = paymentLag;
  }

  /**
   * Gets the the {@code paymentLag} property.
   * @return the property, not null
   */
  public final Property<Integer> paymentLag() {
    return metaBean().paymentLag().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public ONCompoundedLegRollDateConvention clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      ONCompoundedLegRollDateConvention other = (ONCompoundedLegRollDateConvention) obj;
      return JodaBeanUtils.equal(getOvernightIndexConvention(), other.getOvernightIndexConvention()) &&
          JodaBeanUtils.equal(getPaymentTenor(), other.getPaymentTenor()) &&
          JodaBeanUtils.equal(getStubType(), other.getStubType()) &&
          (isIsExchangeNotional() == other.isIsExchangeNotional()) &&
          (getPaymentLag() == other.getPaymentLag()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = hash * 31 + JodaBeanUtils.hashCode(getOvernightIndexConvention());
    hash = hash * 31 + JodaBeanUtils.hashCode(getPaymentTenor());
    hash = hash * 31 + JodaBeanUtils.hashCode(getStubType());
    hash = hash * 31 + JodaBeanUtils.hashCode(isIsExchangeNotional());
    hash = hash * 31 + JodaBeanUtils.hashCode(getPaymentLag());
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(192);
    buf.append("ONCompoundedLegRollDateConvention{");
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
    buf.append("overnightIndexConvention").append('=').append(JodaBeanUtils.toString(getOvernightIndexConvention())).append(',').append(' ');
    buf.append("paymentTenor").append('=').append(JodaBeanUtils.toString(getPaymentTenor())).append(',').append(' ');
    buf.append("stubType").append('=').append(JodaBeanUtils.toString(getStubType())).append(',').append(' ');
    buf.append("isExchangeNotional").append('=').append(JodaBeanUtils.toString(isIsExchangeNotional())).append(',').append(' ');
    buf.append("paymentLag").append('=').append(JodaBeanUtils.toString(getPaymentLag())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code ONCompoundedLegRollDateConvention}.
   */
  public static class Meta extends FinancialConvention.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code overnightIndexConvention} property.
     */
    private final MetaProperty<ExternalId> _overnightIndexConvention = DirectMetaProperty.ofReadWrite(
        this, "overnightIndexConvention", ONCompoundedLegRollDateConvention.class, ExternalId.class);
    /**
     * The meta-property for the {@code paymentTenor} property.
     */
    private final MetaProperty<Tenor> _paymentTenor = DirectMetaProperty.ofReadWrite(
        this, "paymentTenor", ONCompoundedLegRollDateConvention.class, Tenor.class);
    /**
     * The meta-property for the {@code stubType} property.
     */
    private final MetaProperty<StubType> _stubType = DirectMetaProperty.ofReadWrite(
        this, "stubType", ONCompoundedLegRollDateConvention.class, StubType.class);
    /**
     * The meta-property for the {@code isExchangeNotional} property.
     */
    private final MetaProperty<Boolean> _isExchangeNotional = DirectMetaProperty.ofReadWrite(
        this, "isExchangeNotional", ONCompoundedLegRollDateConvention.class, Boolean.TYPE);
    /**
     * The meta-property for the {@code paymentLag} property.
     */
    private final MetaProperty<Integer> _paymentLag = DirectMetaProperty.ofReadWrite(
        this, "paymentLag", ONCompoundedLegRollDateConvention.class, Integer.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "overnightIndexConvention",
        "paymentTenor",
        "stubType",
        "isExchangeNotional",
        "paymentLag");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1218695809:  // overnightIndexConvention
          return _overnightIndexConvention;
        case -507548582:  // paymentTenor
          return _paymentTenor;
        case 1873675528:  // stubType
          return _stubType;
        case 348962765:  // isExchangeNotional
          return _isExchangeNotional;
        case 1612870060:  // paymentLag
          return _paymentLag;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends ONCompoundedLegRollDateConvention> builder() {
      return new DirectBeanBuilder<ONCompoundedLegRollDateConvention>(new ONCompoundedLegRollDateConvention());
    }

    @Override
    public Class<? extends ONCompoundedLegRollDateConvention> beanType() {
      return ONCompoundedLegRollDateConvention.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code overnightIndexConvention} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ExternalId> overnightIndexConvention() {
      return _overnightIndexConvention;
    }

    /**
     * The meta-property for the {@code paymentTenor} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Tenor> paymentTenor() {
      return _paymentTenor;
    }

    /**
     * The meta-property for the {@code stubType} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<StubType> stubType() {
      return _stubType;
    }

    /**
     * The meta-property for the {@code isExchangeNotional} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Boolean> isExchangeNotional() {
      return _isExchangeNotional;
    }

    /**
     * The meta-property for the {@code paymentLag} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Integer> paymentLag() {
      return _paymentLag;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -1218695809:  // overnightIndexConvention
          return ((ONCompoundedLegRollDateConvention) bean).getOvernightIndexConvention();
        case -507548582:  // paymentTenor
          return ((ONCompoundedLegRollDateConvention) bean).getPaymentTenor();
        case 1873675528:  // stubType
          return ((ONCompoundedLegRollDateConvention) bean).getStubType();
        case 348962765:  // isExchangeNotional
          return ((ONCompoundedLegRollDateConvention) bean).isIsExchangeNotional();
        case 1612870060:  // paymentLag
          return ((ONCompoundedLegRollDateConvention) bean).getPaymentLag();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -1218695809:  // overnightIndexConvention
          ((ONCompoundedLegRollDateConvention) bean).setOvernightIndexConvention((ExternalId) newValue);
          return;
        case -507548582:  // paymentTenor
          ((ONCompoundedLegRollDateConvention) bean).setPaymentTenor((Tenor) newValue);
          return;
        case 1873675528:  // stubType
          ((ONCompoundedLegRollDateConvention) bean).setStubType((StubType) newValue);
          return;
        case 348962765:  // isExchangeNotional
          ((ONCompoundedLegRollDateConvention) bean).setIsExchangeNotional((Boolean) newValue);
          return;
        case 1612870060:  // paymentLag
          ((ONCompoundedLegRollDateConvention) bean).setPaymentLag((Integer) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((ONCompoundedLegRollDateConvention) bean)._overnightIndexConvention, "overnightIndexConvention");
      JodaBeanUtils.notNull(((ONCompoundedLegRollDateConvention) bean)._paymentTenor, "paymentTenor");
      JodaBeanUtils.notNull(((ONCompoundedLegRollDateConvention) bean)._stubType, "stubType");
      super.validate(bean);
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

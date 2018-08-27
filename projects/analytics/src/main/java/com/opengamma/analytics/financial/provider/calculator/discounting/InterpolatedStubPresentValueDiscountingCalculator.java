package com.opengamma.analytics.financial.provider.calculator.discounting;

import com.opengamma.analytics.financial.interestrate.InstrumentDerivativeVisitorAdapter;
import com.opengamma.analytics.financial.interestrate.payments.derivative.CouponIbor;
import com.opengamma.analytics.financial.interestrate.payments.derivative.CouponIborCompounding;
import com.opengamma.analytics.financial.interestrate.payments.derivative.CouponIborCompoundingFlatSpread;
import com.opengamma.analytics.financial.interestrate.payments.derivative.CouponIborCompoundingSpread;
import com.opengamma.analytics.financial.interestrate.payments.derivative.CouponIborSpread;
import com.opengamma.analytics.financial.interestrate.payments.derivative.CouponON;
import com.opengamma.analytics.financial.interestrate.payments.derivative.CouponONCompounded;
import com.opengamma.analytics.financial.interestrate.payments.derivative.CouponONSpread;
import com.opengamma.analytics.financial.interestrate.payments.provider.CouponIborCompoundingDiscountingMethod;
import com.opengamma.analytics.financial.interestrate.payments.provider.CouponIborCompoundingFlatSpreadDiscountingMethod;
import com.opengamma.analytics.financial.interestrate.payments.provider.CouponIborCompoundingSpreadDiscountingMethod;
import com.opengamma.analytics.financial.interestrate.payments.provider.CouponIborDiscountingMethod;
import com.opengamma.analytics.financial.interestrate.payments.provider.CouponIborSpreadDiscountingMethod;
import com.opengamma.analytics.financial.interestrate.payments.provider.CouponONArithmeticAverageDiscountingApproxMethod;
import com.opengamma.analytics.financial.interestrate.payments.provider.CouponONArithmeticAverageSpreadDiscountingMethod;
import com.opengamma.analytics.financial.interestrate.payments.provider.CouponONArithmeticAverageSpreadSimplifiedDiscountingApproxMethod;
import com.opengamma.analytics.financial.interestrate.payments.provider.CouponONDiscountingMethod;
import com.opengamma.analytics.financial.interestrate.payments.provider.CouponONSpreadDiscountingMethod;
import com.opengamma.util.money.MultipleCurrencyAmount;

/**
 * Instrument derivative visitor implementation for valuing
 */
public final class InterpolatedStubPresentValueDiscountingCalculator extends InstrumentDerivativeVisitorAdapter<InterpolatedStubData, MultipleCurrencyAmount> {

  private static final InterpolatedStubPresentValueDiscountingCalculator INSTANCE = new InterpolatedStubPresentValueDiscountingCalculator();

  private InterpolatedStubPresentValueDiscountingCalculator() {
  }

  public static InterpolatedStubPresentValueDiscountingCalculator getInstance() {
    return INSTANCE;
  }

  private static final CouponIborDiscountingMethod METHOD_CPN_IBOR = CouponIborDiscountingMethod.getInstance();
  private static final CouponIborSpreadDiscountingMethod METHOD_CPN_IBOR_SPREAD = CouponIborSpreadDiscountingMethod.getInstance();
  private static final CouponIborCompoundingDiscountingMethod METHOD_CPN_IBOR_COMP = CouponIborCompoundingDiscountingMethod.getInstance();
  private static final CouponIborCompoundingFlatSpreadDiscountingMethod METHOD_CPN_IBOR_COMP_FLAT_SPREAD = CouponIborCompoundingFlatSpreadDiscountingMethod.getInstance();
  private static final CouponIborCompoundingSpreadDiscountingMethod METHOD_CPN_IBOR_COMP_SPREAD = CouponIborCompoundingSpreadDiscountingMethod.getInstance();

  private static final CouponONDiscountingMethod METHOD_CPN_ON = CouponONDiscountingMethod.getInstance();
  private static final CouponONSpreadDiscountingMethod METHOD_CPN_ON_SPREAD = CouponONSpreadDiscountingMethod.getInstance();
  private static final CouponONArithmeticAverageDiscountingApproxMethod METHOD_CPN_AAON_APPROX = CouponONArithmeticAverageDiscountingApproxMethod.getInstance();
  private static final CouponONArithmeticAverageSpreadDiscountingMethod METHOD_CPN_AAON_SPREAD = CouponONArithmeticAverageSpreadDiscountingMethod.getInstance();
  private static final CouponONArithmeticAverageSpreadSimplifiedDiscountingApproxMethod METHOD_CPN_ONAA_SPREADSIMPL_APPROX =
      CouponONArithmeticAverageSpreadSimplifiedDiscountingApproxMethod.getInstance();

  @Override
  public MultipleCurrencyAmount visitCouponIbor(final CouponIbor payment, final InterpolatedStubData data) {
    return METHOD_CPN_IBOR.presentValue(payment, data.getMulticurve(), data.getInterpolatedStubCoupon().accept(IborInterpolatedStubForwardRateProviderVisitor.getInstance()));
  }

  @Override
  public MultipleCurrencyAmount visitCouponIborSpread(final CouponIborSpread payment, final InterpolatedStubData data) {
    return METHOD_CPN_IBOR_SPREAD.presentValue(payment, data.getMulticurve(), data.getInterpolatedStubCoupon().accept(IborInterpolatedStubForwardRateProviderVisitor.getInstance()));
  }

  @Override
  public MultipleCurrencyAmount visitCouponIborCompounding(final CouponIborCompounding payment, final InterpolatedStubData data) {
    return METHOD_CPN_IBOR_COMP.presentValue(payment, data.getMulticurve(), data.getInterpolatedStubCoupon().accept(IborInterpolatedStubForwardRateProviderVisitor.getInstance()));
  }

  @Override
  public MultipleCurrencyAmount visitCouponIborCompoundingFlatSpread(final CouponIborCompoundingFlatSpread payment, final InterpolatedStubData data) {
    return METHOD_CPN_IBOR_COMP_FLAT_SPREAD.presentValue(payment, data.getMulticurve(), data.getInterpolatedStubCoupon().accept(IborInterpolatedStubForwardRateProviderVisitor.getInstance()));
  }

  @Override
  public MultipleCurrencyAmount visitCouponIborCompoundingSpread(final CouponIborCompoundingSpread payment, final InterpolatedStubData data) {
    return METHOD_CPN_IBOR_COMP_SPREAD.presentValue(payment, data.getMulticurve(), data.getInterpolatedStubCoupon().accept(IborInterpolatedStubForwardRateProviderVisitor.getInstance()));
  }

  @Override
  public MultipleCurrencyAmount visitCouponOIS(final CouponON payment, final InterpolatedStubData data) {
    return METHOD_CPN_ON.presentValue(payment, data.getMulticurve(), data.getInterpolatedStubCoupon().accept(OvernightIndexInterpolatedStubForwardRateProviderVisitor.getInstance()));
  }

  @Override
  public MultipleCurrencyAmount visitCouponONSpread(final CouponONSpread payment, final InterpolatedStubData data) {
    // TODO Auto-generated method stub
    return super.visitCouponONSpread(payment, data);
  }

  @Override
  public MultipleCurrencyAmount visitCouponONCompounded(final CouponONCompounded payment, final InterpolatedStubData data) {
    // TODO Auto-generated method stub
    return super.visitCouponONCompounded(payment, data);
  }
}

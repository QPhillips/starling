/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.interestrate.payments.provider;

import java.util.function.Function;

import com.opengamma.analytics.financial.interestrate.annuity.derivative.AnnuityPaymentFixed;
import com.opengamma.analytics.financial.interestrate.payments.derivative.CouponCMS;
import com.opengamma.analytics.financial.interestrate.payments.derivative.Payment;
import com.opengamma.analytics.financial.interestrate.swap.derivative.SwapFixedCoupon;
import com.opengamma.analytics.financial.model.interestrate.HullWhiteOneFactorPiecewiseConstantInterestRateModel;
import com.opengamma.analytics.financial.model.interestrate.definition.HullWhiteOneFactorPiecewiseConstantParameters;
import com.opengamma.analytics.financial.provider.calculator.discounting.CashFlowEquivalentCalculator;
import com.opengamma.analytics.financial.provider.description.interestrate.HullWhiteOneFactorProviderInterface;
import com.opengamma.analytics.financial.provider.description.interestrate.MulticurveProviderInterface;
import com.opengamma.analytics.math.MathException;
import com.opengamma.analytics.math.function.Function1dAdapter;
import com.opengamma.analytics.math.integration.RungeKuttaIntegrator1D;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.money.Currency;
import com.opengamma.util.money.MultipleCurrencyAmount;

/**
 * Pricing method of a CMS coupon in the Hull-White (extended Vasicek) model by numerical integration.
 */
public final class CouponCMSHullWhiteNumericalIntegrationMethod {

  /**
   * The method unique instance.
   */
  private static final CouponCMSHullWhiteNumericalIntegrationMethod INSTANCE = new CouponCMSHullWhiteNumericalIntegrationMethod();

  /**
   * Private constructor.
   */
  private CouponCMSHullWhiteNumericalIntegrationMethod() {
  }

  /**
   * Return the unique instance of the class.
   *
   * @return The instance.
   */
  public static CouponCMSHullWhiteNumericalIntegrationMethod getInstance() {
    return INSTANCE;
  }

  /**
   * The model used in computations.
   */
  private static final HullWhiteOneFactorPiecewiseConstantInterestRateModel MODEL = new HullWhiteOneFactorPiecewiseConstantInterestRateModel();
  /**
   * The cash flow equivalent calculator used in computations.
   */
  private static final CashFlowEquivalentCalculator CFEC = CashFlowEquivalentCalculator.getInstance();
  /**
   * Minimal number of integration steps.
   */
  private static final int NB_INTEGRATION = 10;

  /**
   * Compute the present value of a CMS coupon with the Hull-White (extended Vasicek) model by numerical integration.
   *
   * @param cms
   *          The CMS coupon.
   * @param hwMulticurves
   *          The Hull-White and multi-curves provider.
   * @return The present value.
   */
  public MultipleCurrencyAmount presentValue(final CouponCMS cms, final HullWhiteOneFactorProviderInterface hwMulticurves) {
    ArgumentChecker.notNull(cms, "CMS");
    ArgumentChecker.notNull(hwMulticurves, "Hull-White provider");
    final Currency ccy = cms.getCurrency();
    final HullWhiteOneFactorPiecewiseConstantParameters parameters = hwMulticurves.getHullWhiteParameters();
    final MulticurveProviderInterface multicurves = hwMulticurves.getMulticurveProvider();
    final double expiryTime = cms.getFixingTime();
    final SwapFixedCoupon<? extends Payment> swap = cms.getUnderlyingSwap();
    final int nbFixed = cms.getUnderlyingSwap().getFixedLeg().getNumberOfPayments();
    final double[] alphaFixed = new double[nbFixed];
    final double[] dfFixed = new double[nbFixed];
    final double[] discountedCashFlowFixed = new double[nbFixed];
    for (int loopcf = 0; loopcf < nbFixed; loopcf++) {
      alphaFixed[loopcf] = MODEL.alpha(parameters, 0.0, expiryTime, expiryTime, swap.getFixedLeg().getNthPayment(loopcf).getPaymentTime());
      dfFixed[loopcf] = multicurves.getDiscountFactor(ccy, swap.getFixedLeg().getNthPayment(loopcf).getPaymentTime());
      discountedCashFlowFixed[loopcf] = dfFixed[loopcf] * swap.getFixedLeg().getNthPayment(loopcf).getPaymentYearFraction()
          * swap.getFixedLeg().getNthPayment(loopcf).getNotional();
    }
    final AnnuityPaymentFixed cfeIbor = swap.getSecondLeg().accept(CFEC, multicurves);
    final double[] alphaIbor = new double[cfeIbor.getNumberOfPayments()];
    final double[] dfIbor = new double[cfeIbor.getNumberOfPayments()];
    final double[] discountedCashFlowIbor = new double[cfeIbor.getNumberOfPayments()];
    for (int loopcf = 0; loopcf < cfeIbor.getNumberOfPayments(); loopcf++) {
      alphaIbor[loopcf] = MODEL.alpha(parameters, 0.0, expiryTime, expiryTime, cfeIbor.getNthPayment(loopcf).getPaymentTime());
      dfIbor[loopcf] = multicurves.getDiscountFactor(ccy, cfeIbor.getNthPayment(loopcf).getPaymentTime());
      discountedCashFlowIbor[loopcf] = dfIbor[loopcf] * cfeIbor.getNthPayment(loopcf).getAmount();
    }
    final double alphaPayment = MODEL.alpha(parameters, 0.0, expiryTime, expiryTime, cms.getPaymentTime());
    final double dfPayment = multicurves.getDiscountFactor(ccy, cms.getPaymentTime());
    // Integration
    final CMSIntegrant integrant = new CMSIntegrant(discountedCashFlowFixed, alphaFixed, discountedCashFlowIbor, alphaIbor, alphaPayment);
    final double limit = 10.0;
    final double absoluteTolerance = 1.0E-8;
    final double relativeTolerance = 1.0E-9;
    final RungeKuttaIntegrator1D integrator = new RungeKuttaIntegrator1D(absoluteTolerance, relativeTolerance, NB_INTEGRATION);
    double pv = 0.0;
    try {
      pv = 1.0 / Math.sqrt(2.0 * Math.PI) * integrator.integrate(Function1dAdapter.of(integrant), -limit, limit) * dfPayment
          * cms.getNotional() * cms.getPaymentYearFraction();
    } catch (final Exception e) {
      throw new MathException(e);
    }
    return MultipleCurrencyAmount.of(cms.getCurrency(), pv);
  }

  /**
   * Inner class to implement the integration used in price computation.
   */
  private static final class CMSIntegrant implements Function<Double, Double> {

    private final double[] _discountedCashFlowFixed;
    private final double[] _alphaFixed;
    private final double[] _discountedCashFlowIbor;
    private final double[] _alphaIbor;
    private final double _alphaPayment;

    /**
     * Constructor to the integrant function.
     *
     * @param discountedCashFlowFixed
     *          The discounted cash flows of the underlying swap fixed leg.
     * @param alphaFixed
     *          The bond volatilities of the underlying swap fixed leg.
     * @param discountedCashFlowIbor
     *          The discounted cash flows of the underlying swap Ibor leg.
     * @param alphaIbor
     *          The bond volatilities of the underlying swap Ibor leg.
     * @param alphaPayment
     *          The bond volatilities of the payment discount factor.
     */
    CMSIntegrant(final double[] discountedCashFlowFixed, final double[] alphaFixed, final double[] discountedCashFlowIbor,
        final double[] alphaIbor,
        final double alphaPayment) {
      _discountedCashFlowFixed = discountedCashFlowFixed;
      _alphaFixed = alphaFixed;
      _discountedCashFlowIbor = discountedCashFlowIbor;
      _alphaIbor = alphaIbor;
      _alphaPayment = alphaPayment;
    }

    @SuppressWarnings("synthetic-access")
    @Override
    public Double apply(final Double x) {
      final double swapRate = MODEL.swapRate(x, _discountedCashFlowFixed, _alphaFixed, _discountedCashFlowIbor, _alphaIbor);
      final double dfDensity = Math.exp(-(x + _alphaPayment) * (x + _alphaPayment) / 2.0);
      final double result = dfDensity * swapRate;
      return result;
    }
  }

}

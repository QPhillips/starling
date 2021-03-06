/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.interestrate.swap.provider;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;
import org.threeten.bp.Period;
import org.threeten.bp.ZonedDateTime;

import com.mcleodmoores.date.CalendarAdapter;
import com.mcleodmoores.date.WorkingDayCalendar;
import com.opengamma.analytics.financial.instrument.annuity.AnnuityCouponIborDefinition;
import com.opengamma.analytics.financial.instrument.annuity.AnnuityCouponIborSpreadDefinition;
import com.opengamma.analytics.financial.instrument.index.GeneratorSwapFixedIbor;
import com.opengamma.analytics.financial.instrument.index.GeneratorSwapFixedIborMaster;
import com.opengamma.analytics.financial.instrument.index.IborIndex;
import com.opengamma.analytics.financial.instrument.swap.SwapDefinition;
import com.opengamma.analytics.financial.instrument.swap.SwapFixedIborDefinition;
import com.opengamma.analytics.financial.instrument.swap.SwapIborIborDefinition;
import com.opengamma.analytics.financial.interestrate.InstrumentDerivative;
import com.opengamma.analytics.financial.interestrate.annuity.derivative.Annuity;
import com.opengamma.analytics.financial.interestrate.payments.derivative.Coupon;
import com.opengamma.analytics.financial.interestrate.payments.derivative.Payment;
import com.opengamma.analytics.financial.interestrate.swap.derivative.Swap;
import com.opengamma.analytics.financial.interestrate.swap.derivative.SwapFixedCoupon;
import com.opengamma.analytics.financial.model.interestrate.curve.YieldAndDiscountCurve;
import com.opengamma.analytics.financial.model.interestrate.curve.YieldCurve;
import com.opengamma.analytics.financial.provider.calculator.discounting.PV01CurveParametersCalculator;
import com.opengamma.analytics.financial.provider.calculator.discounting.ParSpreadMarketQuoteDiscountingCalculator;
import com.opengamma.analytics.financial.provider.calculator.discounting.PresentValueCurveSensitivityDiscountingCalculator;
import com.opengamma.analytics.financial.provider.calculator.discounting.PresentValueDiscountingCalculator;
import com.opengamma.analytics.financial.provider.calculator.generic.TodayPaymentCalculator;
import com.opengamma.analytics.financial.provider.description.MulticurveProviderDiscountDataSets;
import com.opengamma.analytics.financial.provider.description.interestrate.MulticurveProviderDiscount;
import com.opengamma.analytics.financial.provider.description.interestrate.MulticurveProviderInterface;
import com.opengamma.analytics.financial.provider.sensitivity.multicurve.MultipleCurrencyMulticurveSensitivity;
import com.opengamma.analytics.financial.provider.sensitivity.multicurve.MultipleCurrencyParameterSensitivity;
import com.opengamma.analytics.financial.provider.sensitivity.parameter.ParameterSensitivityParameterCalculator;
import com.opengamma.analytics.financial.schedule.ScheduleCalculator;
import com.opengamma.analytics.math.curve.InterpolatedDoublesCurve;
import com.opengamma.analytics.math.interpolation.Interpolator1D;
import com.opengamma.analytics.math.interpolation.factory.FlatExtrapolator1dAdapter;
import com.opengamma.analytics.math.interpolation.factory.LinearInterpolator1dAdapter;
import com.opengamma.analytics.math.interpolation.factory.NamedInterpolator1dFactory;
import com.opengamma.analytics.util.amount.ReferenceAmount;
import com.opengamma.timeseries.precise.zdt.ImmutableZonedDateTimeDoubleTimeSeries;
import com.opengamma.timeseries.precise.zdt.ZonedDateTimeDoubleTimeSeries;
import com.opengamma.util.money.Currency;
import com.opengamma.util.money.MultipleCurrencyAmount;
import com.opengamma.util.test.TestGroup;
import com.opengamma.util.time.DateUtils;
import com.opengamma.util.tuple.Pair;

/**
 * Test.
 */
@Test(groups = TestGroup.UNIT)
public class SwapCalculatorTest {

  private static final MulticurveProviderDiscount MULTICURVES = MulticurveProviderDiscountDataSets.createMulticurveEurUsd();
  private static final IborIndex[] INDEX_LIST = MulticurveProviderDiscountDataSets.getIndexesIborMulticurveEurUsd();
  private static final IborIndex USDLIBOR3M = INDEX_LIST[2];
  private static final IborIndex USDLIBOR6M = INDEX_LIST[3];
  private static final WorkingDayCalendar NYC = MulticurveProviderDiscountDataSets.getUSDCalendar();

  private static final GeneratorSwapFixedIborMaster GENERATOR_SWAP_MASTER = GeneratorSwapFixedIborMaster.getInstance();

  // Swap Fixed-Ibor
  private static final GeneratorSwapFixedIbor USD6MLIBOR3M = GENERATOR_SWAP_MASTER.getGenerator("USD6MLIBOR3M", NYC);
  private static final Period SWAP_TENOR = Period.ofYears(10);
  private static final ZonedDateTime SETTLEMENT_DATE = DateUtils.getUTCDate(2012, 5, 17);
  private static final double NOTIONAL = 100000000; // 100m
  private static final double RATE_FIXED = 0.025;
  private static final SwapFixedIborDefinition SWAP_FIXED_IBOR_DEFINITION = SwapFixedIborDefinition.from(SETTLEMENT_DATE, SWAP_TENOR,
      USD6MLIBOR3M, NOTIONAL,
      RATE_FIXED, true);

  // Swap Ibor-ibor
  private static final double SPREAD3 = 0.0020;
  private static final double SPREAD6 = 0.0005;
  private static final SwapIborIborDefinition SWAP_IBORSPREAD_IBORSPREAD_DEFINITION = new SwapIborIborDefinition(
      AnnuityCouponIborSpreadDefinition.from(SETTLEMENT_DATE, SWAP_TENOR, NOTIONAL, USDLIBOR3M, SPREAD3, true, CalendarAdapter.of(NYC)),
      AnnuityCouponIborSpreadDefinition.from(SETTLEMENT_DATE, SWAP_TENOR, NOTIONAL, USDLIBOR6M, SPREAD6, false, CalendarAdapter.of(NYC)));
  private static final SwapDefinition SWAP_IBOR_IBORSPREAD_DEFINITION = new SwapDefinition(
      AnnuityCouponIborDefinition.from(SETTLEMENT_DATE, SWAP_TENOR, NOTIONAL, USDLIBOR3M, true, CalendarAdapter.of(NYC)),
      AnnuityCouponIborSpreadDefinition.from(SETTLEMENT_DATE, SWAP_TENOR, NOTIONAL, USDLIBOR6M, SPREAD6, false, CalendarAdapter.of(NYC)));

  // Calculators
  private static final ParSpreadMarketQuoteDiscountingCalculator PSMQDC = ParSpreadMarketQuoteDiscountingCalculator.getInstance();
  private static final PresentValueDiscountingCalculator PVDC = PresentValueDiscountingCalculator.getInstance();
  private static final TodayPaymentCalculator TPC = TodayPaymentCalculator.getInstance();

  private static final ZonedDateTimeDoubleTimeSeries FIXING_TS_3 = ImmutableZonedDateTimeDoubleTimeSeries.ofUTC(
      new ZonedDateTime[] { DateUtils.getUTCDate(2012, 5, 10), DateUtils.getUTCDate(2012, 5, 14), DateUtils.getUTCDate(2012, 5, 15),
          DateUtils.getUTCDate(2012, 5, 16), DateUtils.getUTCDate(2012, 8, 15), DateUtils.getUTCDate(2012, 11, 15) },
      new double[] { 0.0080, 0.0090, 0.0100, 0.0110, 0.0140, 0.0160 });
  private static final ZonedDateTimeDoubleTimeSeries FIXING_TS_6 = ImmutableZonedDateTimeDoubleTimeSeries.ofUTC(
      new ZonedDateTime[] { DateUtils.getUTCDate(2012, 5, 10), DateUtils.getUTCDate(2012, 5, 15), DateUtils.getUTCDate(2012, 5, 16) },
      new double[] { 0.0095, 0.0120, 0.0130 });
  private static final ZonedDateTimeDoubleTimeSeries[] FIXING_TS_3_6 = new ZonedDateTimeDoubleTimeSeries[] { FIXING_TS_3, FIXING_TS_6 };

  private static final PV01CurveParametersCalculator<MulticurveProviderInterface> PV01CPC = new PV01CurveParametersCalculator<>(
      PresentValueCurveSensitivityDiscountingCalculator.getInstance());
  private static final ParameterSensitivityParameterCalculator<MulticurveProviderInterface> PSPVC = new ParameterSensitivityParameterCalculator<>(
      PresentValueCurveSensitivityDiscountingCalculator.getInstance());
  // private static final ParSpreadMarketQuoteCurveSensitivityDiscountingCalculator PSCSC =
  // ParSpreadMarketQuoteCurveSensitivityDiscountingCalculator
  // .getInstance();

  private static final double TOLERANCE_PV = 1.0E-2; // one cent out of 100m

  private static final double BP1 = 1.0E-4; // The size of the scaling: 1 basis point.

  /**
   *
   */
  @Test
  public void parSpreadFixedIborBeforeFirstFixing() {
    final ZonedDateTime referenceDate = DateUtils.getUTCDate(2012, 5, 14);
    final SwapFixedCoupon<Coupon> swap = SWAP_FIXED_IBOR_DEFINITION.toDerivative(referenceDate);
    final double parSpread = swap.accept(PSMQDC, MULTICURVES);
    final SwapFixedIborDefinition swap0Definition = SwapFixedIborDefinition.from(SETTLEMENT_DATE, SWAP_TENOR, USD6MLIBOR3M, NOTIONAL,
        RATE_FIXED + parSpread,
        true);
    final SwapFixedCoupon<Coupon> swap0 = swap0Definition.toDerivative(referenceDate);
    final MultipleCurrencyAmount pv = swap0.accept(PVDC, MULTICURVES);
    assertEquals("ParSpreadCalculator: fixed-coupon swap", pv.getAmount(swap.getFirstLeg().getCurrency()), 0, TOLERANCE_PV);
  }

  /**
   *
   */
  @Test
  public void parSpreadFixedIborAfterFirstFixing() {
    final ZonedDateTime referenceDate = DateUtils.getUTCDate(2012, 5, 16);
    final SwapFixedCoupon<Coupon> swap = SWAP_FIXED_IBOR_DEFINITION.toDerivative(referenceDate, FIXING_TS_3_6);
    final double parSpread = swap.accept(PSMQDC, MULTICURVES);
    final SwapFixedIborDefinition swap0Definition = SwapFixedIborDefinition.from(SETTLEMENT_DATE, SWAP_TENOR, USD6MLIBOR3M, NOTIONAL,
        RATE_FIXED + parSpread,
        true);
    final SwapFixedCoupon<Coupon> swap0 = swap0Definition.toDerivative(referenceDate, FIXING_TS_3_6);
    final MultipleCurrencyAmount pv = swap0.accept(PVDC, MULTICURVES);
    assertEquals("ParSpreadCalculator: fixed-coupon swap", pv.getAmount(swap.getFirstLeg().getCurrency()), 0, TOLERANCE_PV);
  }

  /**
   *
   */
  @Test
  public void parSpreadIborSpreadIborSpreadBeforeFirstFixing() {
    final ZonedDateTime referenceDate = DateUtils.getUTCDate(2012, 5, 14);
    final Swap<Coupon, Coupon> swap = SWAP_IBORSPREAD_IBORSPREAD_DEFINITION.toDerivative(referenceDate);
    final double parSpread = swap.accept(PSMQDC, MULTICURVES);
    final SwapIborIborDefinition swap0Definition = new SwapIborIborDefinition(
        AnnuityCouponIborSpreadDefinition.from(SETTLEMENT_DATE, SWAP_TENOR, NOTIONAL, USDLIBOR3M, SPREAD3 + parSpread, true,
            CalendarAdapter.of(NYC)),
        AnnuityCouponIborSpreadDefinition.from(SETTLEMENT_DATE, SWAP_TENOR, NOTIONAL, USDLIBOR6M, SPREAD6, false, CalendarAdapter.of(NYC)));
    final Swap<Coupon, Coupon> swap0 = swap0Definition.toDerivative(referenceDate);
    final MultipleCurrencyAmount pv = swap0.accept(PVDC, MULTICURVES);
    assertEquals("ParSpreadCalculator: fixed-coupon swap", pv.getAmount(swap.getFirstLeg().getCurrency()), 0, TOLERANCE_PV);
  }

  /**
   *
   */
  @Test
  public void parSpreadIborSpreadIborSpreadAfterFirstFixing() {
    final ZonedDateTime referenceDate = DateUtils.getUTCDate(2012, 5, 16);
    final Swap<Coupon, Coupon> swap = SWAP_IBORSPREAD_IBORSPREAD_DEFINITION.toDerivative(referenceDate, FIXING_TS_3_6);
    final double parSpread = swap.accept(PSMQDC, MULTICURVES);
    final SwapIborIborDefinition swap0Definition = new SwapIborIborDefinition(
        AnnuityCouponIborSpreadDefinition.from(SETTLEMENT_DATE, SWAP_TENOR, NOTIONAL, USDLIBOR3M, SPREAD3 + parSpread, true,
            CalendarAdapter.of(NYC)),
        AnnuityCouponIborSpreadDefinition.from(SETTLEMENT_DATE, SWAP_TENOR, NOTIONAL, USDLIBOR6M, SPREAD6, false, CalendarAdapter.of(NYC)));
    final Swap<Coupon, Coupon> swap0 = swap0Definition.toDerivative(referenceDate, FIXING_TS_3_6);
    final MultipleCurrencyAmount pv = swap0.accept(PVDC, MULTICURVES);
    assertEquals("ParSpreadCalculator: fixed-coupon swap", pv.getAmount(swap.getFirstLeg().getCurrency()), 0, TOLERANCE_PV);
  }

  /**
   * Test for a swap with first leg without spread and par spread computed on that leg.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void parSpreadIborIborBeforeFirstFixing() {
    final ZonedDateTime referenceDate = DateUtils.getUTCDate(2012, 5, 14);
    final Swap<? extends Payment, ? extends Payment> swap = new Swap<>(
        (Annuity<Payment>) SWAP_IBOR_IBORSPREAD_DEFINITION.getFirstLeg().toDerivative(referenceDate),
        (Annuity<Payment>) SWAP_IBOR_IBORSPREAD_DEFINITION.getSecondLeg().toDerivative(referenceDate));
    final double parSpread = swap.accept(PSMQDC, MULTICURVES);
    final SwapIborIborDefinition swap0Definition = new SwapIborIborDefinition(
        AnnuityCouponIborSpreadDefinition.from(SETTLEMENT_DATE, SWAP_TENOR, NOTIONAL, USDLIBOR3M, parSpread, true, CalendarAdapter.of(NYC)),
        AnnuityCouponIborSpreadDefinition.from(SETTLEMENT_DATE, SWAP_TENOR, NOTIONAL, USDLIBOR6M, SPREAD6, false, CalendarAdapter.of(NYC)));
    final Swap<Coupon, Coupon> swap0 = swap0Definition.toDerivative(referenceDate);
    final MultipleCurrencyAmount pv = swap0.accept(PVDC, MULTICURVES);
    assertEquals("ParSpreadCalculator: fixed-coupon swap", pv.getAmount(swap.getFirstLeg().getCurrency()), 0, TOLERANCE_PV);
  }

  /**
   *
   */
  @Test
  public void pv01CurveParametersBeforeFirstFixing() {
    final ZonedDateTime referenceDate = DateUtils.getUTCDate(2012, 5, 14);
    final SwapFixedCoupon<Coupon> swap = SWAP_FIXED_IBOR_DEFINITION.toDerivative(referenceDate);
    final ReferenceAmount<Pair<String, Currency>> pv01Computed = swap.accept(PV01CPC, MULTICURVES);
    final ReferenceAmount<Pair<String, Currency>> pv01Expected = new ReferenceAmount<>();
    final MultipleCurrencyParameterSensitivity pvps = PSPVC.calculateSensitivity(swap, MULTICURVES, MULTICURVES.getAllNames());
    for (final Pair<String, Currency> nameCcy : pvps.getAllNamesCurrency()) {
      double total = 0.0;
      final double[] array = pvps.getSensitivity(nameCcy).getData();
      for (final double element : array) {
        total += element;
      }
      total *= BP1;
      pv01Expected.add(nameCcy, total);
    }
    assertEquals("PV01CurveParametersCalculator: fixed-coupon swap", pv01Expected, pv01Computed);
  }

  // TODO
  // /**
  // *
  // */
  // @Test
  // public void parSpreadCurveSensitivityFixedIborBeforeFirstFixing() {
  // final ZonedDateTime referenceDate = DateUtils.getUTCDate(2012, 5, 14);
  // final SwapFixedCoupon<Coupon> swap = SWAP_FIXED_IBOR_DEFINITION.toDerivative(referenceDate);
  // MulticurveSensitivity pscsComputed = swap.accept(PSCSC, MULTICURVES);
  // pscsComputed = pscsComputed.cleaned();
  // final double[] timesDsc = new double[swap.getSecondLeg().getNumberOfPayments()];
  // for (int loopcpn = 0; loopcpn < swap.getSecondLeg().getNumberOfPayments(); loopcpn++) {
  // timesDsc[loopcpn] = swap.getSecondLeg().getNthPayment(loopcpn).getPaymentTime();
  // }
  // final List<DoublesPair> sensiDscFD = FDCurveSensitivityCalculator.curveSensitvityFDCalculator(swap, PSC, CURVES,
  // swap.getFirstLeg().getDiscountCurve(),
  // timesDsc, 1.0E-10);
  // final List<DoublesPair> sensiDscComputed = pscsComputed.getSensitivities().get(swap.getFirstLeg().getDiscountCurve());
  // assertTrue("parSpread: curve sensitivity - dsc",
  // InterestRateCurveSensitivityUtils.compare(sensiDscFD, sensiDscComputed, TOLERANCE_SPREAD_DELTA));
  // final Set<Double> timesFwdSet = new TreeSet<>();
  // for (int loopcpn = 0; loopcpn < swap.getSecondLeg().getNumberOfPayments(); loopcpn++) {
  // timesFwdSet.add(((CouponIbor) swap.getSecondLeg().getNthPayment(loopcpn)).getFixingPeriodStartTime());
  // timesFwdSet.add(((CouponIbor) swap.getSecondLeg().getNthPayment(loopcpn)).getFixingPeriodEndTime());
  // }
  // final Double[] timesFwd = timesFwdSet.toArray(new Double[timesFwdSet.size()]);
  // final List<DoublesPair> sensiFwdFD = FDCurveSensitivityCalculator.curveSensitvityFDCalculator(swap, PSC, CURVES, fwdCurveName,
  // ArrayUtils.toPrimitive(timesFwd), 1.0E-10);
  // final List<DoublesPair> sensiFwdComputed = pscsComputed.getSensitivities().get(fwdCurveName);
  // assertTrue("parSpread: curve sensitivity - fwd",
  // InterestRateCurveSensitivityUtils.compare(sensiFwdFD, sensiFwdComputed, TOLERANCE_SPREAD_DELTA));
  // }

  /**
   *
   */
  @Test
  public void todayPaymentFixedIborBeforeFirstFixing() {
    final ZonedDateTime referenceDate = DateUtils.getUTCDate(2012, 5, 14);
    final SwapFixedCoupon<Coupon> swap = SWAP_FIXED_IBOR_DEFINITION.toDerivative(referenceDate);
    final MultipleCurrencyAmount cash = swap.accept(TPC);
    assertEquals("TodayPaymentCalculator: fixed-coupon swap", 0.0, cash.getAmount(USDLIBOR3M.getCurrency()), TOLERANCE_PV);
    assertEquals("TodayPaymentCalculator: fixed-coupon swap", 1, cash.getCurrencyAmounts().length);
  }

  /**
   *
   */
  @Test
  public void todayPaymentFixedIborOnFirstIborPayment() {
    final ZonedDateTime referenceDate = DateUtils.getUTCDate(2012, 8, 17);
    final SwapFixedCoupon<Coupon> swap = SWAP_FIXED_IBOR_DEFINITION.toDerivative(referenceDate, FIXING_TS_3_6);
    final MultipleCurrencyAmount cash = swap.accept(TPC);
    assertEquals("TodayPaymentCalculator: fixed-coupon swap",
        0.0100 * NOTIONAL * SWAP_FIXED_IBOR_DEFINITION.getIborLeg().getNthPayment(0).getPaymentYearFraction(),
        cash.getAmount(USDLIBOR3M.getCurrency()),
        TOLERANCE_PV);
    assertEquals("TodayPaymentCalculator: fixed-coupon swap", 1, cash.getCurrencyAmounts().length);
  }

  /**
   *
   */
  @Test
  public void todayPaymentFixedIborOnFirstFixedPayment() {
    final ZonedDateTime referenceDate = DateUtils.getUTCDate(2012, 11, 19);
    final SwapFixedCoupon<Coupon> swap = SWAP_FIXED_IBOR_DEFINITION.toDerivative(referenceDate, FIXING_TS_3_6);
    final MultipleCurrencyAmount cash = swap.accept(TPC);
    assertEquals("TodayPaymentCalculator: fixed-coupon swap",
        SWAP_FIXED_IBOR_DEFINITION.getFixedLeg().getNthPayment(0).getAmount()
            + 0.0140 * NOTIONAL * SWAP_FIXED_IBOR_DEFINITION.getIborLeg().getNthPayment(1).getPaymentYearFraction(),
        cash.getAmount(USDLIBOR3M.getCurrency()), TOLERANCE_PV);
    assertEquals("TodayPaymentCalculator: fixed-coupon swap", 1, cash.getCurrencyAmounts().length);
  }

  /**
   *
   */
  @Test
  public void todayPaymentFixedIborBetweenPayments() {
    final ZonedDateTime referenceDate = DateUtils.getUTCDate(2012, 11, 14);
    final SwapFixedCoupon<Coupon> swap = SWAP_FIXED_IBOR_DEFINITION.toDerivative(referenceDate, FIXING_TS_3_6);
    final MultipleCurrencyAmount cash = swap.accept(TPC);
    assertEquals("TodayPaymentCalculator: fixed-coupon swap", 0.0, cash.getAmount(USDLIBOR3M.getCurrency()), TOLERANCE_PV);
    assertEquals("TodayPaymentCalculator: fixed-coupon swap", 1, cash.getCurrencyAmounts().length);
  }

  /**
   *
   */
  @Test(enabled = false)
  public void presentValuePerformance() {

    long startTime, endTime;
    final int nbTest = 100;

    final PresentValueDiscountingCalculator pvdCalculator = PresentValueDiscountingCalculator.getInstance();
    final PresentValueCurveSensitivityDiscountingCalculator pvcsdCalculator = PresentValueCurveSensitivityDiscountingCalculator
        .getInstance();
    final ParameterSensitivityParameterCalculator<MulticurveProviderInterface> psCalculator = new ParameterSensitivityParameterCalculator<>(
        pvcsdCalculator);

    final ZonedDateTime referenceDate = DateUtils.getUTCDate(2012, 5, 14);

    final int nbSwap = 100;
    final MultipleCurrencyAmount[] pv = new MultipleCurrencyAmount[nbSwap];
    final MultipleCurrencyMulticurveSensitivity[] pvcs = new MultipleCurrencyMulticurveSensitivity[nbSwap];
    final MultipleCurrencyParameterSensitivity[] ps = new MultipleCurrencyParameterSensitivity[nbSwap];

    final InstrumentDerivative[] swap = new InstrumentDerivative[nbSwap];

    for (int i = 0; i < nbSwap; i++) {
      final double rate = RATE_FIXED - 0.0050 + i * BP1;
      final SwapFixedIborDefinition swapDefinition = SwapFixedIborDefinition.from(SETTLEMENT_DATE, SWAP_TENOR, USD6MLIBOR3M, NOTIONAL, rate,
          true);
      swap[i] = swapDefinition.toDerivative(referenceDate);
    }

    startTime = System.currentTimeMillis();
    for (int i = 0; i < nbTest; i++) {
      for (int loops = 0; loops < nbSwap; loops++) {
        pv[loops] = swap[loops].accept(pvdCalculator, MULTICURVES);
      }
    }
    endTime = System.currentTimeMillis();
    System.out.println("SwapCalculatorTest: " + nbTest + " x " + nbSwap + " swaps (5Y/Q) - present value " + (endTime - startTime) + " ms");
    // Performance note: Discounting price: 11-Jun-2014: On Mac Pro 3.2 GHz Quad-Core Intel Xeon: 225 ms for 100x100 swaps.

    startTime = System.currentTimeMillis(); // Swap construction + PV
    for (int i = 0; i < nbTest; i++) {
      for (int j = 0; j < nbSwap; j++) {
        final double rate = RATE_FIXED - 0.0050 + j * BP1;
        final SwapFixedIborDefinition swapDefinition = SwapFixedIborDefinition.from(SETTLEMENT_DATE, SWAP_TENOR, USD6MLIBOR3M, NOTIONAL,
            rate, true);
        swap[j] = swapDefinition.toDerivative(referenceDate);
      }
      for (int j = 0; j < nbSwap; j++) {
        pv[j] = swap[j].accept(pvdCalculator, MULTICURVES);
      }
    }
    endTime = System.currentTimeMillis();
    System.out.println(
        "SwapCalculatorTest: " + nbTest + " x " + nbSwap + " swaps (5Y/Q) - construction + present value " + (endTime - startTime) + " ms");
    // Performance note: Discounting price: 11-Jun-2014: On Mac Pro 3.2 GHz Quad-Core Intel Xeon: 1280 ms for 100x100 swaps.

    startTime = System.currentTimeMillis();
    for (int looptest = 0; looptest < nbTest; looptest++) {
      for (int loops = 0; loops < nbSwap; loops++) {
        pv[loops] = swap[loops].accept(pvdCalculator, MULTICURVES);
        pvcs[loops] = swap[loops].accept(pvcsdCalculator, MULTICURVES);

      }
    }
    endTime = System.currentTimeMillis();
    System.out.println(
        "SwapCalculatorTest: " + nbTest + " x " + nbSwap + " swaps (5Y/Q) - present value + present value curve sensitivity "
            + (endTime - startTime) + " ms");
    // Performance note: Discounting price: 11-Jun-2014: On Mac Pro 3.2 GHz Quad-Core Intel Xeon: 930 ms for 100x100 swaps.

    startTime = System.currentTimeMillis();
    for (int i = 0; i < nbTest; i++) {
      for (int j = 0; j < nbSwap; j++) {
        pv[j] = swap[j].accept(pvdCalculator, MULTICURVES);
        ps[j] = psCalculator.calculateSensitivity(swap[j], MULTICURVES, MULTICURVES.getAllNames());
      }
    }
    endTime = System.currentTimeMillis();
    System.out
        .println("SwapCalculatorTest: " + nbTest + " x " + nbSwap + " swaps (5Y/Q) - present value + present value parameters sensitivity "
            + (endTime - startTime) + " ms");
    // Performance note: Discounting price: 11-Jun-2014: On Mac Pro 3.2 GHz Quad-Core Intel Xeon: 1325 ms for 100x100 swaps.

  }

  // @Test
  // public void thetaFixedIborBeforeFirstFixing() {
  // final ZonedDateTime referenceDate = DateUtils.getUTCDate(2012, 5, 11);
  // final MultipleCurrencyAmount theta = THETAC.getTheta(SWAP_FIXED_IBOR_DEFINITION, referenceDate, CURVE_NAMES, CURVES, FIXING_TS_3_6, 1);
  // final SwapFixedCoupon<Coupon> swapToday = SWAP_FIXED_IBOR_DEFINITION.toDerivative(referenceDate, FIXING_TS_3_6, CURVE_NAMES);
  // final SwapFixedCoupon<Coupon> swapTomorrow = SWAP_FIXED_IBOR_DEFINITION.toDerivative(referenceDate.plusDays(1), FIXING_TS_3_6,
  // CURVE_NAMES);
  // final double pvToday = PVC.visit(swapToday, CURVES);
  // final YieldCurveBundle tomorrowData = CURVE_ROLLDOWN.rollDown(CURVES, TimeCalculator.getTimeBetween(referenceDate,
  // referenceDate.plusDays(1)));
  // final double pvTomorrow = PVC.visit(swapTomorrow, tomorrowData);
  // assertEquals("ThetaCalculator: fixed-coupon swap", pvTomorrow - pvToday, theta.getAmount(USDLIBOR3M.getCurrency()), TOLERANCE_PV);
  // assertEquals("ThetaCalculator: fixed-coupon swap", 1, theta.getCurrencyAmounts().length);
  // }
  //
  // @Test
  // public void thetaFixedIborOneDayBeforeFirstFixing() {
  // final ZonedDateTime referenceDate = DateUtils.getUTCDate(2012, 5, 14);
  // final MultipleCurrencyAmount theta = THETAC.getTheta(SWAP_FIXED_IBOR_DEFINITION, referenceDate, CURVE_NAMES, CURVES, FIXING_TS_3_6, 1);
  // final SwapFixedCoupon<Coupon> swapToday = SWAP_FIXED_IBOR_DEFINITION.toDerivative(referenceDate, FIXING_TS_3_6, CURVE_NAMES);
  // final ArrayZonedDateTimeDoubleTimeSeries fixing3extended = ImmutableZonedDateTimeDoubleTimeSeries.ofUTC(new ZonedDateTime[]
  // {DateUtils.getUTCDate(2012, 5,
  // 14), DateUtils.getUTCDate(2012, 5, 15) },
  // new double[] {0.0090, 0.0090 });
  // final ArrayZonedDateTimeDoubleTimeSeries[] fixing36 = new ArrayZonedDateTimeDoubleTimeSeries[] {fixing3extended, FIXING_TS_6 };
  // final SwapFixedCoupon<Coupon> swapTomorrow = SWAP_FIXED_IBOR_DEFINITION.toDerivative(referenceDate.plusDays(1), fixing36, CURVE_NAMES);
  // final double pvToday = PVC.visit(swapToday, CURVES);
  // final YieldCurveBundle tomorrowData = CURVE_ROLLDOWN.rollDown(CURVES, TimeCalculator.getTimeBetween(referenceDate,
  // referenceDate.plusDays(1)));
  // final double pvTomorrow = PVC.visit(swapTomorrow, tomorrowData);
  // assertEquals("ThetaCalculator: fixed-coupon swap", pvTomorrow - pvToday, theta.getAmount(USDLIBOR3M.getCurrency()), TOLERANCE_PV);
  // assertEquals("ThetaCalculator: fixed-coupon swap", 1, theta.getCurrencyAmounts().length);
  // }
  //
  // @Test
  // public void thetaFixedIborOverFirstPayment() {
  // final ZonedDateTime referenceDate = DateUtils.getUTCDate(2012, 8, 17);
  // final MultipleCurrencyAmount theta = THETAC.getTheta(SWAP_FIXED_IBOR_DEFINITION, referenceDate, CURVE_NAMES, CURVES, FIXING_TS_3_6, 1);
  // final SwapFixedCoupon<Coupon> swapToday = SWAP_FIXED_IBOR_DEFINITION.toDerivative(referenceDate, FIXING_TS_3_6, CURVE_NAMES);
  // final SwapFixedCoupon<Coupon> swapTomorrow = SWAP_FIXED_IBOR_DEFINITION.toDerivative(referenceDate.plusDays(1), FIXING_TS_3_6,
  // CURVE_NAMES);
  // final double pvToday = PVC.visit(swapToday, CURVES);
  // final YieldCurveBundle tomorrowData = CURVE_ROLLDOWN.rollDown(CURVES, TimeCalculator.getTimeBetween(referenceDate,
  // referenceDate.plusDays(1)));
  // final double pvTomorrow = PVC.visit(swapTomorrow, tomorrowData);
  // final double todayCash = ((CouponFixed) swapToday.getSecondLeg().getNthPayment(0)).getAmount();
  // assertEquals("ThetaCalculator: fixed-coupon swap", pvTomorrow - (pvToday - todayCash), theta.getAmount(USDLIBOR3M.getCurrency()),
  // TOLERANCE_PV);
  // assertEquals("ThetaCalculator: fixed-coupon swap", 1, theta.getCurrencyAmounts().length);
  // }

  // Create a very simplified example of swap and curve to produce a detailed workout of AD for curve sensitivity
  private static final Interpolator1D LINEAR_FLAT = NamedInterpolator1dFactory.of(LinearInterpolator1dAdapter.NAME,
      FlatExtrapolator1dAdapter.NAME, FlatExtrapolator1dAdapter.NAME);
  private static final double[] CURVE_TIME = new double[] { 0.25, 0.50, 1.00, 2.00 };
  private static final double[] CURVE_RATE = new double[] { 0.01, 0.0125, 0.0150, 0.0175 };
  private static final String CURVE_NAME = "USD All";
  private static final YieldAndDiscountCurve CURVE_SIMPLE = new YieldCurve(CURVE_NAME,
      new InterpolatedDoublesCurve(CURVE_TIME, CURVE_RATE, LINEAR_FLAT, true, CURVE_NAME));
  private static final MulticurveProviderDiscount MULTICURVE_SIMPLIFIED = new MulticurveProviderDiscount();
  static {
    MULTICURVE_SIMPLIFIED.setCurve(Currency.USD, CURVE_SIMPLE);
    MULTICURVE_SIMPLIFIED.setCurve(USDLIBOR3M, CURVE_SIMPLE);
  }
  private static final ZonedDateTime REFERENCE_DATE = DateUtils.getUTCDate(2014, 6, 13);
  private static final ZonedDateTime SPOT_DATE = ScheduleCalculator.getAdjustedDate(REFERENCE_DATE, USDLIBOR3M.getSpotLag(), NYC);
  private static final Period SWAP_SIMPLE_START = Period.ofMonths(6);
  private static final Period SWAP_SIMPLE_TENOR = Period.ofYears(1);
  private static final ZonedDateTime SETTLEMENT_DATE_SIMPLE = ScheduleCalculator.getAdjustedDate(SPOT_DATE, SWAP_SIMPLE_START, USDLIBOR3M,
      NYC);
  private static final double NOTIONAL_SIMPLE = 100000000; // 100m
  private static final double RATE_FIXED_SIMPLE = 0.0200;
  private static final SwapFixedIborDefinition SWAP_SIMPLE_DEFINITION = SwapFixedIborDefinition.from(SETTLEMENT_DATE_SIMPLE,
      SWAP_SIMPLE_TENOR, USD6MLIBOR3M,
      NOTIONAL_SIMPLE, RATE_FIXED_SIMPLE, true);
  private static final SwapFixedCoupon<Coupon> SWAP_SIMPLE = SWAP_SIMPLE_DEFINITION.toDerivative(REFERENCE_DATE);

  private static final PresentValueCurveSensitivityDiscountingCalculator PVCSDC = PresentValueCurveSensitivityDiscountingCalculator
      .getInstance();
  private static final ParameterSensitivityParameterCalculator<MulticurveProviderInterface> PSC = new ParameterSensitivityParameterCalculator<>(
      PVCSDC);

  /**
   *
   */
  @SuppressWarnings("unused")
  @Test(enabled = false)
  public void workoutADExample() {
    final MultipleCurrencyMulticurveSensitivity pvcs = SWAP_SIMPLE.accept(PVCSDC, MULTICURVE_SIMPLIFIED);
    final MultipleCurrencyParameterSensitivity ps = PSC.calculateSensitivity(SWAP_SIMPLE, MULTICURVE_SIMPLIFIED);
  }

}

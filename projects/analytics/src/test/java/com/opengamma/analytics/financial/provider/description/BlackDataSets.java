/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.provider.description;

import com.mcleodmoores.date.WeekendWorkingDayCalendar;
import com.mcleodmoores.date.WorkingDayCalendar;
import com.opengamma.analytics.financial.instrument.index.GeneratorSwapFixedCompoundedONCompounded;
import com.opengamma.analytics.financial.instrument.index.GeneratorSwapFixedCompoundedONCompoundedMaster;
import com.opengamma.analytics.financial.instrument.index.GeneratorSwapFixedIbor;
import com.opengamma.analytics.financial.instrument.index.GeneratorSwapFixedIborMaster;
import com.opengamma.analytics.financial.model.option.parameters.BlackFlatSwaptionParameters;
import com.opengamma.analytics.math.interpolation.GridInterpolator2D;
import com.opengamma.analytics.math.interpolation.Interpolator1D;
import com.opengamma.analytics.math.interpolation.factory.FlatExtrapolator1dAdapter;
import com.opengamma.analytics.math.interpolation.factory.LinearInterpolator1dAdapter;
import com.opengamma.analytics.math.interpolation.factory.NamedInterpolator1dFactory;
import com.opengamma.analytics.math.surface.InterpolatedDoublesSurface;

/**
 * Sets of market data used in tests.
 */
public class BlackDataSets {

  private static final Interpolator1D LINEAR_FLAT = NamedInterpolator1dFactory.of(LinearInterpolator1dAdapter.NAME,
      FlatExtrapolator1dAdapter.NAME,
      FlatExtrapolator1dAdapter.NAME);
  private static final GridInterpolator2D INTERPOLATOR_LINEAR_2D = new GridInterpolator2D(LINEAR_FLAT, LINEAR_FLAT);

  private static final WorkingDayCalendar CALENDAR = WeekendWorkingDayCalendar.SATURDAY_SUNDAY;
  private static final GeneratorSwapFixedIborMaster GENERATOR_SWAP_MASTER = GeneratorSwapFixedIborMaster.getInstance();
  private static final GeneratorSwapFixedIbor EUR1YEURIBOR6M = GENERATOR_SWAP_MASTER.getGenerator("EUR1YEURIBOR6M", CALENDAR);
  private static final GeneratorSwapFixedIbor EUR1YEURIBOR3M = GENERATOR_SWAP_MASTER.getGenerator("EUR1YEURIBOR3M", CALENDAR);
  private static final GeneratorSwapFixedCompoundedONCompounded BRLCDI = GeneratorSwapFixedCompoundedONCompoundedMaster.getInstance()
      .getGenerator("BRLCDI", CALENDAR);

  private static final InterpolatedDoublesSurface BLACK_SURFACE_EXP_TEN = InterpolatedDoublesSurface.from(
      new double[] { 0.5, 1.0, 5.0, 0.5, 1.0, 5.0 },
      new double[] { 2, 2, 2, 10, 10, 10 },
      new double[] { 0.35, 0.34, 0.25, 0.30, 0.25, 0.20 },
      INTERPOLATOR_LINEAR_2D);
  private static final InterpolatedDoublesSurface BLACK_SURFACE_EXP_STR = InterpolatedDoublesSurface.from(
      new double[] { 0.5, 1.0, 5.0, 0.5, 1.0, 5.0, 0.5, 1.0, 5.0 },
      new double[] { 0.01, 0.01, 0.01, 0.02, 0.02, 0.02, 0.03, 0.03, 0.03 },
      new double[] { 0.35, 0.34, 0.25, 0.30, 0.25, 0.20, 0.28, 0.23, 0.18 },
      INTERPOLATOR_LINEAR_2D);
  private static final BlackFlatSwaptionParameters BLACK_SWAPTION_EUR6 = new BlackFlatSwaptionParameters(BLACK_SURFACE_EXP_TEN,
      EUR1YEURIBOR6M);
  private static final BlackFlatSwaptionParameters BLACK_SWAPTION_EUR3 = new BlackFlatSwaptionParameters(BLACK_SURFACE_EXP_TEN,
      EUR1YEURIBOR3M);
  private static final BlackFlatSwaptionParameters BLACK_SWAPTION_BRL = new BlackFlatSwaptionParameters(BLACK_SURFACE_EXP_TEN, BRLCDI);

  public static InterpolatedDoublesSurface createBlackSurfaceExpiryTenor() {
    return BLACK_SURFACE_EXP_TEN;
  }

  public static InterpolatedDoublesSurface createBlackSurfaceExpiryStrike() {
    return BLACK_SURFACE_EXP_STR;
  }

  public static InterpolatedDoublesSurface createBlackSurfaceExpiryTenorShift(final double shift) {
    return InterpolatedDoublesSurface.from(new double[] { 0.5, 1.0, 5.0, 0.5, 1.0, 5.0 }, new double[] { 2, 2, 2, 10, 10, 10 },
        new double[] { 0.35 + shift, 0.34 + shift, 0.25 + shift, 0.30 + shift,
            0.25 + shift, 0.20 + shift },
        INTERPOLATOR_LINEAR_2D);
  }

  public static InterpolatedDoublesSurface createBlackSurfaceExpiryStrikeShift(final double shift) {
    return InterpolatedDoublesSurface.from(new double[] { 0.5, 1.0, 5.0, 0.5, 1.0, 5.0, 0.5, 1.0, 5.0 },
        new double[] { 0.01, 0.01, 0.01, 0.02, 0.02, 0.02, 0.03, 0.03, 0.03 },
        new double[] { 0.35 + shift, 0.34 + shift, 0.25 + shift, 0.30 + shift, 0.25 + shift, 0.20 + shift, 0.28 + shift, 0.23 + shift,
            0.18 + shift },
        INTERPOLATOR_LINEAR_2D);
  }

  public static BlackFlatSwaptionParameters createBlackSwaptionEUR6() {
    return BLACK_SWAPTION_EUR6;
  }

  public static BlackFlatSwaptionParameters createBlackSwaptionEUR3() {
    return BLACK_SWAPTION_EUR3;
  }

  public static BlackFlatSwaptionParameters createBlackSwaptionBrl() {
    return BLACK_SWAPTION_BRL;
  }

  public static WorkingDayCalendar getBrlCalendar() {
    return CALENDAR;
  }

  /**
   * Create the same surface as createBlackSwaptionEUR6() but with a given parallel shift.
   *
   * @param shift
   *          The shift.
   * @return The surface.
   */
  public static BlackFlatSwaptionParameters createBlackSwaptionEUR6Shift(final double shift) {
    final InterpolatedDoublesSurface surfaceShift = createBlackSurfaceExpiryTenorShift(shift);
    return new BlackFlatSwaptionParameters(surfaceShift, EUR1YEURIBOR6M);
  }

  /**
   * Create the same surface as createBlackSwaptionEUR6() but with one volatility shifted.
   *
   * @param index
   *          The index of the shifted volatility.
   * @param shift
   *          The shift.
   * @return The surface.
   */
  public static BlackFlatSwaptionParameters createBlackSwaptionEUR6Shift(final int index, final double shift) {
    final double[] vol = new double[] { 0.35, 0.34, 0.25, 0.30, 0.25, 0.20 };
    vol[index] += shift;
    final InterpolatedDoublesSurface surfaceShift = InterpolatedDoublesSurface.from(new double[] { 0.5, 1.0, 5.0, 0.5, 1.0, 5.0 },
        new double[] { 2, 2, 2, 10, 10, 10 }, vol, INTERPOLATOR_LINEAR_2D);
    return new BlackFlatSwaptionParameters(surfaceShift, EUR1YEURIBOR6M);
  }

  /**
   * Create the same surface as createBlackSwaptionEUR6() but with a given parallel shift.
   *
   * @param shift
   *          The shift.
   * @return The surface.
   */
  public static BlackFlatSwaptionParameters createBlackSwaptionBRLShift(final double shift) {
    final InterpolatedDoublesSurface surfaceShift = createBlackSurfaceExpiryTenorShift(shift);
    return new BlackFlatSwaptionParameters(surfaceShift, BRLCDI);
  }
}

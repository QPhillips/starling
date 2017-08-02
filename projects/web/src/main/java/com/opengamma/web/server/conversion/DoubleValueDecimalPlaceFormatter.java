/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.web.server.conversion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormatSymbols;

/**
 * Formats double values to a fixed number of decimal places.
 */
public class DoubleValueDecimalPlaceFormatter extends DoubleValueFormatter {

  public static final DoubleValueDecimalPlaceFormatter NON_CCY_2DP = DoubleValueDecimalPlaceFormatter.of(2, false);
  public static final DoubleValueDecimalPlaceFormatter NON_CCY_3DP = DoubleValueDecimalPlaceFormatter.of(3, false);
  public static final DoubleValueDecimalPlaceFormatter NON_CCY_4DP = DoubleValueDecimalPlaceFormatter.of(4, false);
  public static final DoubleValueDecimalPlaceFormatter NON_CCY_6DP = DoubleValueDecimalPlaceFormatter.of(6, false);
  public static final DoubleValueDecimalPlaceFormatter CCY_2DP = DoubleValueDecimalPlaceFormatter.of(2, true);
  public static final DoubleValueDecimalPlaceFormatter CCY_4DP = DoubleValueDecimalPlaceFormatter.of(4, true);
  public static final DoubleValueDecimalPlaceFormatter CCY_6DP = DoubleValueDecimalPlaceFormatter.of(6, true);

  private final int _decimalPlaces;

  public DoubleValueDecimalPlaceFormatter(final int decimalPlaces, final boolean isCurrencyAmount) {
    this(decimalPlaces, isCurrencyAmount, DecimalFormatSymbols.getInstance());
  }

  public DoubleValueDecimalPlaceFormatter(final int decimalPlaces, final boolean isCurrencyAmount, final DecimalFormatSymbols formatSymbols) {
    super(isCurrencyAmount, formatSymbols);
    _decimalPlaces = decimalPlaces;
  }

  public static DoubleValueDecimalPlaceFormatter of(final int decimalPlaces, final boolean isCurrencyAmount) {
    return new DoubleValueDecimalPlaceFormatter(decimalPlaces, isCurrencyAmount);
  }

  @Override
  public BigDecimal process(final BigDecimal value) {
    return value.setScale(_decimalPlaces, RoundingMode.HALF_UP);
  }

}

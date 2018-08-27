/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.web.server.conversion;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormatSymbols;

/**
 *
 */
public class PercentageValueSignificantFiguresFormatter extends DoubleValueFormatter {
  /** Formats numbers to five significant figures */
  public static final PercentageValueSignificantFiguresFormatter NON_CCY_5SF = PercentageValueSignificantFiguresFormatter.of(5, false);

  private final BigDecimal _maxValueForSigFig;
  private final MathContext _sigFigMathContext;

  public PercentageValueSignificantFiguresFormatter(final int significantFigures, final boolean isCurrencyAmount) {
    this(significantFigures, isCurrencyAmount, DecimalFormatSymbols.getInstance());
  }

  public PercentageValueSignificantFiguresFormatter(final int significantFigures, final boolean isCurrencyAmount, final DecimalFormatSymbols formatSymbols) {
    super(isCurrencyAmount, formatSymbols);
    _maxValueForSigFig = BigDecimal.TEN.pow(significantFigures - 1);
    _sigFigMathContext = new MathContext(significantFigures, RoundingMode.HALF_UP);
  }

  public static PercentageValueSignificantFiguresFormatter of(final int significantFigures, final boolean isCurrencyAmount) {
    return new PercentageValueSignificantFiguresFormatter(significantFigures, isCurrencyAmount);
  }

  @Override
  public BigDecimal process(final BigDecimal bigDecimalValue) {
    if (bigDecimalValue.abs().compareTo(_maxValueForSigFig) > 0) {
      return bigDecimalValue.setScale(0, RoundingMode.HALF_UP);
    } else {
      return bigDecimalValue.round(_sigFigMathContext);
    }
  }

  @Override
  public String format(final BigDecimal value) {
    final BigDecimal processedValue = process(value.multiply(BigDecimal.valueOf(100)));
    final String plainString = super.transformPlainNumberString(processedValue.toPlainString());
    return plainString + "%";
  }

}

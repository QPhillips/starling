/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.provider.calculator.blackforex;

import com.opengamma.analytics.financial.forex.derivative.ForexOptionVanilla;
import com.opengamma.analytics.financial.forex.provider.ForexOptionVanillaBlackSmileMethod;
import com.opengamma.analytics.financial.interestrate.InstrumentDerivativeVisitorAdapter;
import com.opengamma.analytics.financial.provider.description.forex.BlackForexSmileProviderInterface;
import com.opengamma.util.money.CurrencyAmount;

/**
 * Calculates the value theta (first order derivative with respect to time) using the forward driftless theta
 * for Forex derivatives in the Black (Garman-Kohlhagen) world. The theta is not scaled and so the value produced is an annual amount.
 */
public class ValueThetaForexBlackSmileCalculator extends InstrumentDerivativeVisitorAdapter<BlackForexSmileProviderInterface, CurrencyAmount> {

  /**
   * The unique instance of the calculator.
   */
  private static final ValueThetaForexBlackSmileCalculator INSTANCE = new ValueThetaForexBlackSmileCalculator();

  /**
   * Gets the calculator instance.
   * @return The calculator.
   */
  public static ValueThetaForexBlackSmileCalculator getInstance() {
    return INSTANCE;
  }

  /**
   * Constructor.
   */
  ValueThetaForexBlackSmileCalculator() {
  }

  @Override
  public CurrencyAmount visitForexOptionVanilla(final ForexOptionVanilla option, final BlackForexSmileProviderInterface marketData) {
    return ForexOptionVanillaBlackSmileMethod.getInstance().thetaTheoretical(option, marketData);
  }
}

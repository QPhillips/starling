/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 *
 * Modified by McLeod Moores Software Limited.
 *
 * Copyright (C) 2016 - present McLeod Moores Software Limited.  All rights reserved.
 */
package com.opengamma.analytics.financial.provider.calculator.blackforex;

import com.opengamma.analytics.financial.forex.derivative.ForexOptionVanilla;
import com.opengamma.analytics.financial.forex.provider.ForexOptionVanillaBlackFlatMethod;
import com.opengamma.analytics.financial.interestrate.InstrumentDerivativeVisitorAdapter;
import com.opengamma.analytics.financial.provider.description.forex.BlackForexFlatProviderInterface;
import com.opengamma.util.money.MultipleCurrencyAmount;

/**
 * Calculates the currency exposure of FX options using a Black model without smile.
 */
public final class CurrencyExposureForexBlackFlatCalculator extends InstrumentDerivativeVisitorAdapter<BlackForexFlatProviderInterface, MultipleCurrencyAmount> {

  /**
   * The unique instance of the calculator.
   */
  private static final CurrencyExposureForexBlackFlatCalculator INSTANCE = new CurrencyExposureForexBlackFlatCalculator();

  /**
   * Constructor.
   */
  private CurrencyExposureForexBlackFlatCalculator() {
  }

  /**
   * Gets the calculator instance.
   * @return The calculator.
   */
  public static CurrencyExposureForexBlackFlatCalculator getInstance() {
    return INSTANCE;
  }

  @Override
  public MultipleCurrencyAmount visitForexOptionVanilla(final ForexOptionVanilla option, final BlackForexFlatProviderInterface marketData) {
    return ForexOptionVanillaBlackFlatMethod.getInstance().currencyExposure(option, marketData);
  }

}

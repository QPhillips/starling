/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.horizon.rolldown;

import org.apache.commons.lang.NotImplementedException;

import com.opengamma.analytics.financial.horizon.RolldownFunction;
import com.opengamma.analytics.financial.model.volatility.surface.SmileDeltaTermStructureParametersStrikeInterpolation;
import com.opengamma.analytics.financial.provider.description.forex.BlackForexSmileProviderDiscount;
import com.opengamma.analytics.financial.provider.description.forex.BlackForexSmileProviderInterface;
import com.opengamma.analytics.financial.provider.description.interestrate.MulticurveProviderDiscount;
import com.opengamma.analytics.financial.provider.description.interestrate.ParameterProviderInterface;
import com.opengamma.util.money.Currency;
import com.opengamma.util.tuple.Pair;

/**
 *
 */
public final class FxOptionBlackDataConstantSpreadRolldown implements RolldownFunction<BlackForexSmileProviderInterface> {
  /**
   * A static instance.
   */
  public static final FxOptionBlackDataConstantSpreadRolldown INSTANCE = new FxOptionBlackDataConstantSpreadRolldown();

  /**
   * Private constructor
   */
  private FxOptionBlackDataConstantSpreadRolldown() {
  }

  @Override
  public BlackForexSmileProviderInterface rollDown(final BlackForexSmileProviderInterface data, final double shiftTime) {
    final ParameterProviderInterface shiftedCurves = CurveProviderConstantSpreadRolldown.INSTANCE.rollDown(data, shiftTime);
    final Pair<Currency, Currency> currencyPair = data.getCurrencyPair();
    final SmileDeltaTermStructureParametersStrikeInterpolation volatilityData = data.getVolatility();
    final SmileDeltaTermStructureParametersStrikeInterpolation smile = new SmileDeltaTermStructureParametersStrikeInterpolation(
        volatilityData.getVolatilityTerm(),
        volatilityData.getStrikeInterpolator()) {

      @Override
      public double getVolatility(final double time, final double strike, final double forward) {
        return volatilityData.getVolatility(time + shiftTime, strike, forward);
      }
    };
    if (shiftedCurves instanceof MulticurveProviderDiscount) {
      return new BlackForexSmileProviderDiscount((MulticurveProviderDiscount) shiftedCurves, smile, currencyPair);
    }
    throw new NotImplementedException();
  }

}

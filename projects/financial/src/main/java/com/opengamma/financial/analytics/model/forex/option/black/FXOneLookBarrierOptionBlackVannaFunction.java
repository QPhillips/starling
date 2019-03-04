/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.forex.option.black;

import java.util.Set;

import org.apache.commons.lang.Validate;

import com.opengamma.analytics.financial.forex.derivative.ForexOptionVanilla;
import com.opengamma.analytics.financial.forex.method.ForexOptionVanillaBlackSmileMethod;
import com.opengamma.analytics.financial.model.option.definition.ForexOptionDataBundle;
import com.opengamma.analytics.financial.model.option.definition.SmileDeltaTermStructureDataBundle;
import com.opengamma.engine.value.ValueRequirementNames;
import com.opengamma.util.money.CurrencyAmount;

/**
 *
 * @deprecated Deprecated
 */
@Deprecated
public class FXOneLookBarrierOptionBlackVannaFunction extends FXOneLookBarrierOptionBlackFunction {

  public FXOneLookBarrierOptionBlackVannaFunction() {
    super(ValueRequirementNames.VALUE_VANNA);
  }

  /** The pricing method, Black (Garman-Kohlhagen) */
  private static final ForexOptionVanillaBlackSmileMethod METHOD = ForexOptionVanillaBlackSmileMethod.getInstance();

  @Override
  protected Object computeValues(final Set<ForexOptionVanilla> vanillaOptions, final ForexOptionDataBundle<?> market) {
    Validate.isTrue(market instanceof SmileDeltaTermStructureDataBundle, "FXOneLookBarrierOptionBlackVannaFunction requires a Vol surface with a smile.");
    double sum = 0.0;
    for (final ForexOptionVanilla derivative : vanillaOptions) {
      final CurrencyAmount vannaCcy = METHOD.vanna(derivative, market);
      sum += vannaCcy.getAmount();
    }
    return sum;
  }

}

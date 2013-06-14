/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.integration.marketdata.manipulator.dsl;

import java.util.Set;
import java.util.regex.Pattern;

import com.opengamma.core.marketdatasnapshot.YieldCurveKey;
import com.opengamma.engine.marketdata.manipulator.StructureType;
import com.opengamma.util.money.Currency;

/**
 *
 */
public class CurveSelector extends Selector<YieldCurveKey> {

  /* package */ CurveSelector(String calcConfigName, Set<String> names, Set<Currency> currencies, Pattern namePattern) {
    super(calcConfigName, names, currencies, namePattern, YieldCurveKey.class, StructureType.YIELD_CURVE);
  }

  @Override
  boolean matches(YieldCurveKey key) {
    return matches(key.getName(), key.getCurrency());
  }


  public static class Builder extends Selector.Builder {

    /* package */ Builder(Scenario scenario, String calcConfigName) {
      super(scenario, calcConfigName);
    }

    public CurveManipulatorBuilder apply() {
      return new CurveManipulatorBuilder(selector(), getScenario());
    }

    @Override
    public Builder named(String... names) {
      super.named(names);
      return this;
    }

    @Override
    public Builder currencies(String... codes) {
      super.currencies(codes);
      return this;
    }

    @Override
    public Builder nameMatches(String regex) {
      super.nameMatches(regex);
      return this;
    }

    /**
     * This is package scoped for testing
     * @return A selector built from this builder's data
     */
    /* package */ CurveSelector selector() {
      return new CurveSelector(getCalcConfigName(), getNames(), getCurrencies(), getNamePattern());
    }
  }
}
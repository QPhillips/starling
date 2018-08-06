/**
 * Copyright (C) 2018 - present McLeod Moores Software Limited.  All rights reserved.
 */
package com.mcleodmoores.analytics.financial.bond;

import com.opengamma.analytics.financial.interestrate.bond.definition.BondFixedSecurity;

/**
 * An interface for yield convention types e.g. US street or JGB yield.
 */
public interface YieldConventionType {

  /**
   * A visitor-style method that dispatches from the yield convention defined in the bond security to
   * the particular method for that yield in the calculator.
   *
   * @param <RESULT_TYPE>  the type of the result
   * @param visitor  the visitor, not null
   * @param bond  the bond security, not null
   * @return  the result
   */
  <RESULT_TYPE> RESULT_TYPE accept(YieldConventionTypeVisitor<RESULT_TYPE> visitor, BondFixedSecurity bond);
}

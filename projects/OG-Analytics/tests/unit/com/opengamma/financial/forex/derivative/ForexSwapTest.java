/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.forex.derivative;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import javax.time.calendar.ZonedDateTime;

import org.testng.annotations.Test;

import com.opengamma.financial.forex.definition.ForexDefinition;
import com.opengamma.util.money.Currency;
import com.opengamma.util.time.DateUtil;

/**
 * Tests related to the construction of Forex Swap instruments.
 */
public class ForexSwapTest {

  private static final Currency CUR_1 = Currency.EUR;
  private static final Currency CUR_2 = Currency.USD;
  private static final ZonedDateTime NEAR_DATE = DateUtil.getUTCDate(2011, 5, 26);
  private static final ZonedDateTime FAR_DATE = DateUtil.getUTCDate(2011, 6, 27); // 1m
  private static final double NOMINAL_1 = 100000000;
  private static final double FX_RATE = 1.4177;
  private static final double FORWARD_POINTS = -0.0007;

  private static final ForexDefinition FX_NEAR_DEFINITION = new ForexDefinition(CUR_1, CUR_2, NEAR_DATE, NOMINAL_1, FX_RATE);
  private static final ForexDefinition FX_FAR_DEFINITION = new ForexDefinition(CUR_1, CUR_2, FAR_DATE, -NOMINAL_1, FX_RATE + FORWARD_POINTS);

  private static final String DISCOUNTING_CURVE_NAME_CUR_1 = "Discounting EUR";
  private static final String DISCOUNTING_CURVE_NAME_CUR_2 = "Discounting USD";
  private static final String[] CURVES_NAME = new String[] {DISCOUNTING_CURVE_NAME_CUR_1, DISCOUNTING_CURVE_NAME_CUR_2};
  private static final ZonedDateTime REFERENCE_DATE = DateUtil.getUTCDate(2011, 5, 20);

  private static final Forex FX_NEAR = FX_NEAR_DEFINITION.toDerivative(REFERENCE_DATE, CURVES_NAME);
  private static final Forex FX_FAR = FX_FAR_DEFINITION.toDerivative(REFERENCE_DATE, CURVES_NAME);

  private static final ForexSwap FX_SWAP = new ForexSwap(FX_NEAR, FX_FAR);

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullNearLeg() {
    new ForexSwap(null, FX_FAR);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullFarLeg() {
    new ForexSwap(FX_NEAR, null);
  }

  @Test
  /**
   * Tests the class getters.
   */
  public void getter() {
    assertEquals(FX_NEAR, FX_SWAP.getNearLeg());
    assertEquals(FX_FAR, FX_SWAP.getFarLeg());
  }

  @Test
  /**
   * Tests the class equal and hashCode
   */
  public void equalHash() {
    assertTrue(FX_SWAP.equals(FX_SWAP));
    ForexSwap newFxSwap = new ForexSwap(FX_NEAR, FX_FAR);
    assertTrue(FX_SWAP.equals(newFxSwap));
    assertTrue(FX_SWAP.hashCode() == newFxSwap.hashCode());
    ForexSwap modifiedFxSwap;
    modifiedFxSwap = new ForexSwap(FX_FAR, FX_FAR);
    assertFalse(FX_SWAP.equals(modifiedFxSwap));
    modifiedFxSwap = new ForexSwap(FX_NEAR, FX_NEAR);
    assertFalse(FX_SWAP.equals(modifiedFxSwap));
    assertFalse(FX_SWAP.equals(FX_NEAR));
    assertFalse(FX_SWAP.equals(null));
  }

}

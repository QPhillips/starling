/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.analytics.math.statistics.distribution.fnlib;

import com.opengamma.analytics.math.MathException;

/**
 * Computes the n-term Chebychev series at point 'x'. This code is an approximate translation of the equivalent function in the "Public Domain" code from
 * SLATEC, see: http://www.netlib.org/slatec/fnlib/dcsevl.f
 */
public class DCSEVL {

  /**
   * Numerically, one plus machine precision
   */
  private static double s_onepl;
  static {
    s_onepl = 1.0 + D1MACH.four();
  }

  /**
   * Computes the n-term Chebychev series at point 'x'.
   * 
   * @param x
   *          the position for evaluation
   * @param cs
   *          the terms of the Chebychev series
   * @param n
   *          the number of terms in the double[] cs
   * @return the evaluated series
   */
  public static double getDCSEVL(final double x, final double[] cs, final int n) {
    if (cs == null) {
      throw new MathException("DCSEVL: cs is null");
    }
    if (n < 1) {
      throw new MathException("DCSEVL: number of terms < 0");
    }
    if (n > 1000) {
      throw new MathException("DCSEVL: number of terms > 1000");
    }
    if (Math.abs(x) > s_onepl) {
      throw new MathException("DCSEVL: x outside of the interval [-1,+1)");
    }
    if (n > cs.length) {
      throw new MathException("DCSEVL: number of terms to compute greater than number of coefficients given (n>cs.length)");
    }
    double b2, b1, b0, twoX;
    b2 = 0;
    b1 = 0;
    b0 = 0;
    twoX = 2 * x;
    int ni;
    for (int i = 0; i < n; i++) {
      b2 = b1;
      b1 = b0;
      ni = n - 1 - i;
      b0 = twoX * b1 - b2 + cs[ni];
    }
    return 0.5d * (b0 - b2);
  }
}

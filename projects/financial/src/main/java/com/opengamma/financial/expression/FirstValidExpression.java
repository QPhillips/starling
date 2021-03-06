/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.expression;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a sequence of expressions, evaluating to the first that is valid.
 */
/* package */class FirstValidExpression extends UserExpression {

  private final List<UserExpression> _exprs;

  FirstValidExpression(final List<UserExpression> exprs) {
    _exprs = new ArrayList<>(exprs);
  }

  protected List<UserExpression> getExprs() {
    return _exprs;
  }

  @Override
  protected Object evaluate(final Evaluator evaluator) {
    for (final UserExpression expr : getExprs()) {
      final Object exprResult = expr.evaluate(evaluator);
      if (exprResult != NA) {
        return exprResult;
      }
    }
    return NA;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    for (final UserExpression expr : _exprs) {
      if (sb.length() != 0) {
        sb.append(";\n");
      }
      sb.append(expr);
    }
    return sb.toString();
  }

}

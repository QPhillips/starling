/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.function.exclusion;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNull;

import org.testng.annotations.Test;
import org.threeten.bp.Instant;

import com.opengamma.engine.function.CompiledFunctionDefinition;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.function.FunctionDefinition;
import com.opengamma.engine.function.FunctionParameters;
import com.opengamma.util.test.TestGroup;

/**
 * Tests the function exclusion group class.
 */
@Test(groups = TestGroup.UNIT)
public class FunctionExclusionGroupTest {

  private static final class Function implements FunctionDefinition {

    private final String _shortName;

    private Function(final String shortName) {
      _shortName = shortName;
    }

    @Override
    public void init(final FunctionCompilationContext context) {
      throw new UnsupportedOperationException();
    }

    @Override
    public CompiledFunctionDefinition compile(final FunctionCompilationContext context, final Instant atInstant) {
      throw new UnsupportedOperationException();
    }

    @Override
    public String getUniqueId() {
      throw new UnsupportedOperationException();
    }

    @Override
    public String getShortName() {
      return _shortName;
    }

    @Override
    public FunctionParameters getDefaultParameters() {
      throw new UnsupportedOperationException();
    }

  }

  public void testAbstract() {
    final FunctionExclusionGroups groups = new AbstractFunctionExclusionGroups() {
      @Override
      protected String getKey(final FunctionDefinition function) {
        if (function.getShortName().startsWith("A_")) {
          return "A";
        } else if (function.getShortName().startsWith("B_")) {
          return "B";
        } else {
          return null;
        }
      }
    };
    final FunctionDefinition aFoo = new Function("A_foo");
    final FunctionDefinition aBar = new Function("A_bar");
    final FunctionDefinition bFoo = new Function("B_foo");
    final FunctionDefinition bBar = new Function("B_bar");
    final FunctionDefinition foo = new Function("foo");
    final FunctionDefinition bar = new Function("bar");
    assertNull(groups.getExclusionGroup(foo));
    assertNull(groups.getExclusionGroup(bar));
    final FunctionExclusionGroup afoo1 = groups.getExclusionGroup(aFoo);
    final FunctionExclusionGroup afoo2 = groups.getExclusionGroup(aFoo);
    assertEquals(afoo1, afoo2);
    final FunctionExclusionGroup abar = groups.getExclusionGroup(aBar);
    assertEquals(abar, afoo1);
    final FunctionExclusionGroup bfoo = groups.getExclusionGroup(bFoo);
    assertNotEquals(abar, bfoo);
    final FunctionExclusionGroup bbar = groups.getExclusionGroup(bBar);
    assertEquals(bbar, bfoo);
  }

}

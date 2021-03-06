/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.integration.marketdata.manipulator.dsl;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.opengamma.analytics.ShiftType;

/**
 * The type of shift. Relative will scale the curve by a percentage value; Absolute will add an absolute number of basis points.
 */
public enum ScenarioShiftType implements GroovyAliasable {

  /**
   * Relative shift.
   */
  RELATIVE("Relative") {

    @Override
    public ShiftType toAnalyticsType() {
      return ShiftType.RELATIVE;
    }
  },

  /**
   * Absolute shift.
   */
  ABSOLUTE("Absolute") {

    @Override
    public ShiftType toAnalyticsType() {
      return ShiftType.ABSOLUTE;
    }
  };

  private static final ImmutableList<String> ALIASES;
  static {
    final List<String> result = newArrayList();
    for (final GroovyAliasable value : values()) {
      result.add(value.getGroovyAlias());
    }
    Collections.sort(result);
    ALIASES = ImmutableList.copyOf(result);

  }

  private final String _groovyAlias;

  ScenarioShiftType(final String groovyAlias) {
    _groovyAlias = groovyAlias;
  }

  /**
   * The alias to use in the groovy script.
   *
   * @return the alias
   */
  @Override
  public String getGroovyAlias() {
    return _groovyAlias;
  }

  /**
   * The list of available groovy aliases, sorted.
   *
   * @return list of aliases.
   */
  public static ImmutableList<String> getAliasList() {
    return ALIASES;
  }

  /**
   * Converts this enum to the appropriate {@link ShiftType}.
   *
   * @return The analytics equivalent shift type
   */
  public abstract ShiftType toAnalyticsType();
}

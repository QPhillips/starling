/**
 * Copyright (C) 2018 - present McLeod Moores Software Limited.  All rights reserved.
 */
package com.opengamma.web.json;

import org.threeten.bp.LocalTime;

import com.opengamma.core.id.ExternalSchemes;
import com.opengamma.financial.convention.IborIndexConvention;
import com.opengamma.financial.convention.businessday.BusinessDayConventions;
import com.opengamma.financial.convention.daycount.DayCounts;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.money.Currency;

/**
 * Custom JSON builder to convert an {@link IborIndexConvention} to JSON and back again.
 */
public final class IborIndexConventionJsonBuilder extends AbstractJSONBuilder<IborIndexConvention> {
  /**
   * Static instance.
   */
  public static final IborIndexConventionJsonBuilder INSTANCE = new IborIndexConventionJsonBuilder();

  @Override
  public IborIndexConvention fromJSON(final String json) {
    return fromJSON(IborIndexConvention.class, ArgumentChecker.notNull(json, "json"));
  }

  @Override
  public String toJSON(final IborIndexConvention object) {
    return fudgeToJson(ArgumentChecker.notNull(object, "object"));
  }

  @Override
  public String getTemplate() {
    return IborIndexConventionJsonBuilder.INSTANCE.toJSON(getDummyIborIndexConvention());
  }

  private static IborIndexConvention getDummyIborIndexConvention() {
    return new IborIndexConvention("XXXX", ExternalIdBundle.EMPTY, DayCounts.ACT_360, BusinessDayConventions.MODIFIED_FOLLOWING,
        2, false, Currency.USD, LocalTime.of(11, 0), "", ExternalSchemes.financialRegionId("US"), ExternalSchemes.financialRegionId("US"),
        "");
  }

  private IborIndexConventionJsonBuilder() {
  }
}

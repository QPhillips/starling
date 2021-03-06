/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.pnl;

import org.threeten.bp.Clock;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Period;

import com.opengamma.core.historicaltimeseries.HistoricalTimeSeries;
import com.opengamma.core.position.PositionOrTrade;
import com.opengamma.core.security.Security;
import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.target.ComputationTargetType;
import com.opengamma.engine.value.ValueRequirementNames;
import com.opengamma.financial.analytics.timeseries.DateConstraint;
import com.opengamma.financial.security.FinancialSecurityUtils;
import com.opengamma.financial.security.bond.BondSecurity;
import com.opengamma.financial.security.fx.FXForwardSecurity;
import com.opengamma.financial.security.option.FXBarrierOptionSecurity;
import com.opengamma.financial.security.option.FXDigitalOptionSecurity;
import com.opengamma.financial.security.option.FXOptionSecurity;

/**
 *
 */
public class TradeExchangeTradedDailyPnLFunction extends AbstractTradeOrDailyPositionPnLFunction {

  private static final int MAX_DAYS_OLD = 7;

  /**
   * @param resolutionKey
   *          the resolution key, not-null
   * @param markDataField
   *          the mark to market data field name, not-null
   * @param costOfCarryField
   *          the cost of carry field name, not-null
   */
  public TradeExchangeTradedDailyPnLFunction(final String resolutionKey, final String markDataField, final String costOfCarryField) {
    super(resolutionKey, markDataField, costOfCarryField);
  }

  @Override
  public boolean canApplyTo(final FunctionCompilationContext context, final ComputationTarget target) {
    if (!super.canApplyTo(context, target)) {
      return false;
    }
    final Security security = target.getTrade().getSecurity();
    if (security instanceof FXForwardSecurity || security instanceof FXOptionSecurity || security instanceof FXBarrierOptionSecurity
        || security instanceof FXDigitalOptionSecurity) {
      return false;
    }
    return FinancialSecurityUtils.isExchangeTraded(security) || security instanceof BondSecurity;
  }

  @Override
  public String getShortName() {
    return "TradeDailyPnL";
  }

  @Override
  public ComputationTargetType getTargetType() {
    return ComputationTargetType.TRADE;
  }

  @Override
  protected LocalDate getPreferredTradeDate(final Clock valuationClock, final PositionOrTrade positionOrTrade) {
    return LocalDate.now(valuationClock).minusDays(1);
  }

  @Override
  protected DateConstraint getTimeSeriesStartDate(final PositionOrTrade positionOrTrade) {
    return DateConstraint.VALUATION_TIME.minus(Period.ofDays(MAX_DAYS_OLD + 1)); // yesterday - MAX_DAYS_OLD
  }

  @Override
  protected DateConstraint getTimeSeriesEndDate(final PositionOrTrade positionOrTrade) {
    return DateConstraint.VALUATION_TIME.minus(Period.ofDays(1));
  }

  @Override
  protected LocalDate checkAvailableData(final LocalDate originalTradeDate, final HistoricalTimeSeries markToMarketSeries, final Security security,
      final String markDataField, final String resolutionKey) {
    if (markToMarketSeries.getTimeSeries().isEmpty() || markToMarketSeries.getTimeSeries().getLatestValue() == null) {
      throw new NullPointerException("Could not get mark to market value for security "
          + security.getExternalIdBundle() + " for " + markDataField + " using " + resolutionKey + " for " + MAX_DAYS_OLD + " back from " + originalTradeDate);
    }
    return markToMarketSeries.getTimeSeries().getLatestTime();
  }

  @Override
  protected String getResultValueRequirementName() {
    return ValueRequirementNames.DAILY_PNL;
  }

}

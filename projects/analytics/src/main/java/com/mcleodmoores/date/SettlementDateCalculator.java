/**
 * Copyright (C) 2014 - present McLeod Moores Software Limited.  All rights reserved.
 */
package com.mcleodmoores.date;

import org.threeten.bp.LocalDate;

/**
 * Calculates the settlement date given a number of days to settle and a calendar. Settlement date
 * calculation rules are different depending on the asset class.
 *
 * @param <T>  the type of the working day calendar
 */
public interface SettlementDateCalculator<T extends WorkingDayCalendar> {

  /**
   * Calculates the settlement date.
   * @param date  the date, not null
   * @param daysToSettle  the number of days to settle
   * @param calendar  the working day calendar, not null
   * @return  the settlement date
   */
  LocalDate getSettlementDate(LocalDate date, int daysToSettle, T calendar);

}

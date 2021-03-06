/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.interestrate;

import java.util.ArrayList;
import java.util.List;

import com.opengamma.analytics.financial.interestrate.annuity.derivative.Annuity;
import com.opengamma.analytics.financial.interestrate.annuity.derivative.AnnuityCouponFixed;
import com.opengamma.analytics.financial.interestrate.annuity.derivative.AnnuityCouponIborRatchet;
import com.opengamma.analytics.financial.interestrate.payments.derivative.Payment;
import com.opengamma.util.money.CurrencyAmount;

/**
 * Gets the forward rates for an annuity given a bundle of yield curves.
 *
 * @deprecated Moved to {@link com.opengamma.analytics.financial.interestrate.swap.provider.AnnuityPaymentAmountsVisitor}
 */
@Deprecated
public final class AnnuityPaymentAmountsVisitor extends InstrumentDerivativeVisitorAdapter<Void, CurrencyAmount[]> {
  /** Gets the fixed rates for coupons */
  private static final InstrumentDerivativeVisitor<Void, CurrencyAmount> COUPON_VISITOR = new CouponPaymentVisitor();
  /** The singleton instance */
  private static final InstrumentDerivativeVisitor<Void, CurrencyAmount[]> INSTANCE = new AnnuityPaymentAmountsVisitor();

  /**
   * Gets the singleton instance.
   * 
   * @return The instance
   */
  public static InstrumentDerivativeVisitor<Void, CurrencyAmount[]> getInstance() {
    return INSTANCE;
  }

  /**
   * Private constructor.
   */
  private AnnuityPaymentAmountsVisitor() {
  }

  @Override
  public CurrencyAmount[] visitGenericAnnuity(final Annuity<? extends Payment> annuity) {
    final int n = annuity.getNumberOfPayments();
    final List<CurrencyAmount> ca = new ArrayList<>();
    int count = 0;
    for (int i = 0; i < n; i++) {
      final Payment payment = annuity.getNthPayment(i);
      try {
        ca.add(payment.accept(COUPON_VISITOR));
      } catch (final UnsupportedOperationException e) {
        ca.add(null);
      }
      count++;
    }
    return ca.toArray(new CurrencyAmount[count]);
  }

  @Override
  public CurrencyAmount[] visitFixedCouponAnnuity(final AnnuityCouponFixed annuity) {
    return visitGenericAnnuity(annuity);
  }

  @Override
  public CurrencyAmount[] visitAnnuityCouponIborRatchet(final AnnuityCouponIborRatchet annuity) {
    return visitGenericAnnuity(annuity);
  }
}

/**
 * Copyright (C) 2015-Present McLeod Moores Software Limited.  All rights reserved.
 */
package com.opengamma.financial.analytics.curve;

import static com.opengamma.financial.analytics.curve.CurveNodeCurrencyVisitorTest.EMPTY_CONFIG_SOURCE;
import static com.opengamma.financial.analytics.curve.CurveNodeCurrencyVisitorTest.EMPTY_CONVENTION_SOURCE;
import static com.opengamma.financial.analytics.curve.CurveNodeCurrencyVisitorTest.EMPTY_SECURITY_SOURCE;
import static com.opengamma.financial.analytics.curve.CurveNodeCurrencyVisitorTest.SCHEME;
import static com.opengamma.financial.analytics.curve.CurveNodeCurrencyVisitorTest.US;
import static org.testng.Assert.assertEquals;

import java.util.Collections;

import org.testng.annotations.Test;

import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.core.config.ConfigSource;
import com.opengamma.core.security.Security;
import com.opengamma.core.security.SecuritySource;
import com.opengamma.financial.analytics.curve.CurveNodeCurrencyVisitorTest.MyConfigSource;
import com.opengamma.financial.analytics.curve.CurveNodeCurrencyVisitorTest.MySecuritySource;
import com.opengamma.financial.analytics.ircurve.CurveInstrumentProvider;
import com.opengamma.financial.analytics.ircurve.StaticCurveInstrumentProvider;
import com.opengamma.financial.analytics.ircurve.strips.BondNode;
import com.opengamma.financial.convention.daycount.DayCounts;
import com.opengamma.financial.convention.frequency.PeriodFrequency;
import com.opengamma.financial.convention.yield.SimpleYieldConvention;
import com.opengamma.financial.security.bond.GovernmentBondSecurity;
import com.opengamma.financial.security.cash.CashSecurity;
import com.opengamma.id.ExternalId;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.master.security.ManageableSecurity;
import com.opengamma.util.money.Currency;
import com.opengamma.util.time.DateUtils;
import com.opengamma.util.time.Expiry;
import com.opengamma.util.time.Tenor;

/**
 * Tests the retrieval of a currency from cash nodes.
 */
public class BondNodeCurrencyVisitorTest {
  /** The curve instrument provider */
  private static final CurveInstrumentProvider CURVE_INSTRUMENT_PROVIDER = new StaticCurveInstrumentProvider(ExternalId.of(SCHEME, "ISIN"));
  /** The curve node id mapper name */
  private static final String CNIM_NAME = "CNIM";

  /**
   * Tests the behaviour when the config source is null.
   */
  @Test(expectedExceptions = OpenGammaRuntimeException.class)
  public void testNullConfigSource() {
    final BondNode node = new BondNode(Tenor.ONE_YEAR, CNIM_NAME);
    node.accept(new CurveNodeCurrencyVisitor(EMPTY_CONVENTION_SOURCE, EMPTY_SECURITY_SOURCE));
  }

  /**
   * Tests the behaviour when there is no curve node id mapper with the same name as that in the node
   * in the config source.
   */
  @Test(expectedExceptions = OpenGammaRuntimeException.class)
  public void testNoCurveNodeIdMapper() {
    final BondNode node = new BondNode(Tenor.ONE_YEAR, CNIM_NAME);
    node.accept(new CurveNodeCurrencyVisitor(EMPTY_CONVENTION_SOURCE, EMPTY_SECURITY_SOURCE, EMPTY_CONFIG_SOURCE));
  }

  /**
   * Tests the behaviour when there is no curve instrument provider for the tenor of the bond.
   */
  @Test(expectedExceptions = OpenGammaRuntimeException.class)
  public void testNoIdForTenor() {
    final BondNode node = new BondNode(Tenor.ONE_YEAR, CNIM_NAME);
    final CurveNodeIdMapper cnim = CurveNodeIdMapper.builder()
        .name(CNIM_NAME)
        .bondNodeIds(Collections.<Tenor, CurveInstrumentProvider>singletonMap(Tenor.TWO_YEARS, CURVE_INSTRUMENT_PROVIDER))
        .build();
    final ConfigSource configSource = new MyConfigSource(Collections.<String, Object>singletonMap(CNIM_NAME, cnim));
    node.accept(new CurveNodeCurrencyVisitor(EMPTY_CONVENTION_SOURCE, EMPTY_SECURITY_SOURCE, configSource));
  }

  /**
   * Tests the behaviour when there is no security available from the source.
   */
  @Test(expectedExceptions = OpenGammaRuntimeException.class)
  public void testNoSecurity() {
    final BondNode node = new BondNode(Tenor.ONE_YEAR, CNIM_NAME);
    final CurveNodeIdMapper cnim = CurveNodeIdMapper.builder()
        .name(CNIM_NAME)
        .bondNodeIds(Collections.<Tenor, CurveInstrumentProvider>singletonMap(Tenor.ONE_YEAR, CURVE_INSTRUMENT_PROVIDER))
        .build();
    final ConfigSource configSource = new MyConfigSource(Collections.<String, Object>singletonMap(CNIM_NAME, cnim));
    node.accept(new CurveNodeCurrencyVisitor(EMPTY_CONVENTION_SOURCE, EMPTY_SECURITY_SOURCE, configSource));
  }

  /**
   * Tests the behaviour when the underlying security is not a bond.
   */
  @Test(expectedExceptions = OpenGammaRuntimeException.class)
  public void testWrongUnderlyingSecurityType() {
    final BondNode node = new BondNode(Tenor.ONE_YEAR, CNIM_NAME);
    final CurveNodeIdMapper cnim = CurveNodeIdMapper.builder()
        .name(CNIM_NAME)
        .bondNodeIds(Collections.<Tenor, CurveInstrumentProvider>singletonMap(Tenor.ONE_YEAR, CURVE_INSTRUMENT_PROVIDER))
        .build();
    final ConfigSource configSource = new MyConfigSource(Collections.<String, Object>singletonMap(CNIM_NAME, cnim));
    final Currency currency = Currency.USD;
    final ManageableSecurity security =
        new CashSecurity(currency, US, DateUtils.getUTCDate(2015, 1, 1), DateUtils.getUTCDate(2025, 1, 1), DayCounts.ACT_360, 0.01, 1000);
    security.setExternalIdBundle(ExternalIdBundle.of(SCHEME, "ISIN"));
    final SecuritySource securitySource =
        new MySecuritySource(Collections.<ExternalIdBundle, Security>singletonMap(ExternalIdBundle.of(SCHEME, "ISIN"), security));
    assertEquals(node.accept(new CurveNodeCurrencyVisitor(EMPTY_CONVENTION_SOURCE, securitySource, configSource)), Collections.singleton(currency));
  }


  /**
   * Tests that the correct currency is returned if the security and curve node id mapper are present in the sources.
   */
  @Test
  public void test() {
    final BondNode node = new BondNode(Tenor.ONE_YEAR, CNIM_NAME);
    final CurveNodeIdMapper cnim = CurveNodeIdMapper.builder()
        .name(CNIM_NAME)
        .bondNodeIds(Collections.<Tenor, CurveInstrumentProvider>singletonMap(Tenor.ONE_YEAR, CURVE_INSTRUMENT_PROVIDER))
        .build();
    final ConfigSource configSource = new MyConfigSource(Collections.<String, Object>singletonMap(CNIM_NAME, cnim));
    final Currency currency = Currency.USD;
    final ManageableSecurity security = new GovernmentBondSecurity("US", "Government", "US", "Domestic", currency, SimpleYieldConvention.US_STREET,
        new Expiry(DateUtils.getUTCDate(2025, 1, 1)), "FIXED", 0.05, PeriodFrequency.SEMI_ANNUAL, DayCounts.ACT_360, DateUtils.getUTCDate(2015, 1, 1),
        DateUtils.getUTCDate(2015, 1, 1), DateUtils.getUTCDate(2015, 7, 1), 100., 1000000, 1000, 1000, 100, 100);
    security.setExternalIdBundle(ExternalIdBundle.of(SCHEME, "ISIN"));
    final SecuritySource securitySource =
        new MySecuritySource(Collections.<ExternalIdBundle, Security>singletonMap(ExternalIdBundle.of(SCHEME, "ISIN"), security));
    assertEquals(node.accept(new CurveNodeCurrencyVisitor(EMPTY_CONVENTION_SOURCE, securitySource, configSource)), Collections.singleton(currency));
  }
}

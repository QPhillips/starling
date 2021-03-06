/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.core.exchange.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertSame;

import java.net.URI;
import java.util.Collection;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.threeten.bp.Instant;

import com.google.common.collect.ImmutableList;
import com.opengamma.core.exchange.Exchange;
import com.opengamma.core.exchange.ExchangeSource;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.id.ObjectId;
import com.opengamma.id.UniqueId;
import com.opengamma.id.VersionCorrection;
import com.opengamma.util.fudgemsg.FudgeListWrapper;
import com.opengamma.util.test.TestGroup;
import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * Tests DataExchangeSourceResource.
 */
@Test(groups = TestGroup.UNIT)
public class DataExchangeSourceResourceTest {

  private static final ObjectId OID = ObjectId.of("Test", "A");
  private static final UniqueId UID = OID.atVersion("B");
  private static final VersionCorrection VC = VersionCorrection.LATEST.withLatestFixed(Instant.now());
  private static final ExternalIdBundle BUNDLE = ExternalIdBundle.of("A", "B");
  private ExchangeSource _underlying;
  private UriInfo _uriInfo;
  private DataExchangeSourceResource _resource;

  /**
   * Sets up the underlying source.
   */
  @BeforeMethod
  public void setUp() {
    _underlying = mock(ExchangeSource.class);
    _uriInfo = mock(UriInfo.class);
    when(_uriInfo.getBaseUri()).thenReturn(URI.create("testhost"));
    _resource = new DataExchangeSourceResource(_underlying);
  }

  //-------------------------------------------------------------------------
  /**
   * Tests getting an exchange by unique id.
   */
  @Test
  public void testGetExchangeByUid() {
    final SimpleExchange target = new SimpleExchange();
    target.setExternalIdBundle(BUNDLE);
    target.setName("Test");

    when(_underlying.get(eq(UID))).thenReturn(target);

    final Response test = _resource.get(OID.toString(), UID.getVersion(), "", "");
    assertEquals(Status.OK.getStatusCode(), test.getStatus());
    assertSame(target, test.getEntity());
  }

  /**
   * Tests getting an exchange by object id and version.
   */
  @Test
  public void testGetExchangeByOidVersion() {
    final SimpleExchange target = new SimpleExchange();
    target.setExternalIdBundle(BUNDLE);
    target.setName("Test");

    when(_underlying.get(eq(OID), eq(VC))).thenReturn(target);

    final Response test = _resource.get(OID.toString(), null, VC.getVersionAsOfString(), VC.getCorrectedToString());
    assertEquals(Status.OK.getStatusCode(), test.getStatus());
    assertSame(target, test.getEntity());
  }

  /**
   * Tests getting an exchange by object id.
   */
  @Test
  public void testGetExchangeByOid() {
    final SimpleExchange target = new SimpleExchange();
    target.setExternalIdBundle(BUNDLE);
    target.setName("Test");

    when(_underlying.get(eq(OID), any(VersionCorrection.class))).thenReturn(target);

    final Response test = _resource.get(OID.toString(), null, null, null);
    assertEquals(Status.OK.getStatusCode(), test.getStatus());
    assertSame(target, test.getEntity());
  }

  /**
   * Tests searching for an exchange by external identifiers and version.
   */
  @SuppressWarnings({"rawtypes", "unchecked" })
  @Test
  public void testSearch() {
    final SimpleExchange target = new SimpleExchange();
    target.setExternalIdBundle(BUNDLE);
    target.setName("Test");
    final Collection targetColl = ImmutableList.<Exchange>of(target);

    when(_underlying.get(eq(BUNDLE), eq(VC))).thenReturn(targetColl);

    final Response test = _resource.search(VC.getVersionAsOfString(), VC.getCorrectedToString(), BUNDLE.toStringList());
    assertEquals(Status.OK.getStatusCode(), test.getStatus());
    assertEquals(FudgeListWrapper.of(targetColl), test.getEntity());
  }

  /**
   * Tests searching for a single exchange by external identifiers and version.
   */
  @Test
  public void testSearchSingle() {
    final SimpleExchange target = new SimpleExchange();
    target.setExternalIdBundle(BUNDLE);
    target.setName("Test");
    when(_underlying.getSingle(eq(BUNDLE))).thenReturn(target);

    final Response test = _resource.searchSingle(BUNDLE.toStringList());
    assertEquals(Status.OK.getStatusCode(), test.getStatus());
    assertEquals(target, test.getEntity());
  }
}

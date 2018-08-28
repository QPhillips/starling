/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.livedata.server.combining;

import static org.testng.AssertJUnit.assertEquals;

import java.util.Collections;

import org.fudgemsg.FudgeContext;
import org.fudgemsg.MutableFudgeMsg;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.id.ExternalId;
import com.opengamma.id.ExternalScheme;
import com.opengamma.livedata.LiveDataSpecification;
import com.opengamma.livedata.LiveDataValueUpdateBean;
import com.opengamma.livedata.UserPrincipal;
import com.opengamma.livedata.entitlement.AbstractEntitlementChecker;
import com.opengamma.livedata.entitlement.LiveDataEntitlementChecker;
import com.opengamma.livedata.msg.LiveDataSubscriptionRequest;
import com.opengamma.livedata.msg.LiveDataSubscriptionResponse;
import com.opengamma.livedata.msg.LiveDataSubscriptionResponseMsg;
import com.opengamma.livedata.msg.LiveDataSubscriptionResult;
import com.opengamma.livedata.msg.SubscriptionType;
import com.opengamma.livedata.server.DistributionSpecification;
import com.opengamma.livedata.server.MockDistributionSpecificationResolver;
import com.opengamma.livedata.server.MockLiveDataServer;
import com.opengamma.livedata.server.StandardLiveDataServer;
import com.opengamma.util.ehcache.EHCacheUtils;
import com.opengamma.util.test.TestGroup;

import net.sf.ehcache.CacheManager;

/**
 * Test.
 */
@Test(groups = {TestGroup.UNIT, "ehcache"})
public class PriorityResolvingCombiningLiveDataServerTest {

  private static final UserPrincipal UNAUTHORIZED_USER = new UserPrincipal("unauthorized", "127.0.0.1");
  private static final UserPrincipal AUTHORIZED_USER = new UserPrincipal("authorized", "127.0.0.1");

  private ExternalScheme _domainB;
  private ExternalScheme _domainC;
  private ExternalScheme _domainD;
  private MockLiveDataServer _serverB;
  private MockLiveDataServer _serverC;
  private PriorityResolvingCombiningLiveDataServer _combiningServer;
  private CacheManager _cacheManager;

  @BeforeClass
  public void setUpClass() {
    _cacheManager = EHCacheUtils.createTestCacheManager(getClass());
  }

  @AfterClass
  public void tearDownClass() {
    EHCacheUtils.shutdownQuiet(_cacheManager);
  }

  @BeforeMethod
  public void setUp() {
    _domainB = ExternalScheme.of("B");
    _serverB = new MockLiveDataServer(_domainB, _cacheManager);
    _serverB.setDistributionSpecificationResolver(new MockDistributionSpecificationResolver(_domainB));
    setEntitlementChecker(_serverB);
    _serverB.connect();

    _domainC = ExternalScheme.of("C");
    _serverC = new MockLiveDataServer(_domainC, _cacheManager);
    _serverC.setDistributionSpecificationResolver(new MockDistributionSpecificationResolver(_domainC));
    setEntitlementChecker(_serverC);
    _serverC.connect();

    _combiningServer = new PriorityResolvingCombiningLiveDataServer(Lists.newArrayList(_serverB, _serverC), _cacheManager);
    _combiningServer.start();

    assertEquals(StandardLiveDataServer.ConnectionStatus.CONNECTED, _combiningServer.getConnectionStatus());
    _domainD = ExternalScheme.of("D");
  }

  @AfterMethod
  public void tearDown() {
    assertEquals(StandardLiveDataServer.ConnectionStatus.CONNECTED, _combiningServer.getConnectionStatus());
    _combiningServer.stop();
    assertEquals(StandardLiveDataServer.ConnectionStatus.NOT_CONNECTED, _combiningServer.getConnectionStatus());
  }

  //-------------------------------------------------------------------------
  private void setEntitlementChecker(final MockLiveDataServer server) {
    server.setEntitlementChecker(getEntitlementChecker(server.getUniqueIdDomain()));
  }

  private LiveDataEntitlementChecker getEntitlementChecker(final ExternalScheme domain) {
    return new AbstractEntitlementChecker() {
      @Override
      public boolean isEntitled(final UserPrincipal user, final LiveDataSpecification requestedSpecification) {
        if (user == UNAUTHORIZED_USER) {
          return false;
        } else if (user == AUTHORIZED_USER) {
          return true;
        } else {
          throw new OpenGammaRuntimeException("Unexpected request for user " + user);
        }
      }
    };
  }

  //-------------------------------------------------------------------------
  public void defaultSubscription() {
    final LiveDataSpecification spec = new LiveDataSpecification("No Normalization", ExternalId.of(_domainD, "X"));
    final LiveDataSubscriptionResponse subscribe = _combiningServer.subscribe(spec, false);
    assertEquals(LiveDataSubscriptionResult.NOT_PRESENT, subscribe.getSubscriptionResult());
  }

  @Test(expectedExceptions =  Throwable.class)
  public void failingSubscriptionsDontStopWorking() {
    final LiveDataSpecification specWorking = new LiveDataSpecification("No Normalization", ExternalId.of(_domainC, "X"));
    final LiveDataSpecification specFailed = new LiveDataSpecification("No Normalization", ExternalId.of(_domainD, "X"));
    final LiveDataSubscriptionResponseMsg subscriptionRequestMade = _combiningServer.subscriptionRequestMade(new LiveDataSubscriptionRequest(UserPrincipal.getLocalUser(), SubscriptionType.NON_PERSISTENT,  Lists.newArrayList(specWorking, specFailed)));

    assertEquals(2, subscriptionRequestMade.getResponses().size());
    for (final LiveDataSubscriptionResponse response : subscriptionRequestMade.getResponses()) {
      if (response.getRequestedSpecification().equals(specWorking)) {
        assertEquals(LiveDataSubscriptionResult.SUCCESS, response.getSubscriptionResult());
      }
      else if (response.getRequestedSpecification().equals(specFailed)) {
        assertEquals(LiveDataSubscriptionResult.INTERNAL_ERROR, response.getSubscriptionResult());
      }
    }

    assertEquals(0, _serverB.getSubscriptions().size());
    assertEquals(1, _serverC.getSubscriptions().size());
  }

  public void matchingSubscription() {
    final LiveDataSpecification spec = new LiveDataSpecification("No Normalization", ExternalId.of(_domainC, "X"));
    final LiveDataSubscriptionResponse result = _combiningServer.subscribe(spec, false);
    assertEquals(LiveDataSubscriptionResult.SUCCESS, result.getSubscriptionResult());

    assertEquals(0, _serverB.getSubscriptions().size());
    assertEquals(1, _serverC.getSubscriptions().size());
  }

  public void prioritySubscription() {
    final LiveDataSpecification spec = new LiveDataSpecification("No Normalization", ExternalId.of(_domainB, "X"), ExternalId.of(_domainC, "X"));
    final LiveDataSubscriptionResponse result = _combiningServer.subscribe(spec, false);
    assertEquals(LiveDataSubscriptionResult.SUCCESS, result.getSubscriptionResult());

    assertEquals(1, _serverB.getSubscriptions().size());
    assertEquals(0, _serverC.getSubscriptions().size());
  }

  public void matchingResolution() {
    final LiveDataSpecification spec = new LiveDataSpecification("No Normalization", ExternalId.of(_domainC, "X"));
    final DistributionSpecification combined = _combiningServer.getDefaultDistributionSpecificationResolver().resolve(spec);
    final DistributionSpecification direct = _serverC.getDistributionSpecificationResolver().resolve(spec);
    assertEquals(direct, combined);
  }

  public void snapshot() {
    final MutableFudgeMsg msg = FudgeContext.GLOBAL_DEFAULT.newMessage();
    msg.add("FIELD", "VALUE");
    _serverC.addMarketDataMapping("X", msg);
    final LiveDataSpecification spec = new LiveDataSpecification("No Normalization", ExternalId.of(_domainC, "X"));
    final LiveDataSubscriptionRequest request = new LiveDataSubscriptionRequest(AUTHORIZED_USER, SubscriptionType.SNAPSHOT, Collections.singleton(spec));
    final LiveDataSubscriptionResponseMsg responseMsg = _combiningServer.subscriptionRequestMade(request);
    assertEquals(responseMsg.getRequestingUser(), AUTHORIZED_USER);
    assertEquals(1, responseMsg.getResponses().size());
    for (final LiveDataSubscriptionResponse response : responseMsg.getResponses()) {
      assertEquals(LiveDataSubscriptionResult.SUCCESS, response.getSubscriptionResult());
      final LiveDataValueUpdateBean snap = response.getSnapshot();
      assertEquals("VALUE", snap.getFields().getString("FIELD"));
      assertEquals(1, snap.getFields().getNumFields());
    }

    assertEquals(0, _serverB.getSubscriptions().size());
    assertEquals(0, _serverC.getSubscriptions().size());
  }

  public void entitled() {
    final LiveDataSpecification spec = new LiveDataSpecification("No Normalization", ExternalId.of(_domainC, "X"));
    final LiveDataSubscriptionRequest request = new LiveDataSubscriptionRequest(AUTHORIZED_USER, SubscriptionType.NON_PERSISTENT, Collections.singleton(spec));
    final LiveDataSubscriptionResponseMsg responseMsg = _combiningServer.subscriptionRequestMade(request);
    assertEquals(responseMsg.getRequestingUser(), AUTHORIZED_USER);
    assertEquals(1, responseMsg.getResponses().size());
    for (final LiveDataSubscriptionResponse response : responseMsg.getResponses()) {
      assertEquals(LiveDataSubscriptionResult.SUCCESS, response.getSubscriptionResult());
    }

    assertEquals(0, _serverB.getSubscriptions().size());
    assertEquals(1, _serverC.getSubscriptions().size());
  }

  public void notEntitled() {
    final LiveDataSpecification spec = new LiveDataSpecification("No Normalization", ExternalId.of(_domainC, "X"));
    final LiveDataSubscriptionRequest request = new LiveDataSubscriptionRequest(UNAUTHORIZED_USER, SubscriptionType.NON_PERSISTENT,
        Collections.singleton(spec));
    final LiveDataSubscriptionResponseMsg responseMsg = _combiningServer.subscriptionRequestMade(request);
    assertEquals(responseMsg.getRequestingUser(), UNAUTHORIZED_USER);
    assertEquals(1, responseMsg.getResponses().size());
    for (final LiveDataSubscriptionResponse response : responseMsg.getResponses()) {
      assertEquals(LiveDataSubscriptionResult.NOT_AUTHORIZED, response.getSubscriptionResult());
    }

    assertEquals(0, _serverB.getSubscriptions().size());
    assertEquals(0, _serverC.getSubscriptions().size());
  }

}

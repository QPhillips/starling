/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.livedata.server;

import org.fudgemsg.FudgeMsg;
import org.fudgemsg.FudgeMsgEnvelope;
import org.fudgemsg.mapping.FudgeDeserializer;
import org.fudgemsg.mapping.FudgeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.opengamma.livedata.msg.LiveDataSubscriptionRequest;
import com.opengamma.livedata.msg.LiveDataSubscriptionResponseMsg;
import com.opengamma.transport.FudgeRequestReceiver;
import com.opengamma.util.ArgumentChecker;

/**
 * Receives market data subscription requests from clients.
 */
public class SubscriptionRequestReceiver implements FudgeRequestReceiver {

  /** Logger. */
  private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionRequestReceiver.class);

  /**
   * The underlying server.
   */
  private final StandardLiveDataServer _liveDataServer;

  /**
   * Creates an instance wrapping an underlying server.
   *
   * @param liveDataServer  the server, not null
   */
  public SubscriptionRequestReceiver(final StandardLiveDataServer liveDataServer) {
    ArgumentChecker.notNull(liveDataServer, "liveDataServer");
    _liveDataServer = liveDataServer;
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the underlying server.
   *
   * @return the server, not null
   */
  public StandardLiveDataServer getLiveDataServer() {
    return _liveDataServer;
  }

  //-------------------------------------------------------------------------
  @Override
  @Transactional
  public FudgeMsg requestReceived(final FudgeDeserializer deserializer, final FudgeMsgEnvelope requestEnvelope) {
    try {
      final FudgeMsg requestFudgeMsg = requestEnvelope.getMessage();
      final LiveDataSubscriptionRequest subscriptionRequest = LiveDataSubscriptionRequest.fromFudgeMsg(deserializer, requestFudgeMsg);
      LOGGER.debug("Received subscription request {}", subscriptionRequest);
      final LiveDataSubscriptionResponseMsg subscriptionResponse = getLiveDataServer().subscriptionRequestMade(subscriptionRequest);
      LOGGER.debug("Sending subscription response {}", subscriptionResponse);
      final FudgeMsg responseFudgeMsg = subscriptionResponse.toFudgeMsg(new FudgeSerializer(deserializer.getFudgeContext()));
      return responseFudgeMsg;
    } catch (final RuntimeException e) {
      LOGGER.error("Unexpected exception when processing subscription request", e);
      throw e;
    }
  }

}

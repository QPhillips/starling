/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.livedata.client;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.fudgemsg.FudgeContext;
import org.fudgemsg.FudgeMsg;
import org.fudgemsg.FudgeMsgEnvelope;
import org.fudgemsg.mapping.FudgeDeserializer;
import org.fudgemsg.mapping.FudgeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.livedata.LiveDataSpecification;
import com.opengamma.livedata.msg.ResolveRequest;
import com.opengamma.livedata.msg.ResolveResponse;
import com.opengamma.transport.FudgeMessageReceiver;
import com.opengamma.transport.FudgeRequestSender;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.fudgemsg.OpenGammaFudgeContext;

/**
 * Maps client LiveData IDs to server LiveData IDs by contacting a remote LiveData server.
 *
 */
public class DistributedSpecificationResolver {
  private static final long TIMEOUT_MS = 5 * 60 * 100L;
  private static final Logger LOGGER = LoggerFactory.getLogger(DistributedSpecificationResolver.class);
  private final FudgeRequestSender _requestSender;
  private final FudgeContext _fudgeContext;

  public DistributedSpecificationResolver(final FudgeRequestSender requestSender) {
    this(requestSender, OpenGammaFudgeContext.getInstance());
  }

  public DistributedSpecificationResolver(final FudgeRequestSender requestSender, final FudgeContext fudgeContext) {
    ArgumentChecker.notNull(requestSender, "Request Sender");
    ArgumentChecker.notNull(fudgeContext, "Fudge Context");
    _requestSender = requestSender;
    _fudgeContext = fudgeContext;
  }

  public LiveDataSpecification resolve(
      final LiveDataSpecification spec) {

    LOGGER.info("Sending message to resolve ", spec);
    final ResolveRequest resolveRequest = new ResolveRequest(spec);
    final FudgeMsg requestMessage = resolveRequest.toFudgeMsg(new FudgeSerializer(_fudgeContext));
    final AtomicBoolean responseReceived = new AtomicBoolean(false);
    final AtomicReference<LiveDataSpecification> resolved = new AtomicReference<>();
    _requestSender.sendRequest(requestMessage, new FudgeMessageReceiver() {

      @Override
      public void messageReceived(final FudgeContext fudgeContext,
          final FudgeMsgEnvelope msgEnvelope) {

        final FudgeMsg msg = msgEnvelope.getMessage();
        final ResolveResponse response = ResolveResponse.fromFudgeMsg(new FudgeDeserializer(_fudgeContext), msg);
        resolved.set(response.getResolvedSpecification());
        responseReceived.set(true);

      }
    });
    final long start = System.currentTimeMillis();
    while (!responseReceived.get()) {
      try {
        Thread.sleep(100L);
      } catch (final InterruptedException e) {
        Thread.interrupted();
      }
      if (System.currentTimeMillis() - start >= TIMEOUT_MS) {
        throw new OpenGammaRuntimeException("Timeout. Waited for specification resolution response for " + TIMEOUT_MS + " with no response.");
      }
    }

    return resolved.get();
  }

}

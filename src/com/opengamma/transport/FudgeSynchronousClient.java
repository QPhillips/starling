/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.transport;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.fudgemsg.FudgeContext;
import org.fudgemsg.FudgeFieldContainer;
import org.fudgemsg.FudgeMsgEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.util.ArgumentChecker;

/**
 * Allows synchronous RPC-style semantics to be applied over a {@link FudgeRequestSender}.
 * This class also supports multiplexing different clients over the same
 * underlying transport channel using correlation IDs to multiplex the requests
 * and responses. 
 */
public abstract class FudgeSynchronousClient implements FudgeMessageReceiver {

  /** Logger. */
  private static final Logger s_logger = LoggerFactory.getLogger(FudgeSynchronousClient.class);
  /**
   * The default timeout.
   */
  private static final long DEFAULT_TIMEOUT_IN_MILLISECONDS = 30 * 1000L;
  /**
   * The generator of correlation ids.
   */
  private final AtomicLong _nextCorrelationId = new AtomicLong();
  /**
   * The Fudge request sender.
   */
  private final FudgeRequestSender _requestSender;
  /**
   * The map of pending requests keyed by correlation id.
   */
  private final Map<Long, ClientRequestHolder> _pendingRequests = new ConcurrentHashMap<Long, ClientRequestHolder>();
  /**
   * The timeout.
   */
  private long _timeoutInMilliseconds = DEFAULT_TIMEOUT_IN_MILLISECONDS;

  /**
   * Creates the client.
   * @param requestSender  the sender, not null
   */
  protected FudgeSynchronousClient(final FudgeRequestSender requestSender) {
    ArgumentChecker.notNull(requestSender, "requestSender");
    _requestSender = requestSender;
  }

  // -------------------------------------------------------------------------
  /**
   * Gets the request sender.
   * @return the request sender, not null
   */
  public FudgeRequestSender getRequestSender() {
    return _requestSender;
  }

  /**
   * Gets the timeout in milliseconds.
   * @return the timeout
   */
  public long getTimeoutInMilliseconds() {
    return _timeoutInMilliseconds;
  }

  public void setTimeoutInMilliseconds(final long timeoutMilliseconds) {
    _timeoutInMilliseconds = timeoutMilliseconds;
  }

  /**
   * Gets the next id.
   * @return the next numeric id
   */
  protected long getNextCorrelationId() {
    return _nextCorrelationId.incrementAndGet();
  }

  // -------------------------------------------------------------------------
  /**
   * Sends the message.
   * @param requestMsg  the message, not null
   * @param correlationId  the message id
   * @return the result
   */
  protected FudgeFieldContainer sendRequestAndWaitForResponse(FudgeFieldContainer requestMsg, long correlationId) {
    ClientRequestHolder requestHolder = new ClientRequestHolder();
    _pendingRequests.put(correlationId, requestHolder);
    try {
      getRequestSender().sendRequest(requestMsg, this);
      try {
        requestHolder.latch.await(getTimeoutInMilliseconds(), TimeUnit.MILLISECONDS);
      } catch (InterruptedException e) {
        Thread.interrupted();
        s_logger.warn("Didn't get response to {} in {}ms", correlationId, getTimeoutInMilliseconds());
      }
      if (requestHolder.resultValue == null) {
        throw new OpenGammaRuntimeException("Didn't receive a response message to " + correlationId + " in " + getTimeoutInMilliseconds() + "ms");
      }
      assert getCorrelationIdFromReply(requestHolder.resultValue) == correlationId;
      return requestHolder.resultValue;
    } finally {
      _pendingRequests.remove(correlationId);
    }
  }

  /**
   * Receives a message from Fudge.
   * @param fudgeContext  the Fudge context, not null
   * @param msgEnvelope  the message, not null
   */
  @Override
  public void messageReceived(FudgeContext fudgeContext, FudgeMsgEnvelope msgEnvelope) {
    final FudgeFieldContainer reply = msgEnvelope.getMessage();
    final long correlationId = getCorrelationIdFromReply(reply);
    final ClientRequestHolder requestHolder = _pendingRequests.remove(correlationId);
    if (requestHolder == null) {
      s_logger.warn("Got a response on non-pending correlation Id {}", correlationId);
      return;
    }
    requestHolder.resultValue = reply;
    requestHolder.latch.countDown();
  }

  /**
   * Extracts the correlation id from the reply object.
   * @param reply  the reply
   * @return the id
   */
  protected abstract long getCorrelationIdFromReply(FudgeFieldContainer reply);

  // -------------------------------------------------------------------------
  /**
   * Data holder.
   */
  private static final class ClientRequestHolder {
    public FudgeFieldContainer resultValue; // CSIGNORE: simple holder object
    public final CountDownLatch latch = new CountDownLatch(1); // CSIGNORE: simple holder object
  }

}

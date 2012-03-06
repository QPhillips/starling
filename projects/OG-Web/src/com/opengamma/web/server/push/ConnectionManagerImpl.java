/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.web.server.push;

import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.base.Objects;
import com.opengamma.DataNotFoundException;
import com.opengamma.core.change.ChangeManager;
import com.opengamma.id.UniqueId;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.web.server.push.rest.MasterType;

/**
 * {@link ConnectionManager} implementation that creates an instance of {@link ClientConnection} for each
 * client.  It creates {@link Timer} tasks for each connection that closes them and cleans up if they are idle
 * for too long.  This class is thread safe.
 */
public class ConnectionManagerImpl implements ConnectionManager {

  /** Period for the periodic tasks that check whether the client connections have been idle for too long */
  private static final long DEFAULT_TIMEOUT_CHECK_PERIOD = 60000;

  /** By default a client is disconnected if it hasn't been heard from for five minutes */
  private static final long DEFAULT_TIMEOUT = 300000;

  // TODO a better way to generate client IDs
  /** Client ID of the next connection */
  private final AtomicLong _clientConnectionId = new AtomicLong();

  /** Creates and stores viewports */
  private final ViewportManager _viewportFactory;

  /** Provides a connection to the long-polling HTTP connections */
  private final LongPollingConnectionManager _longPollingConnectionManager;

  /** Maximum time a client is allow to be idle before it's disconnected */
  private final long _timeout;

  /** Period for the tasks that check for idle clients */
  private final long _timeoutCheckPeriod;

  /** Connections keyed on client ID */
  private final Map<String, ClientConnection> _connectionsByClientId = new ConcurrentHashMap<String, ClientConnection>();

  /** Connections keyed on viewport ID */
  private final Map<String, ClientConnection> _connectionsByViewportId = new ConcurrentHashMap<String, ClientConnection>();

  /** Timer for tasks that check for idle clients */
  private final Timer _timer = new Timer();

  /** For listening for changes in entity data */
  private final ChangeManager _changeManager;

  /** For listening for changes to any data in a master */
  private final MasterChangeManager _masterChangeManager;

  public ConnectionManagerImpl(ChangeManager changeManager,
                               MasterChangeManager masterChangeManager,
                               ViewportManager viewportFactory,
                               LongPollingConnectionManager longPollingConnectionManager) {
    this(changeManager, masterChangeManager, viewportFactory, longPollingConnectionManager, DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_CHECK_PERIOD);
  }

  public ConnectionManagerImpl(ChangeManager changeManager,
                               MasterChangeManager masterChangeManager,
                               ViewportManager viewportFactory,
                               LongPollingConnectionManager longPollingConnectionManager,
                               long timeout,
                               long timeoutCheckPeriod) {
    _changeManager = changeManager;
    _viewportFactory = viewportFactory;
    _longPollingConnectionManager = longPollingConnectionManager;
    _timeout = timeout;
    _timeoutCheckPeriod = timeoutCheckPeriod;
    _masterChangeManager = masterChangeManager;
  }

  /**
   * Creates a new connection for a client and returns its client ID.  The client ID should be used by the client
   * when subscribing for asynchronous updates.  A connection typically corresponds to a single browser tab or
   * window.  A user can have multiple simultaneous connections.
   * @param userId The ID of the user creating the connection
   * @return The client ID of the new connection, must be supplied by the client when subscribing for updates
   */
  @Override
  public String clientConnected(String userId) {
    // TODO check args
    String clientId = Long.toString(_clientConnectionId.getAndIncrement());
    ConnectionTimeoutTask timeoutTask = new ConnectionTimeoutTask(this, userId, clientId, _timeout);
    LongPollingUpdateListener updateListener = _longPollingConnectionManager.handshake(userId, clientId, timeoutTask);
    ClientConnection connection = new ClientConnection(userId, clientId, updateListener, _viewportFactory, timeoutTask);
    _changeManager.addChangeListener(connection);
    _masterChangeManager.addChangeListener(connection);
    _connectionsByClientId.put(clientId, connection);
    _timer.scheduleAtFixedRate(timeoutTask, _timeoutCheckPeriod, _timeoutCheckPeriod);
    return clientId;
  }

  @Override
  public void clientDisconnected(String userId, String clientId) {
    ClientConnection connection = getConnectionByClientId(userId, clientId);
    _connectionsByClientId.remove(clientId);
    _changeManager.removeChangeListener(connection);
    _masterChangeManager.removeChangeListener(connection);
    _longPollingConnectionManager.disconnect(clientId);
    connection.disconnect();
  }

  @Override
  public void subscribe(String userId, String clientId, UniqueId uid, String url) {
    getConnectionByClientId(userId, clientId).subscribe(uid, url);
  }

  @Override
  public void subscribe(String userId, String clientId, MasterType masterType, String url) {
    getConnectionByClientId(userId, clientId).subscribe(masterType, url);
  }

  @Override
  public Viewport getViewport(String userId, String clientId, String viewportId) {
    // TODO check args
    if (clientId == null) {
      // TODO check the viewport is owned by the user
      return _viewportFactory.getViewport(viewportId);
    } else {
      return getConnectionByViewportId(userId, viewportId).getViewport(viewportId);
    }
  }

  @Override
  public void createViewport(String userId,
                             String clientId,
                             ViewportDefinition viewportDefinition,
                             String viewportId,
                             String dataUrl,
                             String gridStructureUrl) {
    if (clientId == null) {
      _viewportFactory.createViewport(viewportId, viewportDefinition);
    } else {
      ClientConnection connection = getConnectionByClientId(userId, clientId);
      connection.createViewport(viewportDefinition, viewportId, dataUrl, gridStructureUrl);
      _connectionsByViewportId.put(viewportId, connection);
    }
  }

  /**
   * Returns the {@link ClientConnection} corresponding to a client ID.
   * @param userId The ID of the user who owns the connection
   * @param clientId The client ID
   * @return The connection
   * @throws DataNotFoundException If there is no connection for the specified ID, the user ID is invalid or if
   * the client and user IDs don't correspond
   */
  private ClientConnection getConnectionByClientId(String userId, String clientId) {
    // TODO user logins
    //ArgumentChecker.notEmpty(userId, "userId");
    ArgumentChecker.notEmpty(clientId, "clientId");
    ClientConnection connection = _connectionsByClientId.get(clientId);
    if (connection == null) {
      throw new DataNotFoundException("Unknown client ID: " + clientId);
    }
    if (!Objects.equal(userId, connection.getUserId())) {
      throw new DataNotFoundException("User ID " + userId + " is not associated with client ID " + clientId);
    }
    return connection;
  }

  /**
   * Returns the {@link ClientConnection} that owns a viewport.
   * @param userId The ID of the user who owns the connection
   * @param viewportId The ID of the viewport
   * @return The connection
   * @throws DataNotFoundException If there is no viewport with the specified ID, the connection doesn't own viewport,
   * the user ID is invalid or if the client connection isn't owned by the specified user.
   */
  private ClientConnection getConnectionByViewportId(String userId, String viewportId) {
    ClientConnection connection = _connectionsByViewportId.get(viewportId);
    if (connection == null) {
      throw new DataNotFoundException("Unknown viewport ID: " + viewportId);
    }
    if (!Objects.equal(userId, connection.getUserId())) {
      throw new DataNotFoundException("User ID " + userId + " is not associated with viewport " + viewportId);
    }
    return connection;
  }

}

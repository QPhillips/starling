/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.livedata.firehose;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.util.ArgumentChecker;

/**
 * An implementation of {@link InputStreamFactory} that connects to a remote socket.
 */
public class SocketInputStreamFactory implements InputStreamFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(SocketInputStreamFactory.class);
  /**
   * Time in ms to wait on initial connect.
   * Set to 5sec to allow the outer loop to work correctly.
   */
  private static final int CONNECT_TIMEOUT = 5000;

  private final InetAddress _host;
  private final int _port;
  private final String _description;

  public SocketInputStreamFactory(final String hostName, final int port) throws UnknownHostException {
    this(InetAddress.getByName(hostName), port);
  }

  public SocketInputStreamFactory(final InetAddress host, final int port) {
    ArgumentChecker.notNull(host, "host");
    ArgumentChecker.notNegative(port, "port");
    _host = host;
    _port = port;

    _description = "Socket[" + _host + ":" + port + "]";
  }

  /**
   * Gets the host.
   * @return the host
   */
  public InetAddress getHost() {
    return _host;
  }

  /**
   * Gets the port.
   * @return the port
   */
  public int getPort() {
    return _port;
  }

  @Override
  public InputStream openConnection() {
    final Socket socket = new Socket();
    try {
      socket.connect(new InetSocketAddress(_host, _port), CONNECT_TIMEOUT);
      final InputStream is = socket.getInputStream();
      LOGGER.info("Connected to {}:{}", _host, _port);
      return is;
    } catch (final IOException ioe) {
      try {
        socket.close();
      } catch (final IOException ioe2) {
        LOGGER.debug("Unable to close socket in case of error. This is almost certainly fine because the socket isn't bound", ioe2);
      }
      LOGGER.warn("Unable to open a connection to " + _host + ":" + _port, ioe);
      throw new OpenGammaRuntimeException("Unable to open a connection to " + _host + ":" + _port, ioe);
    }
  }

  @Override
  public String getDescription() {
    return _description;
  }

}

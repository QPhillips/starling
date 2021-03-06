/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.util.test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.fudgemsg.FudgeContext;
import org.fudgemsg.FudgeMsg;
import org.fudgemsg.MutableFudgeMsg;
import org.fudgemsg.mapping.FudgeDeserializer;
import org.fudgemsg.mapping.FudgeObjectReader;
import org.fudgemsg.mapping.FudgeObjectWriter;
import org.fudgemsg.mapping.FudgeSerializer;
import org.fudgemsg.wire.FudgeMsgReader;
import org.fudgemsg.wire.FudgeMsgWriter;
import org.fudgemsg.wire.xml.FudgeXMLStreamReader;
import org.fudgemsg.wire.xml.FudgeXMLStreamWriter;
import org.joda.beans.Bean;
import org.joda.beans.ser.JodaBeanSer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;

import com.google.common.base.Charsets;
import com.opengamma.util.fudgemsg.OpenGammaFudgeContext;
import com.opengamma.util.test.BuilderTestProxyFactory.BuilderTestProxy;

/**
 * Base class for builder tests.
 */
public abstract class AbstractFudgeBuilderTestCase {

  /** Logger. */
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFudgeBuilderTestCase.class);

  private FudgeContext _context;
  private FudgeSerializer _serializer;
  private FudgeDeserializer _deserializer;
  private BuilderTestProxy _proxy;

  /**
   * Creates the Fudge context and proxy.
   */
  @BeforeMethod(groups = TestGroup.UNIT)
  public void createContexts() {
    setContext(OpenGammaFudgeContext.getInstance());
    _proxy = new BuilderTestProxyFactory().getProxy();
  }

  /**
   * Sets the context and creates the serializer and deserializer.
   *
   * @param context  the context
   */
  protected void setContext(final FudgeContext context) {
    _context = context;
    _serializer = new FudgeSerializer(context);
    _deserializer = new FudgeDeserializer(context);
  }

  /**
   * Gets the Fudge context.
   *
   * @return  the context
   */
  protected FudgeContext getFudgeContext() {
    return _context;
  }

  /**
   * Gets the serializer.
   *
   * @return  the context
   */
  protected FudgeSerializer getFudgeSerializer() {
    return _serializer;
  }

  /**
   * Gets the deserializer.
   *
   * @return  the context
   */
  protected FudgeDeserializer getFudgeDeserializer() {
    return _deserializer;
  }

  /**
   * Gets the logger.
   *
   * @return  the logger
   */
  protected Logger getLogger() {
    return LOGGER;
  }

  //-------------------------------------------------------------------------
  protected <T> void assertEncodeDecodeCycle(final Class<T> clazz, final T object) {
    assertEquals(object, cycleObjectProxy(clazz, object));
    assertEquals(object, cycleObjectBytes(clazz, object));
    assertEquals(object, cycleObjectXml(clazz, object));
    assertEquals(object, cycleObjectJodaXml(clazz, object));
  }

  protected <T> T cycleObject(final Class<T> clazz, final T object) {
    return cycleObjectProxy(clazz, object);
  }

  protected <T> T cycleObjectProxy(final Class<T> clazz, final T object) {
    getLogger().debug("cycle object {} of class by proxy {}", object, clazz);

    final MutableFudgeMsg msgOut = getFudgeSerializer().newMessage();
    getFudgeSerializer().addToMessage(msgOut, "test", null, object);
    getLogger().debug("message out by proxy {}", msgOut);

    final FudgeMsg msgIn = _proxy.proxy(clazz, msgOut);
    getLogger().debug("message in by proxy {}", msgIn);

    final T cycled = getFudgeDeserializer().fieldValueToObject(clazz, msgIn.getByName("test"));
    getLogger().debug("created object by proxy {}", cycled);
    assertTrue(clazz.isAssignableFrom(cycled.getClass()));
    return cycled;
  }

  protected <T> T cycleObjectBytes(final Class<T> clazz, final T object) {
    getLogger().debug("cycle object {} of class by bytes {}", object, clazz);

    final MutableFudgeMsg msgOut = getFudgeSerializer().newMessage();
    getFudgeSerializer().addToMessage(msgOut, "test", null, object);
    getLogger().debug("message out by bytes {}", msgOut);

    final FudgeMsg msgIn = cycleMessage(msgOut);
    getLogger().debug("message in by bytes {}", msgIn);

    final T cycled = getFudgeDeserializer().fieldValueToObject(clazz, msgIn.getByName("test"));
    getLogger().debug("created object by bytes {}", cycled);
    assertTrue(clazz.isAssignableFrom(cycled.getClass()));
    return cycled;
  }

  protected FudgeMsg cycleMessage(final FudgeMsg message) {
    final byte[] data = getFudgeContext().toByteArray(message);
    getLogger().info("{} bytes", data.length);
    return getFudgeContext().deserialize(data).getMessage();
  }

  protected <T> T cycleObjectXml(final Class<T> clazz, final T object) {
    getLogger().debug("cycle object {} of class by xml {}", object, clazz);

    final MutableFudgeMsg msgOut = getFudgeSerializer().newMessage();
    getFudgeSerializer().addToMessage(msgOut, "test", null, object);
    getLogger().debug("message out by xml {}", msgOut);

    final FudgeMsg msgIn = cycleMessageXml(msgOut);
    getLogger().debug("message in by xml {}", msgIn);

    final T cycled = getFudgeDeserializer().fieldValueToObject(clazz, msgIn.getByName("test"));
    getLogger().debug("created object by xml {}", cycled);
    assertTrue(clazz.isAssignableFrom(cycled.getClass()));
    return cycled;
  }

  protected FudgeMsg cycleMessageXml(final FudgeMsg message) {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final OutputStreamWriter outputWriter = new OutputStreamWriter(baos, Charsets.UTF_8);
    try (FudgeMsgWriter fudgeWriter = new FudgeMsgWriter(new FudgeXMLStreamWriter(getFudgeContext(), outputWriter))) {
      fudgeWriter.writeMessage(message);
      fudgeWriter.flush();
    }
    final byte[] data = baos.toByteArray();
    getLogger().info("{} bytes", data.length);
    final ByteArrayInputStream bais = new ByteArrayInputStream(data);
    final InputStreamReader inputReader = new InputStreamReader(new BufferedInputStream(bais), Charsets.UTF_8);
    try (FudgeMsgReader fudgeReader = new FudgeMsgReader(new FudgeXMLStreamReader(getFudgeContext(), inputReader))) {
      return fudgeReader.nextMessage();
    }
  }

  protected <T> T cycleObjectJodaXml(final Class<T> clazz, final T object) {
    getLogger().debug("cycle object {} of class by xml {}", object, clazz);

    if (object instanceof Bean) {
      final String xml = JodaBeanSer.PRETTY.xmlWriter().write((Bean) object);
      @SuppressWarnings("unchecked")
      final
      T cycled = (T) JodaBeanSer.PRETTY.xmlReader().read(xml);
      assertTrue(clazz.isAssignableFrom(cycled.getClass()));
      return cycled;
    }
    getLogger().info("Not a Bean {}", object.getClass());
    return object;
  }

  @SuppressWarnings("unchecked")
  protected <T> T cycleObjectOverBytes(final T object) {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    try (FudgeObjectWriter writer = getFudgeContext().createObjectWriter(output)) {
      writer.write(object);
    }
    final ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
    try (FudgeObjectReader reader = getFudgeContext().createObjectReader(input)) {
      return (T) reader.read();
    }
  }

}

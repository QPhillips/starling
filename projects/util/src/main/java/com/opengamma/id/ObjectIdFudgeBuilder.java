/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.id;

import org.fudgemsg.FudgeMsg;
import org.fudgemsg.MutableFudgeMsg;
import org.fudgemsg.mapping.FudgeBuilder;
import org.fudgemsg.mapping.FudgeBuilderFor;
import org.fudgemsg.mapping.FudgeDeserializer;
import org.fudgemsg.mapping.FudgeSerializer;

import com.opengamma.util.fudgemsg.AbstractFudgeBuilder;

/**
 * Fudge builder for {@code ObjectId}.
 */
@FudgeBuilderFor(ObjectId.class)
public final class ObjectIdFudgeBuilder extends AbstractFudgeBuilder implements FudgeBuilder<ObjectId> {

  /** Field name. */
  public static final String SCHEME_FIELD_NAME = "Scheme";
  /** Field name. */
  public static final String VALUE_FIELD_NAME = "Value";

  //-------------------------------------------------------------------------
  @Override
  public MutableFudgeMsg buildMessage(final FudgeSerializer serializer, final ObjectId object) {
    final MutableFudgeMsg msg = serializer.newMessage();
    toFudgeMsg(serializer, object, msg);
    return msg;
  }

  /**
   * Converts an {@link ObjectId} to a mutable Fudge message. Returns null if the id is null.
   *
   * @param serializer  the Fudge serializer
   * @param object  the id
   * @return  the message
   */
  public static MutableFudgeMsg toFudgeMsg(final FudgeSerializer serializer, final ObjectId object) {
    if (object == null) {
      return null;
    }
    final MutableFudgeMsg msg = serializer.newMessage();
    toFudgeMsg(serializer, object, msg);
    return msg;
  }

  /**
   * Adds an {@link ObjectId} to a message.
   *
   * @param serializer  the Fudge serializer
   * @param object  the id
   * @param msg  the message, not null
   */
  public static void toFudgeMsg(final FudgeSerializer serializer, final ObjectId object, final MutableFudgeMsg msg) {
    addToMessage(msg, SCHEME_FIELD_NAME, object.getScheme());
    addToMessage(msg, VALUE_FIELD_NAME, object.getValue());
  }

  //-------------------------------------------------------------------------
  @Override
  public ObjectId buildObject(final FudgeDeserializer deserializer, final FudgeMsg msg) {
    return fromFudgeMsg(msg);
  }

  /**
   * Converts a Fudge message to an {@link ObjectId}. Returns null if the message is null.
   *
   * @param deserializer  the Fudge deserializer
   * @param msg  the message
   * @return  the id
   */
  public static ObjectId fromFudgeMsg(final FudgeDeserializer deserializer, final FudgeMsg msg) {
    if (msg == null) {
      return null;
    }
    return fromFudgeMsg(msg);
  }

  /**
   * Converts a Fudge message to an {@link ObjectId}.
   *
   * @param msg  the message, not null
   * @return  the id
   */
  public static ObjectId fromFudgeMsg(final FudgeMsg msg) {
    final String scheme = msg.getString(SCHEME_FIELD_NAME);
    final String value = msg.getString(VALUE_FIELD_NAME);
    return ObjectId.of(scheme, value);
  }

}

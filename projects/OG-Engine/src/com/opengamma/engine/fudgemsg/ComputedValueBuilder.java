/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.fudgemsg;

import org.apache.commons.lang.Validate;
import org.fudgemsg.FudgeField;
import org.fudgemsg.FudgeMsg;
import org.fudgemsg.MutableFudgeMsg;
import org.fudgemsg.mapping.FudgeBuilder;
import org.fudgemsg.mapping.FudgeBuilderFor;
import org.fudgemsg.mapping.FudgeDeserializationContext;
import org.fudgemsg.mapping.FudgeSerializationContext;

import com.opengamma.engine.value.ComputedValue;
import com.opengamma.engine.value.ValueSpecification;

/**
 * Fudge message builder for {@code ComputedValue}.
 */
@FudgeBuilderFor(ComputedValue.class)
public class ComputedValueBuilder implements FudgeBuilder<ComputedValue> {
  /**
   * Fudge field name.
   */
  private static final String SPECIFICATION_KEY = "specification";
  /**
   * Fudge field name.
   */
  private static final String VALUE_KEY = "value";

  @Override
  public MutableFudgeMsg buildMessage(FudgeSerializationContext context, ComputedValue object) {
    MutableFudgeMsg msg = context.newMessage();
    ValueSpecification specification = object.getSpecification();
    if (specification != null) {
      context.addToMessage(msg, SPECIFICATION_KEY, null, specification);
    }
    Object value = object.getValue();
    if (value != null) {
      context.addToMessageWithClassHeaders(msg, VALUE_KEY, null, value);
    }
    return msg;
  }

  @Override
  public ComputedValue buildObject(FudgeDeserializationContext context, FudgeMsg message) {
    FudgeField fudgeField = message.getByName(SPECIFICATION_KEY);
    Validate.notNull(fudgeField, "Fudge message is not a ComputedValue - field 'specification' is not present");
    ValueSpecification valueSpec = context.fieldValueToObject(ValueSpecification.class, fudgeField);
    fudgeField = message.getByName(VALUE_KEY);
    Validate.notNull(fudgeField, "Fudge message is not a ComputedValue - field 'value' is not present");
    Object valueObject = context.fieldValueToObject(fudgeField);
    return new ComputedValue(valueSpec, valueObject);
  }

}

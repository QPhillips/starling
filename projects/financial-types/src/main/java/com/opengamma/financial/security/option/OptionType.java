/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security.option;

/**
 * The option type of Put or Call.
 */
public enum OptionType {

  /**
   * The option is a put.
   */
  PUT,
  /**
   * The option is a call.
   */
  CALL;

  /**
   * Parses the type.
   * 
   * @param text
   *          the string to parse
   * @return the parsed text or null
   */
  public static OptionType parse(final String text) {
    if (text == null) {
      return null;
    }
    if ("put".equalsIgnoreCase(text)) {
      return PUT;
    }
    if ("call".equalsIgnoreCase(text)) {
      return CALL;
    }
    return null;
  }

}

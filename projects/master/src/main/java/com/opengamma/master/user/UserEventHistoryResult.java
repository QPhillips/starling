/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.master.user;

import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

/**
 * Result providing the event history of a user.
 * <p>
 * When a user master is queried for event history, this class is used to return the result.
 * It stores a list of events, in chronological order, representing changes that were made.
 * See {@link RoleEventHistoryRequest} for more details.
 */
@BeanDefinition
public class UserEventHistoryResult extends EventHistoryResult {

  /**
   * Creates an instance.
   */
  protected UserEventHistoryResult() {
  }

  /**
   * Creates an instance from a collection of events.
   *
   * @param events  the collection of events to add, not null
   */
  public UserEventHistoryResult(final Iterable<HistoryEvent> events) {
    super(events);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code UserEventHistoryResult}.
   * @return the meta-bean, not null
   */
  public static UserEventHistoryResult.Meta meta() {
    return UserEventHistoryResult.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(UserEventHistoryResult.Meta.INSTANCE);
  }

  @Override
  public UserEventHistoryResult.Meta metaBean() {
    return UserEventHistoryResult.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  @Override
  public UserEventHistoryResult clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      return super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(32);
    buf.append("UserEventHistoryResult{");
    int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  @Override
  protected void toString(StringBuilder buf) {
    super.toString(buf);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code UserEventHistoryResult}.
   */
  public static class Meta extends EventHistoryResult.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap());

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    public BeanBuilder<? extends UserEventHistoryResult> builder() {
      return new DirectBeanBuilder<UserEventHistoryResult>(new UserEventHistoryResult());
    }

    @Override
    public Class<? extends UserEventHistoryResult> beanType() {
      return UserEventHistoryResult.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

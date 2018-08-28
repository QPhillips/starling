/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.master.user;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.threeten.bp.Instant;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.opengamma.id.UniqueId;

/**
 * A single event history element.
 * <p>
 * See {@link EventHistoryResult} for more details.
 */
@BeanDefinition
public class HistoryEvent implements ImmutableBean, Comparable<HistoryEvent> {

  /**
   * The type of event.
   */
  @PropertyDefinition(validate = "notNull")
  private final HistoryEventType _type;
  /**
   * The unique identifier of the version.
   */
  @PropertyDefinition(validate = "notNull")
  private final UniqueId _uniqueId;
  /**
   * The user that performed the event.
   */
  @PropertyDefinition(validate = "notNull")
  private final String _userName;
  /**
   * The instant that the event occurred.
   */
  @PropertyDefinition(validate = "notNull")
  private final Instant _instant;
  /**
   * The changes that occurred.
   * This list is a description of the important changes that occurred.
   * Not all changes are included, each master selects those it wants to expose.
   * It is not the intention to allow the recreation of an old version of the master.
   */
  @PropertyDefinition(validate = "notNull")
  private final ImmutableList<String> _changes;

  //-------------------------------------------------------------------------
  /**
   * Creates a history event.
   *
   * @param type  the type, not null
   * @param uniqueId  the unique identifier, not null
   * @param userName  the user name, not null
   * @param instant  the instant, not null
   * @param changes  the changes that occurred, not null
   * @return the event, not null
   */
  public static HistoryEvent of(final HistoryEventType type, final UniqueId uniqueId, final String userName,
      final Instant instant, final List<String> changes) {
    return new HistoryEvent(type, uniqueId, userName, instant, changes);
  }

  //-------------------------------------------------------------------------
  /**
   * Creates an instance.
   *
   * @param type  the type, not null
   * @param uniqueId  the unique identifier, not null
   * @param userName  the user name, not null
   * @param instant  the instant, not null
   * @param changes  the changes that occurred, not null
   */
  private HistoryEvent(final HistoryEventType type, final UniqueId uniqueId, final String userName, final Instant instant, final List<String> changes) {
    JodaBeanUtils.notNull(type, "type");
    JodaBeanUtils.notNull(uniqueId, "uniqueId");
    JodaBeanUtils.notNull(userName, "userName");
    JodaBeanUtils.notNull(instant, "instant");
    JodaBeanUtils.notNull(changes, "changes");
    this._type = type;
    this._uniqueId = uniqueId;
    this._userName = userName;
    this._instant = instant;
    this._changes = ImmutableList.copyOf(changes);
  }

  //-------------------------------------------------------------------------
  /**
   * Compares this event to another sorting in instant order.
   *
   * @param other  the other event, not null
   * @return the comparison result
   */
  @Override
  public int compareTo(final HistoryEvent other) {
    return ComparisonChain.start()
        .compare(getInstant(), other.getInstant())
        .compare(getUniqueId(), other.getUniqueId())
        .result();
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code HistoryEvent}.
   * @return the meta-bean, not null
   */
  public static HistoryEvent.Meta meta() {
    return HistoryEvent.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(HistoryEvent.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static HistoryEvent.Builder builder() {
    return new HistoryEvent.Builder();
  }

  /**
   * Restricted constructor.
   * @param builder  the builder to copy from, not null
   */
  protected HistoryEvent(HistoryEvent.Builder builder) {
    JodaBeanUtils.notNull(builder._type, "type");
    JodaBeanUtils.notNull(builder._uniqueId, "uniqueId");
    JodaBeanUtils.notNull(builder._userName, "userName");
    JodaBeanUtils.notNull(builder._instant, "instant");
    JodaBeanUtils.notNull(builder._changes, "changes");
    this._type = builder._type;
    this._uniqueId = builder._uniqueId;
    this._userName = builder._userName;
    this._instant = builder._instant;
    this._changes = ImmutableList.copyOf(builder._changes);
  }

  @Override
  public HistoryEvent.Meta metaBean() {
    return HistoryEvent.Meta.INSTANCE;
  }

  @Override
  public <R> Property<R> property(String propertyName) {
    return metaBean().<R>metaProperty(propertyName).createProperty(this);
  }

  @Override
  public Set<String> propertyNames() {
    return metaBean().metaPropertyMap().keySet();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the type of event.
   * @return the value of the property, not null
   */
  public HistoryEventType getType() {
    return _type;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the unique identifier of the version.
   * @return the value of the property, not null
   */
  public UniqueId getUniqueId() {
    return _uniqueId;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the user that performed the event.
   * @return the value of the property, not null
   */
  public String getUserName() {
    return _userName;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the instant that the event occurred.
   * @return the value of the property, not null
   */
  public Instant getInstant() {
    return _instant;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the changes that occurred.
   * This list is a description of the important changes that occurred.
   * Not all changes are included, each master selects those it wants to expose.
   * It is not the intention to allow the recreation of an old version of the master.
   * @return the value of the property, not null
   */
  public ImmutableList<String> getChanges() {
    return _changes;
  }

  //-----------------------------------------------------------------------
  /**
   * Returns a builder that allows this bean to be mutated.
   * @return the mutable builder, not null
   */
  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      HistoryEvent other = (HistoryEvent) obj;
      return JodaBeanUtils.equal(getType(), other.getType()) &&
          JodaBeanUtils.equal(getUniqueId(), other.getUniqueId()) &&
          JodaBeanUtils.equal(getUserName(), other.getUserName()) &&
          JodaBeanUtils.equal(getInstant(), other.getInstant()) &&
          JodaBeanUtils.equal(getChanges(), other.getChanges());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getType());
    hash = hash * 31 + JodaBeanUtils.hashCode(getUniqueId());
    hash = hash * 31 + JodaBeanUtils.hashCode(getUserName());
    hash = hash * 31 + JodaBeanUtils.hashCode(getInstant());
    hash = hash * 31 + JodaBeanUtils.hashCode(getChanges());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(192);
    buf.append("HistoryEvent{");
    int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  protected void toString(StringBuilder buf) {
    buf.append("type").append('=').append(JodaBeanUtils.toString(getType())).append(',').append(' ');
    buf.append("uniqueId").append('=').append(JodaBeanUtils.toString(getUniqueId())).append(',').append(' ');
    buf.append("userName").append('=').append(JodaBeanUtils.toString(getUserName())).append(',').append(' ');
    buf.append("instant").append('=').append(JodaBeanUtils.toString(getInstant())).append(',').append(' ');
    buf.append("changes").append('=').append(JodaBeanUtils.toString(getChanges())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code HistoryEvent}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code type} property.
     */
    private final MetaProperty<HistoryEventType> _type = DirectMetaProperty.ofImmutable(
        this, "type", HistoryEvent.class, HistoryEventType.class);
    /**
     * The meta-property for the {@code uniqueId} property.
     */
    private final MetaProperty<UniqueId> _uniqueId = DirectMetaProperty.ofImmutable(
        this, "uniqueId", HistoryEvent.class, UniqueId.class);
    /**
     * The meta-property for the {@code userName} property.
     */
    private final MetaProperty<String> _userName = DirectMetaProperty.ofImmutable(
        this, "userName", HistoryEvent.class, String.class);
    /**
     * The meta-property for the {@code instant} property.
     */
    private final MetaProperty<Instant> _instant = DirectMetaProperty.ofImmutable(
        this, "instant", HistoryEvent.class, Instant.class);
    /**
     * The meta-property for the {@code changes} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ImmutableList<String>> _changes = DirectMetaProperty.ofImmutable(
        this, "changes", HistoryEvent.class, (Class) ImmutableList.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "type",
        "uniqueId",
        "userName",
        "instant",
        "changes");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3575610:  // type
          return _type;
        case -294460212:  // uniqueId
          return _uniqueId;
        case -266666762:  // userName
          return _userName;
        case 1957570017:  // instant
          return _instant;
        case 738943683:  // changes
          return _changes;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public HistoryEvent.Builder builder() {
      return new HistoryEvent.Builder();
    }

    @Override
    public Class<? extends HistoryEvent> beanType() {
      return HistoryEvent.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code type} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<HistoryEventType> type() {
      return _type;
    }

    /**
     * The meta-property for the {@code uniqueId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UniqueId> uniqueId() {
      return _uniqueId;
    }

    /**
     * The meta-property for the {@code userName} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> userName() {
      return _userName;
    }

    /**
     * The meta-property for the {@code instant} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Instant> instant() {
      return _instant;
    }

    /**
     * The meta-property for the {@code changes} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ImmutableList<String>> changes() {
      return _changes;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 3575610:  // type
          return ((HistoryEvent) bean).getType();
        case -294460212:  // uniqueId
          return ((HistoryEvent) bean).getUniqueId();
        case -266666762:  // userName
          return ((HistoryEvent) bean).getUserName();
        case 1957570017:  // instant
          return ((HistoryEvent) bean).getInstant();
        case 738943683:  // changes
          return ((HistoryEvent) bean).getChanges();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      metaProperty(propertyName);
      if (quiet) {
        return;
      }
      throw new UnsupportedOperationException("Property cannot be written: " + propertyName);
    }

  }

  //-----------------------------------------------------------------------
  /**
   * The bean-builder for {@code HistoryEvent}.
   */
  public static class Builder extends DirectFieldsBeanBuilder<HistoryEvent> {

    private HistoryEventType _type;
    private UniqueId _uniqueId;
    private String _userName;
    private Instant _instant;
    private List<String> _changes = ImmutableList.of();

    /**
     * Restricted constructor.
     */
    protected Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    protected Builder(HistoryEvent beanToCopy) {
      this._type = beanToCopy.getType();
      this._uniqueId = beanToCopy.getUniqueId();
      this._userName = beanToCopy.getUserName();
      this._instant = beanToCopy.getInstant();
      this._changes = beanToCopy.getChanges();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3575610:  // type
          return _type;
        case -294460212:  // uniqueId
          return _uniqueId;
        case -266666762:  // userName
          return _userName;
        case 1957570017:  // instant
          return _instant;
        case 738943683:  // changes
          return _changes;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 3575610:  // type
          this._type = (HistoryEventType) newValue;
          break;
        case -294460212:  // uniqueId
          this._uniqueId = (UniqueId) newValue;
          break;
        case -266666762:  // userName
          this._userName = (String) newValue;
          break;
        case 1957570017:  // instant
          this._instant = (Instant) newValue;
          break;
        case 738943683:  // changes
          this._changes = (List<String>) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public Builder set(MetaProperty<?> property, Object value) {
      super.set(property, value);
      return this;
    }

    @Override
    public Builder setString(String propertyName, String value) {
      setString(meta().metaProperty(propertyName), value);
      return this;
    }

    @Override
    public Builder setString(MetaProperty<?> property, String value) {
      super.setString(property, value);
      return this;
    }

    @Override
    public Builder setAll(Map<String, ? extends Object> propertyValueMap) {
      super.setAll(propertyValueMap);
      return this;
    }

    @Override
    public HistoryEvent build() {
      return new HistoryEvent(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the type of event.
     * @param type  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder type(HistoryEventType type) {
      JodaBeanUtils.notNull(type, "type");
      this._type = type;
      return this;
    }

    /**
     * Sets the unique identifier of the version.
     * @param uniqueId  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder uniqueId(UniqueId uniqueId) {
      JodaBeanUtils.notNull(uniqueId, "uniqueId");
      this._uniqueId = uniqueId;
      return this;
    }

    /**
     * Sets the user that performed the event.
     * @param userName  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder userName(String userName) {
      JodaBeanUtils.notNull(userName, "userName");
      this._userName = userName;
      return this;
    }

    /**
     * Sets the instant that the event occurred.
     * @param instant  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder instant(Instant instant) {
      JodaBeanUtils.notNull(instant, "instant");
      this._instant = instant;
      return this;
    }

    /**
     * Sets the changes that occurred.
     * This list is a description of the important changes that occurred.
     * Not all changes are included, each master selects those it wants to expose.
     * It is not the intention to allow the recreation of an old version of the master.
     * @param changes  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder changes(List<String> changes) {
      JodaBeanUtils.notNull(changes, "changes");
      this._changes = changes;
      return this;
    }

    /**
     * Sets the {@code changes} property in the builder
     * from an array of objects.
     * @param changes  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder changes(String... changes) {
      return changes(ImmutableList.copyOf(changes));
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(192);
      buf.append("HistoryEvent.Builder{");
      int len = buf.length();
      toString(buf);
      if (buf.length() > len) {
        buf.setLength(buf.length() - 2);
      }
      buf.append('}');
      return buf.toString();
    }

    protected void toString(StringBuilder buf) {
      buf.append("type").append('=').append(JodaBeanUtils.toString(_type)).append(',').append(' ');
      buf.append("uniqueId").append('=').append(JodaBeanUtils.toString(_uniqueId)).append(',').append(' ');
      buf.append("userName").append('=').append(JodaBeanUtils.toString(_userName)).append(',').append(' ');
      buf.append("instant").append('=').append(JodaBeanUtils.toString(_instant)).append(',').append(' ');
      buf.append("changes").append('=').append(JodaBeanUtils.toString(_changes)).append(',').append(' ');
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

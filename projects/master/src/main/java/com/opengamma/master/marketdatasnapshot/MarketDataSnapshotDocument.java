/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.master.marketdatasnapshot;

import java.io.Serializable;
import java.util.Map;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.core.marketdatasnapshot.NamedSnapshot;
import com.opengamma.core.marketdatasnapshot.impl.ManageableMarketDataSnapshot;
import com.opengamma.id.UniqueId;
import com.opengamma.master.AbstractDocument;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.PublicSPI;

/**
 * A document used to pass into and out of the snapshot master. Despite the
 * name, this document is capable of storing any type of named snapshot.
 * <p>
 * The snapshot master provides full management of the snapshot database.
 * Each element is stored in a document.
 *
 * @see MarketDataSnapshotMaster
 */
@PublicSPI
@BeanDefinition
public class MarketDataSnapshotDocument extends AbstractDocument implements Serializable {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * The snapshot object held by the document.
   */
  @PropertyDefinition(validate = "notNull", set = "manual")
  private NamedSnapshot _namedSnapshot;

  /**
   * The type of the snapshot. Only required so that it can be stored
   * in the master and can therefore be used for searching, it is not
   * exposed externally.
   */
  @PropertyDefinition(validate = "notNull", set = "private", get = "private")
  private Class<? extends NamedSnapshot> _snapshotType;

  /**
   * The snapshot document unique identifier.
   * This field is managed by the master but must be set for updates.
   */
  @PropertyDefinition
  private UniqueId _uniqueId;

  /**
   * Gets the snapshot object held by the document.
   *
   * @return the value of the property, not null
   * @deprecated use {@link #getNamedSnapshot()} instead
   */
  @Deprecated
  public ManageableMarketDataSnapshot getSnapshot() {
    return getNamedSnapshot(ManageableMarketDataSnapshot.class);
  }

  /**
   * Sets the snapshot object held by the document.
   *
   * @param snapshot  the new value of the property, not null
   * @deprecated use {@link #setNamedSnapshot(NamedSnapshot)} instead
   */
  @Deprecated
  public void setSnapshot(final ManageableMarketDataSnapshot snapshot) {
    setNamedSnapshot(snapshot);
  }

  /**
   * Sets the snapshot object held by the document.
   * @param namedSnapshot  the new value of the property, not null
   */
  public void setNamedSnapshot(final NamedSnapshot namedSnapshot) {
    _namedSnapshot = ArgumentChecker.notNull(namedSnapshot, "namedSnapshot");
    _snapshotType = namedSnapshot.getClass();
  }

  /**
   * Gets the snapshot object held by the document.
   *
   * @param <T>  the required type for the snapshot
   * @param type  the required type for the snapshot
   * @return the value of the property, not null
   */
  public <T extends NamedSnapshot> T getNamedSnapshot(final Class<T> type) {

    if (type.isAssignableFrom(_namedSnapshot.getClass())) {
      return type.cast(_namedSnapshot);
    } else {
      throw new IllegalStateException("Snapshot is of type: " + _namedSnapshot.getClass() + " but expected type: " + type);
    }
  }

  /**
   * Creates an instance.
   */
  public MarketDataSnapshotDocument() {
  }

  /**
   * Creates an instance from a snapshot and an id.
   *
   * @param uniqueId  the unique identifier, may be null
   * @param snapshot  the snapshot, not null
   */
  public MarketDataSnapshotDocument(final UniqueId uniqueId, final NamedSnapshot snapshot) {
    setUniqueId(uniqueId);
    setNamedSnapshot(snapshot);
  }

  /**
   * Creates an instance from a snapshot.
   *
   * @param snapshot  the snapshot, not null
   */
  public MarketDataSnapshotDocument(final NamedSnapshot snapshot) {
    setUniqueId(snapshot.getUniqueId());
    setNamedSnapshot(snapshot);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the name of the snapshot.
   * <p>
   * This is derived from the snapshot itself.
   *
   * @return the name, null if no name has been set yet
   */
  public String getName() {
    return getNamedSnapshot() != null ? getNamedSnapshot().getName() : null;
  }

  @Override
  public NamedSnapshot getValue() {
    return getNamedSnapshot();
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code MarketDataSnapshotDocument}.
   * @return the meta-bean, not null
   */
  public static MarketDataSnapshotDocument.Meta meta() {
    return MarketDataSnapshotDocument.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(MarketDataSnapshotDocument.Meta.INSTANCE);
  }

  @Override
  public MarketDataSnapshotDocument.Meta metaBean() {
    return MarketDataSnapshotDocument.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the snapshot object held by the document.
   * @return the value of the property, not null
   */
  public NamedSnapshot getNamedSnapshot() {
    return _namedSnapshot;
  }

  /**
   * Gets the the {@code namedSnapshot} property.
   * @return the property, not null
   */
  public final Property<NamedSnapshot> namedSnapshot() {
    return metaBean().namedSnapshot().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the type of the snapshot. Only required so that it can be stored
   * in the master and can therefore be used for searching, it is not
   * exposed externally.
   * @return the value of the property, not null
   */
  private Class<? extends NamedSnapshot> getSnapshotType() {
    return _snapshotType;
  }

  /**
   * Sets the type of the snapshot. Only required so that it can be stored
   * in the master and can therefore be used for searching, it is not
   * exposed externally.
   * @param snapshotType  the new value of the property, not null
   */
  private void setSnapshotType(Class<? extends NamedSnapshot> snapshotType) {
    JodaBeanUtils.notNull(snapshotType, "snapshotType");
    this._snapshotType = snapshotType;
  }

  /**
   * Gets the the {@code snapshotType} property.
   * in the master and can therefore be used for searching, it is not
   * exposed externally.
   * @return the property, not null
   */
  public final Property<Class<? extends NamedSnapshot>> snapshotType() {
    return metaBean().snapshotType().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the snapshot document unique identifier.
   * This field is managed by the master but must be set for updates.
   * @return the value of the property
   */
  public UniqueId getUniqueId() {
    return _uniqueId;
  }

  /**
   * Sets the snapshot document unique identifier.
   * This field is managed by the master but must be set for updates.
   * @param uniqueId  the new value of the property
   */
  public void setUniqueId(UniqueId uniqueId) {
    this._uniqueId = uniqueId;
  }

  /**
   * Gets the the {@code uniqueId} property.
   * This field is managed by the master but must be set for updates.
   * @return the property, not null
   */
  public final Property<UniqueId> uniqueId() {
    return metaBean().uniqueId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public MarketDataSnapshotDocument clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      MarketDataSnapshotDocument other = (MarketDataSnapshotDocument) obj;
      return JodaBeanUtils.equal(getNamedSnapshot(), other.getNamedSnapshot()) &&
          JodaBeanUtils.equal(getSnapshotType(), other.getSnapshotType()) &&
          JodaBeanUtils.equal(getUniqueId(), other.getUniqueId()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = hash * 31 + JodaBeanUtils.hashCode(getNamedSnapshot());
    hash = hash * 31 + JodaBeanUtils.hashCode(getSnapshotType());
    hash = hash * 31 + JodaBeanUtils.hashCode(getUniqueId());
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(128);
    buf.append("MarketDataSnapshotDocument{");
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
    buf.append("namedSnapshot").append('=').append(JodaBeanUtils.toString(getNamedSnapshot())).append(',').append(' ');
    buf.append("snapshotType").append('=').append(JodaBeanUtils.toString(getSnapshotType())).append(',').append(' ');
    buf.append("uniqueId").append('=').append(JodaBeanUtils.toString(getUniqueId())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code MarketDataSnapshotDocument}.
   */
  public static class Meta extends AbstractDocument.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code namedSnapshot} property.
     */
    private final MetaProperty<NamedSnapshot> _namedSnapshot = DirectMetaProperty.ofReadWrite(
        this, "namedSnapshot", MarketDataSnapshotDocument.class, NamedSnapshot.class);
    /**
     * The meta-property for the {@code snapshotType} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<Class<? extends NamedSnapshot>> _snapshotType = DirectMetaProperty.ofReadWrite(
        this, "snapshotType", MarketDataSnapshotDocument.class, (Class) Class.class);
    /**
     * The meta-property for the {@code uniqueId} property.
     */
    private final MetaProperty<UniqueId> _uniqueId = DirectMetaProperty.ofReadWrite(
        this, "uniqueId", MarketDataSnapshotDocument.class, UniqueId.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "namedSnapshot",
        "snapshotType",
        "uniqueId");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 747030557:  // namedSnapshot
          return _namedSnapshot;
        case -931506402:  // snapshotType
          return _snapshotType;
        case -294460212:  // uniqueId
          return _uniqueId;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends MarketDataSnapshotDocument> builder() {
      return new DirectBeanBuilder<MarketDataSnapshotDocument>(new MarketDataSnapshotDocument());
    }

    @Override
    public Class<? extends MarketDataSnapshotDocument> beanType() {
      return MarketDataSnapshotDocument.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code namedSnapshot} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<NamedSnapshot> namedSnapshot() {
      return _namedSnapshot;
    }

    /**
     * The meta-property for the {@code snapshotType} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Class<? extends NamedSnapshot>> snapshotType() {
      return _snapshotType;
    }

    /**
     * The meta-property for the {@code uniqueId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UniqueId> uniqueId() {
      return _uniqueId;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 747030557:  // namedSnapshot
          return ((MarketDataSnapshotDocument) bean).getNamedSnapshot();
        case -931506402:  // snapshotType
          return ((MarketDataSnapshotDocument) bean).getSnapshotType();
        case -294460212:  // uniqueId
          return ((MarketDataSnapshotDocument) bean).getUniqueId();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 747030557:  // namedSnapshot
          ((MarketDataSnapshotDocument) bean).setNamedSnapshot((NamedSnapshot) newValue);
          return;
        case -931506402:  // snapshotType
          ((MarketDataSnapshotDocument) bean).setSnapshotType((Class<? extends NamedSnapshot>) newValue);
          return;
        case -294460212:  // uniqueId
          ((MarketDataSnapshotDocument) bean).setUniqueId((UniqueId) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((MarketDataSnapshotDocument) bean)._namedSnapshot, "namedSnapshot");
      JodaBeanUtils.notNull(((MarketDataSnapshotDocument) bean)._snapshotType, "snapshotType");
      super.validate(bean);
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

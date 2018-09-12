/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.master.position;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.id.VersionCorrection;
import com.opengamma.master.AbstractSearchResult;
import com.opengamma.util.PublicSPI;

/**
 * Result from searching for positions.
 * <p>
 * The returned documents will match the search criteria.
 * See {@link PositionSearchRequest} for more details.
 */
@PublicSPI
@BeanDefinition
public class PositionSearchResult extends AbstractSearchResult<PositionDocument> {

  /**
   * Creates an instance.
   */
  public PositionSearchResult() {
  }

  /**
   * Creates an instance from a collection of documents.
   *
   * @param coll  the collection of documents to add, not null
   */
  public PositionSearchResult(final Collection<PositionDocument> coll) {
    super(coll);
  }

  /**
   * Creates an instance specifying the version-correction searched for.
   *
   * @param versionCorrection  the version-correction of the data, not null
   */
  public PositionSearchResult(final VersionCorrection versionCorrection) {
    setVersionCorrection(versionCorrection);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the returned positions from within the documents.
   *
   * @return the positions, not null
   */
  public List<ManageablePosition> getPositions() {
    final List<ManageablePosition> result = new ArrayList<>();
    if (getDocuments() != null) {
      for (final PositionDocument doc : getDocuments()) {
        result.add(doc.getPosition());
      }
    }
    return result;
  }

  /**
   * Gets the first position, or null if no documents.
   *
   * @return the first position, null if none
   */
  public ManageablePosition getFirstPosition() {
    return getDocuments().size() > 0 ? getDocuments().get(0).getPosition() : null;
  }

  /**
   * Gets the single result expected from a query.
   * <p>
   * This throws an exception if more than 1 result is actually available.
   * Thus, this method implies an assumption about uniqueness of the queried position.
   *
   * @return the matching position, not null
   * @throws IllegalStateException if no position was found
   */
  public ManageablePosition getSinglePosition() {
    if (getDocuments().size() != 1) {
      throw new OpenGammaRuntimeException("Expecting single resulting match, and was " + getDocuments().size());
    }
    return getDocuments().get(0).getPosition();
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code PositionSearchResult}.
   * @return the meta-bean, not null
   */
  public static PositionSearchResult.Meta meta() {
    return PositionSearchResult.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(PositionSearchResult.Meta.INSTANCE);
  }

  @Override
  public PositionSearchResult.Meta metaBean() {
    return PositionSearchResult.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  @Override
  public PositionSearchResult clone() {
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
    buf.append("PositionSearchResult{");
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
   * The meta-bean for {@code PositionSearchResult}.
   */
  public static class Meta extends AbstractSearchResult.Meta<PositionDocument> {
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
    public BeanBuilder<? extends PositionSearchResult> builder() {
      return new DirectBeanBuilder<PositionSearchResult>(new PositionSearchResult());
    }

    @Override
    public Class<? extends PositionSearchResult> beanType() {
      return PositionSearchResult.class;
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

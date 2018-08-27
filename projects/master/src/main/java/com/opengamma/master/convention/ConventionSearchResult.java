/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.master.convention;

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
 * Result from searching for conventions.
 * <p>
 * The returned documents will match the search criteria.
 * See {@link ConventionSearchRequest} for more details.
 */
@PublicSPI
@BeanDefinition
public class ConventionSearchResult extends AbstractSearchResult<ConventionDocument> {

  /**
   * Creates an instance.
   */
  public ConventionSearchResult() {
  }

  /**
   * Creates an instance from a collection of documents.
   *
   * @param coll  the collection of documents to add, not null
   */
  public ConventionSearchResult(final Collection<ConventionDocument> coll) {
    super(coll);
  }

  /**
   * Creates an instance specifying the version-correction searched for.
   *
   * @param versionCorrection  the version-correction of the data, not null
   */
  public ConventionSearchResult(final VersionCorrection versionCorrection) {
    setVersionCorrection(versionCorrection);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the returned conventions from within the documents.
   *
   * @return the conventions, not null
   */
  public List<ManageableConvention> getConventions() {
    final List<ManageableConvention> result = new ArrayList<>();
    if (getDocuments() != null) {
      for (final ConventionDocument doc : getDocuments()) {
        result.add(doc.getConvention());
      }
    }
    return result;
  }

  /**
   * Gets the first convention, or null if no documents.
   *
   * @return the first convention, null if none
   */
  public ManageableConvention getFirstConvention() {
    return getDocuments().size() > 0 ? getDocuments().get(0).getConvention() : null;
  }

  /**
   * Gets the single result expected from a query.
   * <p>
   * This throws an exception if more than 1 result is actually available.
   * Thus, this method implies an assumption about uniqueness of the queried convention.
   *
   * @return the matching convention, not null
   * @throws IllegalStateException if no convention was found
   */
  public ManageableConvention getSingleConvention() {
    if (getDocuments().size() != 1) {
      throw new OpenGammaRuntimeException("Expecting zero or single resulting match, and was " + getDocuments().size());
    } else {
      return getDocuments().get(0).getConvention();
    }
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code ConventionSearchResult}.
   * @return the meta-bean, not null
   */
  public static ConventionSearchResult.Meta meta() {
    return ConventionSearchResult.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(ConventionSearchResult.Meta.INSTANCE);
  }

  @Override
  public ConventionSearchResult.Meta metaBean() {
    return ConventionSearchResult.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  @Override
  public ConventionSearchResult clone() {
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
    buf.append("ConventionSearchResult{");
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
   * The meta-bean for {@code ConventionSearchResult}.
   */
  public static class Meta extends AbstractSearchResult.Meta<ConventionDocument> {
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
    public BeanBuilder<? extends ConventionSearchResult> builder() {
      return new DirectBeanBuilder<ConventionSearchResult>(new ConventionSearchResult());
    }

    @Override
    public Class<? extends ConventionSearchResult> beanType() {
      return ConventionSearchResult.class;
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

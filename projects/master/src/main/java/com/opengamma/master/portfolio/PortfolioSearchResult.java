/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.master.portfolio;

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
 * Result from searching for portfolio trees.
 * <p>
 * The returned documents will match the search criteria.
 * See {@link PortfolioSearchRequest} for more details.
 * <p>
 * This returns multiple instances of the tree excluding positions, which may be a large response.
 * The depth parameter in the request allows the size of the result to be controlled.
 */
@PublicSPI
@BeanDefinition
public class PortfolioSearchResult extends AbstractSearchResult<PortfolioDocument> {

  /**
   * Creates an instance.
   */
  public PortfolioSearchResult() {
  }

  /**
   * Creates an instance from a collection of documents.
   *
   * @param coll  the collection of documents to add, not null
   */
  public PortfolioSearchResult(final Collection<PortfolioDocument> coll) {
    super(coll);
  }

  /**
   * Creates an instance specifying the version-correction searched for.
   *
   * @param versionCorrection  the version-correction of the data, not null
   */
  public PortfolioSearchResult(final VersionCorrection versionCorrection) {
    setVersionCorrection(versionCorrection);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the returned portfolios from within the documents.
   *
   * @return the portfolios, not null
   */
  public List<ManageablePortfolio> getPortfolios() {
    final List<ManageablePortfolio> result = new ArrayList<>();
    if (getDocuments() != null) {
      for (final PortfolioDocument doc : getDocuments()) {
        result.add(doc.getPortfolio());
      }
    }
    return result;
  }

  /**
   * Gets the first portfolio, or null if no documents.
   *
   * @return the first portfolio, null if none
   */
  public ManageablePortfolio getFirstPortfolio() {
    return getDocuments().size() > 0 ? getDocuments().get(0).getPortfolio() : null;
  }

  /**
   * Gets the single result expected from a query.
   * <p>
   * This throws an exception if more than 1 result is actually available.
   * Thus, this method implies an assumption about uniqueness of the queried portfolio.
   *
   * @return the matching portfolio, not null
   * @throws IllegalStateException if no portfolio was found
   */
  public ManageablePortfolio getSinglePortfolio() {
    if (getDocuments().size() != 1) {
      throw new OpenGammaRuntimeException("Expecting zero or single resulting match, and was " + getDocuments().size());
    } else {
      return getDocuments().get(0).getPortfolio();
    }
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code PortfolioSearchResult}.
   * @return the meta-bean, not null
   */
  public static PortfolioSearchResult.Meta meta() {
    return PortfolioSearchResult.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(PortfolioSearchResult.Meta.INSTANCE);
  }

  @Override
  public PortfolioSearchResult.Meta metaBean() {
    return PortfolioSearchResult.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  @Override
  public PortfolioSearchResult clone() {
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
    buf.append("PortfolioSearchResult{");
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
   * The meta-bean for {@code PortfolioSearchResult}.
   */
  public static class Meta extends AbstractSearchResult.Meta<PortfolioDocument> {
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
    public BeanBuilder<? extends PortfolioSearchResult> builder() {
      return new DirectBeanBuilder<PortfolioSearchResult>(new PortfolioSearchResult());
    }

    @Override
    public Class<? extends PortfolioSearchResult> beanType() {
      return PortfolioSearchResult.class;
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

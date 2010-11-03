/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security.master;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.beans.BeanDefinition;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.BasicMetaBean;
import org.joda.beans.impl.direct.DirectBean;
import org.joda.beans.impl.direct.DirectMetaProperty;

import com.opengamma.engine.security.DefaultSecurity;
import com.opengamma.util.db.Paging;

/**
 * Result providing the history of a security.
 * <p>
 * The returned documents may be a mixture of versions and corrections.
 * The document instant fields are used to identify which are which.
 * See {@link SecurityHistoryRequest} for more details.
 */
@BeanDefinition
public class SecurityHistoryResult extends DirectBean {

  /**
   * The paging information.
   */
  @PropertyDefinition
  private Paging _paging;
  /**
   * The list of matched security documents.
   */
  @PropertyDefinition
  private final List<SecurityDocument> _documents = new ArrayList<SecurityDocument>();

  /**
   * Creates an instance.
   */
  public SecurityHistoryResult() {
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the returned securities from within the documents.
   * @return the securities, not null
   */
  public List<DefaultSecurity> getSecurities() {
    List<DefaultSecurity> result = new ArrayList<DefaultSecurity>();
    if (_documents != null) {
      for (SecurityDocument doc : _documents) {
        result.add(doc.getSecurity());
      }
    }
    return result;
  }

  /**
   * Gets the first document, or null if no documents.
   * @return the first document, null if none
   */
  public SecurityDocument getFirstDocument() {
    return getDocuments().size() > 0 ? getDocuments().get(0) : null;
  }

  /**
   * Gets the first security, or null if no documents.
   * @return the first security, null if none
   */
  public DefaultSecurity getFirstSecurity() {
    return getDocuments().size() > 0 ? getDocuments().get(0).getSecurity() : null;
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code SecurityHistoryResult}.
   * @return the meta-bean, not null
   */
  public static SecurityHistoryResult.Meta meta() {
    return SecurityHistoryResult.Meta.INSTANCE;
  }

  @Override
  public SecurityHistoryResult.Meta metaBean() {
    return SecurityHistoryResult.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName) {
    switch (propertyName.hashCode()) {
      case -995747956:  // paging
        return getPaging();
      case 943542968:  // documents
        return getDocuments();
    }
    return super.propertyGet(propertyName);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void propertySet(String propertyName, Object newValue) {
    switch (propertyName.hashCode()) {
      case -995747956:  // paging
        setPaging((Paging) newValue);
        return;
      case 943542968:  // documents
        setDocuments((List<SecurityDocument>) newValue);
        return;
    }
    super.propertySet(propertyName, newValue);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the paging information.
   * @return the value of the property
   */
  public Paging getPaging() {
    return _paging;
  }

  /**
   * Sets the paging information.
   * @param paging  the new value of the property
   */
  public void setPaging(Paging paging) {
    this._paging = paging;
  }

  /**
   * Gets the the {@code paging} property.
   * @return the property, not null
   */
  public final Property<Paging> paging() {
    return metaBean().paging().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the list of matched security documents.
   * @return the value of the property
   */
  public List<SecurityDocument> getDocuments() {
    return _documents;
  }

  /**
   * Sets the list of matched security documents.
   * @param documents  the new value of the property
   */
  public void setDocuments(List<SecurityDocument> documents) {
    this._documents.clear();
    this._documents.addAll(documents);
  }

  /**
   * Gets the the {@code documents} property.
   * @return the property, not null
   */
  public final Property<List<SecurityDocument>> documents() {
    return metaBean().documents().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code SecurityHistoryResult}.
   */
  public static class Meta extends BasicMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code paging} property.
     */
    private final MetaProperty<Paging> _paging = DirectMetaProperty.ofReadWrite(this, "paging", Paging.class);
    /**
     * The meta-property for the {@code documents} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<List<SecurityDocument>> _documents = DirectMetaProperty.ofReadWrite(this, "documents", (Class) List.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<Object>> _map;

    @SuppressWarnings({"unchecked", "rawtypes" })
    protected Meta() {
      LinkedHashMap temp = new LinkedHashMap();
      temp.put("paging", _paging);
      temp.put("documents", _documents);
      _map = Collections.unmodifiableMap(temp);
    }

    @Override
    public SecurityHistoryResult createBean() {
      return new SecurityHistoryResult();
    }

    @Override
    public Class<? extends SecurityHistoryResult> beanType() {
      return SecurityHistoryResult.class;
    }

    @Override
    public Map<String, MetaProperty<Object>> metaPropertyMap() {
      return _map;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code paging} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Paging> paging() {
      return _paging;
    }

    /**
     * The meta-property for the {@code documents} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<List<SecurityDocument>> documents() {
      return _documents;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

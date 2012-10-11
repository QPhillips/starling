/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.util.db.tool;

import java.io.Closeable;
import java.util.Map;
import java.util.Set;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBean;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.springframework.core.io.Resource;

import com.opengamma.util.db.DbConnector;
import com.opengamma.util.db.management.DbManagement;

/**
 * A standard context that is used to provide components to database tools.
 */
@BeanDefinition
public class DbToolContext extends DirectBean implements Closeable {

  /**
   * The database connector.
   */
  @PropertyDefinition
  private DbConnector _dbConnector;
  /**
   * The database management instance.
   */
  @PropertyDefinition
  private DbManagement _dbManagement;
  /**
   * The database catalog name.
   */
  @PropertyDefinition
  private String _catalog;
  /**
   * The schema groups to be operated on.
   */
  @PropertyDefinition
  private Set<String> _schemaGroups;
  /**
   * A resource pointing to the root of the database installation scripts.
   */
  @PropertyDefinition
  private Resource _scriptsResource;
  
  @Override
  public void close() {
    if (getDbConnector() != null) {
      getDbConnector().close();
      setDbConnector(null);
    }
  }
  
  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code DbToolContext}.
   * @return the meta-bean, not null
   */
  public static DbToolContext.Meta meta() {
    return DbToolContext.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(DbToolContext.Meta.INSTANCE);
  }

  @Override
  public DbToolContext.Meta metaBean() {
    return DbToolContext.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 39794031:  // dbConnector
        return getDbConnector();
      case 209279841:  // dbManagement
        return getDbManagement();
      case 555704345:  // catalog
        return getCatalog();
      case -1949073707:  // schemaGroups
        return getSchemaGroups();
      case 1948576054:  // scriptsResource
        return getScriptsResource();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 39794031:  // dbConnector
        setDbConnector((DbConnector) newValue);
        return;
      case 209279841:  // dbManagement
        setDbManagement((DbManagement) newValue);
        return;
      case 555704345:  // catalog
        setCatalog((String) newValue);
        return;
      case -1949073707:  // schemaGroups
        setSchemaGroups((Set<String>) newValue);
        return;
      case 1948576054:  // scriptsResource
        setScriptsResource((Resource) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      DbToolContext other = (DbToolContext) obj;
      return JodaBeanUtils.equal(getDbConnector(), other.getDbConnector()) &&
          JodaBeanUtils.equal(getDbManagement(), other.getDbManagement()) &&
          JodaBeanUtils.equal(getCatalog(), other.getCatalog()) &&
          JodaBeanUtils.equal(getSchemaGroups(), other.getSchemaGroups()) &&
          JodaBeanUtils.equal(getScriptsResource(), other.getScriptsResource());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getDbConnector());
    hash += hash * 31 + JodaBeanUtils.hashCode(getDbManagement());
    hash += hash * 31 + JodaBeanUtils.hashCode(getCatalog());
    hash += hash * 31 + JodaBeanUtils.hashCode(getSchemaGroups());
    hash += hash * 31 + JodaBeanUtils.hashCode(getScriptsResource());
    return hash;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the database connector.
   * @return the value of the property
   */
  public DbConnector getDbConnector() {
    return _dbConnector;
  }

  /**
   * Sets the database connector.
   * @param dbConnector  the new value of the property
   */
  public void setDbConnector(DbConnector dbConnector) {
    this._dbConnector = dbConnector;
  }

  /**
   * Gets the the {@code dbConnector} property.
   * @return the property, not null
   */
  public final Property<DbConnector> dbConnector() {
    return metaBean().dbConnector().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the database management instance.
   * @return the value of the property
   */
  public DbManagement getDbManagement() {
    return _dbManagement;
  }

  /**
   * Sets the database management instance.
   * @param dbManagement  the new value of the property
   */
  public void setDbManagement(DbManagement dbManagement) {
    this._dbManagement = dbManagement;
  }

  /**
   * Gets the the {@code dbManagement} property.
   * @return the property, not null
   */
  public final Property<DbManagement> dbManagement() {
    return metaBean().dbManagement().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the database catalog name.
   * @return the value of the property
   */
  public String getCatalog() {
    return _catalog;
  }

  /**
   * Sets the database catalog name.
   * @param catalog  the new value of the property
   */
  public void setCatalog(String catalog) {
    this._catalog = catalog;
  }

  /**
   * Gets the the {@code catalog} property.
   * @return the property, not null
   */
  public final Property<String> catalog() {
    return metaBean().catalog().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the schema groups to be operated on.
   * @return the value of the property
   */
  public Set<String> getSchemaGroups() {
    return _schemaGroups;
  }

  /**
   * Sets the schema groups to be operated on.
   * @param schemaGroups  the new value of the property
   */
  public void setSchemaGroups(Set<String> schemaGroups) {
    this._schemaGroups = schemaGroups;
  }

  /**
   * Gets the the {@code schemaGroups} property.
   * @return the property, not null
   */
  public final Property<Set<String>> schemaGroups() {
    return metaBean().schemaGroups().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets a resource pointing to the root of the database installation scripts.
   * @return the value of the property
   */
  public Resource getScriptsResource() {
    return _scriptsResource;
  }

  /**
   * Sets a resource pointing to the root of the database installation scripts.
   * @param scriptsResource  the new value of the property
   */
  public void setScriptsResource(Resource scriptsResource) {
    this._scriptsResource = scriptsResource;
  }

  /**
   * Gets the the {@code scriptsResource} property.
   * @return the property, not null
   */
  public final Property<Resource> scriptsResource() {
    return metaBean().scriptsResource().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code DbToolContext}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code dbConnector} property.
     */
    private final MetaProperty<DbConnector> _dbConnector = DirectMetaProperty.ofReadWrite(
        this, "dbConnector", DbToolContext.class, DbConnector.class);
    /**
     * The meta-property for the {@code dbManagement} property.
     */
    private final MetaProperty<DbManagement> _dbManagement = DirectMetaProperty.ofReadWrite(
        this, "dbManagement", DbToolContext.class, DbManagement.class);
    /**
     * The meta-property for the {@code catalog} property.
     */
    private final MetaProperty<String> _catalog = DirectMetaProperty.ofReadWrite(
        this, "catalog", DbToolContext.class, String.class);
    /**
     * The meta-property for the {@code schemaGroups} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<Set<String>> _schemaGroups = DirectMetaProperty.ofReadWrite(
        this, "schemaGroups", DbToolContext.class, (Class) Set.class);
    /**
     * The meta-property for the {@code scriptsResource} property.
     */
    private final MetaProperty<Resource> _scriptsResource = DirectMetaProperty.ofReadWrite(
        this, "scriptsResource", DbToolContext.class, Resource.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "dbConnector",
        "dbManagement",
        "catalog",
        "schemaGroups",
        "scriptsResource");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 39794031:  // dbConnector
          return _dbConnector;
        case 209279841:  // dbManagement
          return _dbManagement;
        case 555704345:  // catalog
          return _catalog;
        case -1949073707:  // schemaGroups
          return _schemaGroups;
        case 1948576054:  // scriptsResource
          return _scriptsResource;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends DbToolContext> builder() {
      return new DirectBeanBuilder<DbToolContext>(new DbToolContext());
    }

    @Override
    public Class<? extends DbToolContext> beanType() {
      return DbToolContext.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code dbConnector} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<DbConnector> dbConnector() {
      return _dbConnector;
    }

    /**
     * The meta-property for the {@code dbManagement} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<DbManagement> dbManagement() {
      return _dbManagement;
    }

    /**
     * The meta-property for the {@code catalog} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> catalog() {
      return _catalog;
    }

    /**
     * The meta-property for the {@code schemaGroups} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Set<String>> schemaGroups() {
      return _schemaGroups;
    }

    /**
     * The meta-property for the {@code scriptsResource} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Resource> scriptsResource() {
      return _scriptsResource;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
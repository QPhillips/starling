/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.function.config;

import java.util.Map;

import org.joda.beans.Bean;
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

import com.opengamma.util.ArgumentChecker;

/**
 * Static function configuration representation
 */
@BeanDefinition
public class StaticFunctionConfiguration extends DirectBean implements FunctionConfiguration {

  private static final long serialVersionUID = 1L;

  @PropertyDefinition(validate = "notNull")
  private String _definitionClassName;

  /**
   * Creates an instance
   *
   * @param definitionClassName the definition class name, not-null.
   */
  public StaticFunctionConfiguration(final String definitionClassName) {
    ArgumentChecker.notNull(definitionClassName, "definitionClassName");
    _definitionClassName = definitionClassName;
  }

  /**
   * Constructor for builder
   */
  StaticFunctionConfiguration() {
  }

  @Override
  public int compareTo(final FunctionConfiguration other) {
    if (other instanceof ParameterizedFunctionConfiguration) {
      // Static goes first
      return -1;
    } else if (other instanceof StaticFunctionConfiguration) {
      // Sort by class name
      return _definitionClassName.compareTo(((StaticFunctionConfiguration) other)._definitionClassName);
    }
    throw new UnsupportedOperationException("Can't compare " + StaticFunctionConfiguration.class + " and " + other.getClass());
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code StaticFunctionConfiguration}.
   * @return the meta-bean, not null
   */
  public static StaticFunctionConfiguration.Meta meta() {
    return StaticFunctionConfiguration.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(StaticFunctionConfiguration.Meta.INSTANCE);
  }

  @Override
  public StaticFunctionConfiguration.Meta metaBean() {
    return StaticFunctionConfiguration.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the definitionClassName.
   * @return the value of the property, not null
   */
  public String getDefinitionClassName() {
    return _definitionClassName;
  }

  /**
   * Sets the definitionClassName.
   * @param definitionClassName  the new value of the property, not null
   */
  public void setDefinitionClassName(String definitionClassName) {
    JodaBeanUtils.notNull(definitionClassName, "definitionClassName");
    this._definitionClassName = definitionClassName;
  }

  /**
   * Gets the the {@code definitionClassName} property.
   * @return the property, not null
   */
  public final Property<String> definitionClassName() {
    return metaBean().definitionClassName().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public StaticFunctionConfiguration clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      StaticFunctionConfiguration other = (StaticFunctionConfiguration) obj;
      return JodaBeanUtils.equal(getDefinitionClassName(), other.getDefinitionClassName());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getDefinitionClassName());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(64);
    buf.append("StaticFunctionConfiguration{");
    int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  protected void toString(StringBuilder buf) {
    buf.append("definitionClassName").append('=').append(JodaBeanUtils.toString(getDefinitionClassName())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code StaticFunctionConfiguration}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code definitionClassName} property.
     */
    private final MetaProperty<String> _definitionClassName = DirectMetaProperty.ofReadWrite(
        this, "definitionClassName", StaticFunctionConfiguration.class, String.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "definitionClassName");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 95245328:  // definitionClassName
          return _definitionClassName;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends StaticFunctionConfiguration> builder() {
      return new DirectBeanBuilder<StaticFunctionConfiguration>(new StaticFunctionConfiguration());
    }

    @Override
    public Class<? extends StaticFunctionConfiguration> beanType() {
      return StaticFunctionConfiguration.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code definitionClassName} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> definitionClassName() {
      return _definitionClassName;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 95245328:  // definitionClassName
          return ((StaticFunctionConfiguration) bean).getDefinitionClassName();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 95245328:  // definitionClassName
          ((StaticFunctionConfiguration) bean).setDefinitionClassName((String) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((StaticFunctionConfiguration) bean)._definitionClassName, "definitionClassName");
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

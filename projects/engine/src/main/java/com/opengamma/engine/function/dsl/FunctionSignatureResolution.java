/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.function.dsl;

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

/**
 * The set of inputs and outputs.
 */
@BeanDefinition
public class FunctionSignatureResolution extends DirectBean {

  /**
   * The resolved inputs.
   */
  @PropertyDefinition(validate = "notNull")
  private InputsResolution _inputs;
  /**
   * The resolved outputs.
   */
  @PropertyDefinition(validate = "notNull")
  private OutputsResolution _outputs;

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code FunctionSignatureResolution}.
   * @return the meta-bean, not null
   */
  public static FunctionSignatureResolution.Meta meta() {
    return FunctionSignatureResolution.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(FunctionSignatureResolution.Meta.INSTANCE);
  }

  @Override
  public FunctionSignatureResolution.Meta metaBean() {
    return FunctionSignatureResolution.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the resolved inputs.
   * @return the value of the property, not null
   */
  public InputsResolution getInputs() {
    return _inputs;
  }

  /**
   * Sets the resolved inputs.
   * @param inputs  the new value of the property, not null
   */
  public void setInputs(InputsResolution inputs) {
    JodaBeanUtils.notNull(inputs, "inputs");
    this._inputs = inputs;
  }

  /**
   * Gets the the {@code inputs} property.
   * @return the property, not null
   */
  public final Property<InputsResolution> inputs() {
    return metaBean().inputs().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the resolved outputs.
   * @return the value of the property, not null
   */
  public OutputsResolution getOutputs() {
    return _outputs;
  }

  /**
   * Sets the resolved outputs.
   * @param outputs  the new value of the property, not null
   */
  public void setOutputs(OutputsResolution outputs) {
    JodaBeanUtils.notNull(outputs, "outputs");
    this._outputs = outputs;
  }

  /**
   * Gets the the {@code outputs} property.
   * @return the property, not null
   */
  public final Property<OutputsResolution> outputs() {
    return metaBean().outputs().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public FunctionSignatureResolution clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      FunctionSignatureResolution other = (FunctionSignatureResolution) obj;
      return JodaBeanUtils.equal(getInputs(), other.getInputs()) &&
          JodaBeanUtils.equal(getOutputs(), other.getOutputs());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getInputs());
    hash = hash * 31 + JodaBeanUtils.hashCode(getOutputs());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("FunctionSignatureResolution{");
    int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  protected void toString(StringBuilder buf) {
    buf.append("inputs").append('=').append(JodaBeanUtils.toString(getInputs())).append(',').append(' ');
    buf.append("outputs").append('=').append(JodaBeanUtils.toString(getOutputs())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code FunctionSignatureResolution}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code inputs} property.
     */
    private final MetaProperty<InputsResolution> _inputs = DirectMetaProperty.ofReadWrite(
        this, "inputs", FunctionSignatureResolution.class, InputsResolution.class);
    /**
     * The meta-property for the {@code outputs} property.
     */
    private final MetaProperty<OutputsResolution> _outputs = DirectMetaProperty.ofReadWrite(
        this, "outputs", FunctionSignatureResolution.class, OutputsResolution.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "inputs",
        "outputs");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1183866391:  // inputs
          return _inputs;
        case -1106114670:  // outputs
          return _outputs;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends FunctionSignatureResolution> builder() {
      return new DirectBeanBuilder<FunctionSignatureResolution>(new FunctionSignatureResolution());
    }

    @Override
    public Class<? extends FunctionSignatureResolution> beanType() {
      return FunctionSignatureResolution.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code inputs} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<InputsResolution> inputs() {
      return _inputs;
    }

    /**
     * The meta-property for the {@code outputs} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<OutputsResolution> outputs() {
      return _outputs;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -1183866391:  // inputs
          return ((FunctionSignatureResolution) bean).getInputs();
        case -1106114670:  // outputs
          return ((FunctionSignatureResolution) bean).getOutputs();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -1183866391:  // inputs
          ((FunctionSignatureResolution) bean).setInputs((InputsResolution) newValue);
          return;
        case -1106114670:  // outputs
          ((FunctionSignatureResolution) bean).setOutputs((OutputsResolution) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((FunctionSignatureResolution) bean)._inputs, "inputs");
      JodaBeanUtils.notNull(((FunctionSignatureResolution) bean)._outputs, "outputs");
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

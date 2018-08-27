/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.component.factory.master;

import java.util.LinkedHashMap;
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

import com.opengamma.component.ComponentInfo;
import com.opengamma.component.ComponentRepository;
import com.opengamma.component.factory.AbstractComponentFactory;
import com.opengamma.master.exchange.ExchangeMaster;
import com.opengamma.master.exchange.impl.DataExchangeMasterResource;
import com.opengamma.master.exchange.impl.DataTrackingExchangeMaster;

/**
 * Component factory for {@link DataTrackingExchangeMaster}.
 */
@BeanDefinition
public class DataTrackingExchangeMasterComponentFactory extends AbstractComponentFactory {

  @PropertyDefinition(validate = "notNull")
  private String _classifier;

  @PropertyDefinition(validate = "notNull")
  private ExchangeMaster _trackedMaster;

  @Override
  public void init(final ComponentRepository repo, final LinkedHashMap<String, String> configuration) throws Exception {

    final ComponentInfo componentInfo = new ComponentInfo(ExchangeMaster.class, _classifier);

    final DataTrackingExchangeMaster dataTrackingExchangeMaster = new DataTrackingExchangeMaster(_trackedMaster);

    repo.registerComponent(componentInfo, dataTrackingExchangeMaster);

    repo.getRestComponents().publish(componentInfo, new DataExchangeMasterResource(_trackedMaster));


  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code DataTrackingExchangeMasterComponentFactory}.
   * @return the meta-bean, not null
   */
  public static DataTrackingExchangeMasterComponentFactory.Meta meta() {
    return DataTrackingExchangeMasterComponentFactory.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(DataTrackingExchangeMasterComponentFactory.Meta.INSTANCE);
  }

  @Override
  public DataTrackingExchangeMasterComponentFactory.Meta metaBean() {
    return DataTrackingExchangeMasterComponentFactory.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the classifier.
   * @return the value of the property, not null
   */
  public String getClassifier() {
    return _classifier;
  }

  /**
   * Sets the classifier.
   * @param classifier  the new value of the property, not null
   */
  public void setClassifier(String classifier) {
    JodaBeanUtils.notNull(classifier, "classifier");
    this._classifier = classifier;
  }

  /**
   * Gets the the {@code classifier} property.
   * @return the property, not null
   */
  public final Property<String> classifier() {
    return metaBean().classifier().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the trackedMaster.
   * @return the value of the property, not null
   */
  public ExchangeMaster getTrackedMaster() {
    return _trackedMaster;
  }

  /**
   * Sets the trackedMaster.
   * @param trackedMaster  the new value of the property, not null
   */
  public void setTrackedMaster(ExchangeMaster trackedMaster) {
    JodaBeanUtils.notNull(trackedMaster, "trackedMaster");
    this._trackedMaster = trackedMaster;
  }

  /**
   * Gets the the {@code trackedMaster} property.
   * @return the property, not null
   */
  public final Property<ExchangeMaster> trackedMaster() {
    return metaBean().trackedMaster().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public DataTrackingExchangeMasterComponentFactory clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      DataTrackingExchangeMasterComponentFactory other = (DataTrackingExchangeMasterComponentFactory) obj;
      return JodaBeanUtils.equal(getClassifier(), other.getClassifier()) &&
          JodaBeanUtils.equal(getTrackedMaster(), other.getTrackedMaster()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = hash * 31 + JodaBeanUtils.hashCode(getClassifier());
    hash = hash * 31 + JodaBeanUtils.hashCode(getTrackedMaster());
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("DataTrackingExchangeMasterComponentFactory{");
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
    buf.append("classifier").append('=').append(JodaBeanUtils.toString(getClassifier())).append(',').append(' ');
    buf.append("trackedMaster").append('=').append(JodaBeanUtils.toString(getTrackedMaster())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code DataTrackingExchangeMasterComponentFactory}.
   */
  public static class Meta extends AbstractComponentFactory.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code classifier} property.
     */
    private final MetaProperty<String> _classifier = DirectMetaProperty.ofReadWrite(
        this, "classifier", DataTrackingExchangeMasterComponentFactory.class, String.class);
    /**
     * The meta-property for the {@code trackedMaster} property.
     */
    private final MetaProperty<ExchangeMaster> _trackedMaster = DirectMetaProperty.ofReadWrite(
        this, "trackedMaster", DataTrackingExchangeMasterComponentFactory.class, ExchangeMaster.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "classifier",
        "trackedMaster");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          return _classifier;
        case -1965332948:  // trackedMaster
          return _trackedMaster;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends DataTrackingExchangeMasterComponentFactory> builder() {
      return new DirectBeanBuilder<DataTrackingExchangeMasterComponentFactory>(new DataTrackingExchangeMasterComponentFactory());
    }

    @Override
    public Class<? extends DataTrackingExchangeMasterComponentFactory> beanType() {
      return DataTrackingExchangeMasterComponentFactory.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code classifier} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> classifier() {
      return _classifier;
    }

    /**
     * The meta-property for the {@code trackedMaster} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ExchangeMaster> trackedMaster() {
      return _trackedMaster;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          return ((DataTrackingExchangeMasterComponentFactory) bean).getClassifier();
        case -1965332948:  // trackedMaster
          return ((DataTrackingExchangeMasterComponentFactory) bean).getTrackedMaster();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          ((DataTrackingExchangeMasterComponentFactory) bean).setClassifier((String) newValue);
          return;
        case -1965332948:  // trackedMaster
          ((DataTrackingExchangeMasterComponentFactory) bean).setTrackedMaster((ExchangeMaster) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((DataTrackingExchangeMasterComponentFactory) bean)._classifier, "classifier");
      JodaBeanUtils.notNull(((DataTrackingExchangeMasterComponentFactory) bean)._trackedMaster, "trackedMaster");
      super.validate(bean);
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

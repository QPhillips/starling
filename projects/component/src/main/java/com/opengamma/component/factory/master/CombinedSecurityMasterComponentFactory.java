/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.component.factory.master;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

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
import com.opengamma.component.factory.ComponentInfoAttributes;
import com.opengamma.master.security.SecurityMaster;
import com.opengamma.master.security.impl.DataSecurityMasterResource;
import com.opengamma.master.security.impl.DelegatingSecurityMaster;
import com.opengamma.master.security.impl.RemoteSecurityMaster;

/**
 * Component factory for the combined security master.
 * <p>
 * The delegate security masters are specify by securityMaster&lt;index&gt; = SecurityMaster::&lt;classifier&gt; in the .ini config file
 */
@BeanDefinition
public class CombinedSecurityMasterComponentFactory extends AbstractComponentFactory {

  /**
   * The classifier that the factory should publish under.
   */
  @PropertyDefinition(validate = "notNull")
  private String _classifier;
  /**
   * The flag determining whether the component should be published by REST (default true).
   */
  @PropertyDefinition
  private boolean _publishRest = true;
  /**
   * The default security master.
   */
  @PropertyDefinition(validate = "notNull")
  private SecurityMaster _defaultSecurityMaster;

  //-------------------------------------------------------------------------
  @Override
  public void init(final ComponentRepository repo, final LinkedHashMap<String, String> configuration) {
    final Map<String, SecurityMaster> map = new HashMap<>();
    final String defaultSecurityScheme = repo.getInfo(getDefaultSecurityMaster()).getAttribute(ComponentInfoAttributes.UNIQUE_ID_SCHEME);
    map.put(defaultSecurityScheme, getDefaultSecurityMaster());

    // all additional PositionMaster instances
    final Map<String, ComponentInfo> infos = repo.findInfos(configuration);
    for (final Entry<String, ComponentInfo> entry : infos.entrySet()) {
      final String key = entry.getKey();
      final ComponentInfo info = entry.getValue();
      if (key.matches("securityMaster[0-9]+") && info.getType() == SecurityMaster.class) {
        final SecurityMaster securityMaster = (SecurityMaster) repo.getInstance(info);
        final String uniqueIdScheme = repo.getInfo(securityMaster).getAttribute(ComponentInfoAttributes.UNIQUE_ID_SCHEME);
        map.put(uniqueIdScheme, securityMaster);
        configuration.remove(key);
      }
    }
    final SecurityMaster master = new DelegatingSecurityMaster(getDefaultSecurityMaster(), map);

    // register
    final ComponentInfo info = new ComponentInfo(SecurityMaster.class, getClassifier());
    info.addAttribute(ComponentInfoAttributes.LEVEL, 2);
    if (isPublishRest()) {
      info.addAttribute(ComponentInfoAttributes.REMOTE_CLIENT_JAVA, RemoteSecurityMaster.class);
    }
    repo.registerComponent(info, master);
    if (isPublishRest()) {
      repo.getRestComponents().publish(info, new DataSecurityMasterResource(master));
    }
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code CombinedSecurityMasterComponentFactory}.
   * @return the meta-bean, not null
   */
  public static CombinedSecurityMasterComponentFactory.Meta meta() {
    return CombinedSecurityMasterComponentFactory.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(CombinedSecurityMasterComponentFactory.Meta.INSTANCE);
  }

  @Override
  public CombinedSecurityMasterComponentFactory.Meta metaBean() {
    return CombinedSecurityMasterComponentFactory.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the classifier that the factory should publish under.
   * @return the value of the property, not null
   */
  public String getClassifier() {
    return _classifier;
  }

  /**
   * Sets the classifier that the factory should publish under.
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
   * Gets the flag determining whether the component should be published by REST (default true).
   * @return the value of the property
   */
  public boolean isPublishRest() {
    return _publishRest;
  }

  /**
   * Sets the flag determining whether the component should be published by REST (default true).
   * @param publishRest  the new value of the property
   */
  public void setPublishRest(boolean publishRest) {
    this._publishRest = publishRest;
  }

  /**
   * Gets the the {@code publishRest} property.
   * @return the property, not null
   */
  public final Property<Boolean> publishRest() {
    return metaBean().publishRest().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the default security master.
   * @return the value of the property, not null
   */
  public SecurityMaster getDefaultSecurityMaster() {
    return _defaultSecurityMaster;
  }

  /**
   * Sets the default security master.
   * @param defaultSecurityMaster  the new value of the property, not null
   */
  public void setDefaultSecurityMaster(SecurityMaster defaultSecurityMaster) {
    JodaBeanUtils.notNull(defaultSecurityMaster, "defaultSecurityMaster");
    this._defaultSecurityMaster = defaultSecurityMaster;
  }

  /**
   * Gets the the {@code defaultSecurityMaster} property.
   * @return the property, not null
   */
  public final Property<SecurityMaster> defaultSecurityMaster() {
    return metaBean().defaultSecurityMaster().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public CombinedSecurityMasterComponentFactory clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      CombinedSecurityMasterComponentFactory other = (CombinedSecurityMasterComponentFactory) obj;
      return JodaBeanUtils.equal(getClassifier(), other.getClassifier()) &&
          (isPublishRest() == other.isPublishRest()) &&
          JodaBeanUtils.equal(getDefaultSecurityMaster(), other.getDefaultSecurityMaster()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = hash * 31 + JodaBeanUtils.hashCode(getClassifier());
    hash = hash * 31 + JodaBeanUtils.hashCode(isPublishRest());
    hash = hash * 31 + JodaBeanUtils.hashCode(getDefaultSecurityMaster());
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(128);
    buf.append("CombinedSecurityMasterComponentFactory{");
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
    buf.append("publishRest").append('=').append(JodaBeanUtils.toString(isPublishRest())).append(',').append(' ');
    buf.append("defaultSecurityMaster").append('=').append(JodaBeanUtils.toString(getDefaultSecurityMaster())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code CombinedSecurityMasterComponentFactory}.
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
        this, "classifier", CombinedSecurityMasterComponentFactory.class, String.class);
    /**
     * The meta-property for the {@code publishRest} property.
     */
    private final MetaProperty<Boolean> _publishRest = DirectMetaProperty.ofReadWrite(
        this, "publishRest", CombinedSecurityMasterComponentFactory.class, Boolean.TYPE);
    /**
     * The meta-property for the {@code defaultSecurityMaster} property.
     */
    private final MetaProperty<SecurityMaster> _defaultSecurityMaster = DirectMetaProperty.ofReadWrite(
        this, "defaultSecurityMaster", CombinedSecurityMasterComponentFactory.class, SecurityMaster.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "classifier",
        "publishRest",
        "defaultSecurityMaster");

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
        case -614707837:  // publishRest
          return _publishRest;
        case 163592803:  // defaultSecurityMaster
          return _defaultSecurityMaster;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends CombinedSecurityMasterComponentFactory> builder() {
      return new DirectBeanBuilder<CombinedSecurityMasterComponentFactory>(new CombinedSecurityMasterComponentFactory());
    }

    @Override
    public Class<? extends CombinedSecurityMasterComponentFactory> beanType() {
      return CombinedSecurityMasterComponentFactory.class;
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
     * The meta-property for the {@code publishRest} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Boolean> publishRest() {
      return _publishRest;
    }

    /**
     * The meta-property for the {@code defaultSecurityMaster} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<SecurityMaster> defaultSecurityMaster() {
      return _defaultSecurityMaster;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          return ((CombinedSecurityMasterComponentFactory) bean).getClassifier();
        case -614707837:  // publishRest
          return ((CombinedSecurityMasterComponentFactory) bean).isPublishRest();
        case 163592803:  // defaultSecurityMaster
          return ((CombinedSecurityMasterComponentFactory) bean).getDefaultSecurityMaster();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          ((CombinedSecurityMasterComponentFactory) bean).setClassifier((String) newValue);
          return;
        case -614707837:  // publishRest
          ((CombinedSecurityMasterComponentFactory) bean).setPublishRest((Boolean) newValue);
          return;
        case 163592803:  // defaultSecurityMaster
          ((CombinedSecurityMasterComponentFactory) bean).setDefaultSecurityMaster((SecurityMaster) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((CombinedSecurityMasterComponentFactory) bean)._classifier, "classifier");
      JodaBeanUtils.notNull(((CombinedSecurityMasterComponentFactory) bean)._defaultSecurityMaster, "defaultSecurityMaster");
      super.validate(bean);
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

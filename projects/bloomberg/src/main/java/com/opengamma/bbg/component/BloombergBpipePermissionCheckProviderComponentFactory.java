/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.bbg.component;

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
import org.threeten.bp.Duration;

import com.opengamma.bbg.BloombergConnector;
import com.opengamma.bbg.BloombergPermissions;
import com.opengamma.bbg.permission.BloombergBpipePermissionCheckProvider;
import com.opengamma.bbg.referencedata.ReferenceDataProvider;
import com.opengamma.component.ComponentInfo;
import com.opengamma.component.ComponentRepository;
import com.opengamma.component.factory.AbstractComponentFactory;
import com.opengamma.component.factory.ComponentInfoAttributes;
import com.opengamma.provider.permission.PermissionCheckProvider;
import com.opengamma.provider.permission.impl.DataPermissionCheckProviderResource;
import com.opengamma.provider.permission.impl.ProviderBasedPermissionResolver;
import com.opengamma.provider.permission.impl.RemotePermissionCheckProvider;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.auth.AuthUtils;

/**
 * Component factory for the Bloomberg permission check provider.
 */
@BeanDefinition
public class BloombergBpipePermissionCheckProviderComponentFactory extends AbstractComponentFactory {

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
   * The Bloomberg connector.
   */
  @PropertyDefinition(validate = "notNull")
  private BloombergConnector _bloombergConnector;
  /**
   * The identity expiry time in hours
   * <p>
   * Defaults to 24hrs if not set.
   */
  @PropertyDefinition
  private Duration _identityExpiryTime = Duration.ofHours(24);
  /**
   * The reference data provider.
   */
  @PropertyDefinition(validate = "notNull")
  private ReferenceDataProvider _referenceDataProvider;

  //-------------------------------------------------------------------------
  @Override
  public void init(final ComponentRepository repo, final LinkedHashMap<String, String> configuration) throws Exception {
    ArgumentChecker.isTrue(getIdentityExpiryTime().getSeconds() > 0, "identity expiry time must be positive");

    final ComponentInfo info = new ComponentInfo(PermissionCheckProvider.class, getClassifier());
    info.addAttribute(ComponentInfoAttributes.LEVEL, 1);
    if (isPublishRest()) {
      info.addAttribute(ComponentInfoAttributes.REMOTE_CLIENT_JAVA, RemotePermissionCheckProvider.class);
    }
    info.addAttribute(ComponentInfoAttributes.ACCEPTED_TYPES, BloombergPermissions.BLOOMBERG_PREFIX);

    final BloombergBpipePermissionCheckProvider provider = new BloombergBpipePermissionCheckProvider(
        getBloombergConnector(), getIdentityExpiryTime());
    repo.registerComponent(info, provider);
    if (isPublishRest()) {
      repo.getRestComponents().publish(info, new DataPermissionCheckProviderResource(provider));
    }
    if (AuthUtils.isPermissive() == false) {
      AuthUtils.getPermissionResolver().register(
          new ProviderBasedPermissionResolver(BloombergPermissions.BLOOMBERG_PREFIX, provider));
    }
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code BloombergBpipePermissionCheckProviderComponentFactory}.
   * @return the meta-bean, not null
   */
  public static BloombergBpipePermissionCheckProviderComponentFactory.Meta meta() {
    return BloombergBpipePermissionCheckProviderComponentFactory.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(BloombergBpipePermissionCheckProviderComponentFactory.Meta.INSTANCE);
  }

  @Override
  public BloombergBpipePermissionCheckProviderComponentFactory.Meta metaBean() {
    return BloombergBpipePermissionCheckProviderComponentFactory.Meta.INSTANCE;
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
   * Gets the Bloomberg connector.
   * @return the value of the property, not null
   */
  public BloombergConnector getBloombergConnector() {
    return _bloombergConnector;
  }

  /**
   * Sets the Bloomberg connector.
   * @param bloombergConnector  the new value of the property, not null
   */
  public void setBloombergConnector(BloombergConnector bloombergConnector) {
    JodaBeanUtils.notNull(bloombergConnector, "bloombergConnector");
    this._bloombergConnector = bloombergConnector;
  }

  /**
   * Gets the the {@code bloombergConnector} property.
   * @return the property, not null
   */
  public final Property<BloombergConnector> bloombergConnector() {
    return metaBean().bloombergConnector().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the identity expiry time in hours
   * <p>
   * Defaults to 24hrs if not set.
   * @return the value of the property
   */
  public Duration getIdentityExpiryTime() {
    return _identityExpiryTime;
  }

  /**
   * Sets the identity expiry time in hours
   * <p>
   * Defaults to 24hrs if not set.
   * @param identityExpiryTime  the new value of the property
   */
  public void setIdentityExpiryTime(Duration identityExpiryTime) {
    this._identityExpiryTime = identityExpiryTime;
  }

  /**
   * Gets the the {@code identityExpiryTime} property.
   * <p>
   * Defaults to 24hrs if not set.
   * @return the property, not null
   */
  public final Property<Duration> identityExpiryTime() {
    return metaBean().identityExpiryTime().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the reference data provider.
   * @return the value of the property, not null
   */
  public ReferenceDataProvider getReferenceDataProvider() {
    return _referenceDataProvider;
  }

  /**
   * Sets the reference data provider.
   * @param referenceDataProvider  the new value of the property, not null
   */
  public void setReferenceDataProvider(ReferenceDataProvider referenceDataProvider) {
    JodaBeanUtils.notNull(referenceDataProvider, "referenceDataProvider");
    this._referenceDataProvider = referenceDataProvider;
  }

  /**
   * Gets the the {@code referenceDataProvider} property.
   * @return the property, not null
   */
  public final Property<ReferenceDataProvider> referenceDataProvider() {
    return metaBean().referenceDataProvider().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public BloombergBpipePermissionCheckProviderComponentFactory clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      BloombergBpipePermissionCheckProviderComponentFactory other = (BloombergBpipePermissionCheckProviderComponentFactory) obj;
      return JodaBeanUtils.equal(getClassifier(), other.getClassifier()) &&
          (isPublishRest() == other.isPublishRest()) &&
          JodaBeanUtils.equal(getBloombergConnector(), other.getBloombergConnector()) &&
          JodaBeanUtils.equal(getIdentityExpiryTime(), other.getIdentityExpiryTime()) &&
          JodaBeanUtils.equal(getReferenceDataProvider(), other.getReferenceDataProvider()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = hash * 31 + JodaBeanUtils.hashCode(getClassifier());
    hash = hash * 31 + JodaBeanUtils.hashCode(isPublishRest());
    hash = hash * 31 + JodaBeanUtils.hashCode(getBloombergConnector());
    hash = hash * 31 + JodaBeanUtils.hashCode(getIdentityExpiryTime());
    hash = hash * 31 + JodaBeanUtils.hashCode(getReferenceDataProvider());
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(192);
    buf.append("BloombergBpipePermissionCheckProviderComponentFactory{");
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
    buf.append("bloombergConnector").append('=').append(JodaBeanUtils.toString(getBloombergConnector())).append(',').append(' ');
    buf.append("identityExpiryTime").append('=').append(JodaBeanUtils.toString(getIdentityExpiryTime())).append(',').append(' ');
    buf.append("referenceDataProvider").append('=').append(JodaBeanUtils.toString(getReferenceDataProvider())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code BloombergBpipePermissionCheckProviderComponentFactory}.
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
        this, "classifier", BloombergBpipePermissionCheckProviderComponentFactory.class, String.class);
    /**
     * The meta-property for the {@code publishRest} property.
     */
    private final MetaProperty<Boolean> _publishRest = DirectMetaProperty.ofReadWrite(
        this, "publishRest", BloombergBpipePermissionCheckProviderComponentFactory.class, Boolean.TYPE);
    /**
     * The meta-property for the {@code bloombergConnector} property.
     */
    private final MetaProperty<BloombergConnector> _bloombergConnector = DirectMetaProperty.ofReadWrite(
        this, "bloombergConnector", BloombergBpipePermissionCheckProviderComponentFactory.class, BloombergConnector.class);
    /**
     * The meta-property for the {@code identityExpiryTime} property.
     */
    private final MetaProperty<Duration> _identityExpiryTime = DirectMetaProperty.ofReadWrite(
        this, "identityExpiryTime", BloombergBpipePermissionCheckProviderComponentFactory.class, Duration.class);
    /**
     * The meta-property for the {@code referenceDataProvider} property.
     */
    private final MetaProperty<ReferenceDataProvider> _referenceDataProvider = DirectMetaProperty.ofReadWrite(
        this, "referenceDataProvider", BloombergBpipePermissionCheckProviderComponentFactory.class, ReferenceDataProvider.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "classifier",
        "publishRest",
        "bloombergConnector",
        "identityExpiryTime",
        "referenceDataProvider");

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
        case 2061648978:  // bloombergConnector
          return _bloombergConnector;
        case 201583102:  // identityExpiryTime
          return _identityExpiryTime;
        case -1788671322:  // referenceDataProvider
          return _referenceDataProvider;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends BloombergBpipePermissionCheckProviderComponentFactory> builder() {
      return new DirectBeanBuilder<BloombergBpipePermissionCheckProviderComponentFactory>(new BloombergBpipePermissionCheckProviderComponentFactory());
    }

    @Override
    public Class<? extends BloombergBpipePermissionCheckProviderComponentFactory> beanType() {
      return BloombergBpipePermissionCheckProviderComponentFactory.class;
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
     * The meta-property for the {@code bloombergConnector} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<BloombergConnector> bloombergConnector() {
      return _bloombergConnector;
    }

    /**
     * The meta-property for the {@code identityExpiryTime} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Duration> identityExpiryTime() {
      return _identityExpiryTime;
    }

    /**
     * The meta-property for the {@code referenceDataProvider} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ReferenceDataProvider> referenceDataProvider() {
      return _referenceDataProvider;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          return ((BloombergBpipePermissionCheckProviderComponentFactory) bean).getClassifier();
        case -614707837:  // publishRest
          return ((BloombergBpipePermissionCheckProviderComponentFactory) bean).isPublishRest();
        case 2061648978:  // bloombergConnector
          return ((BloombergBpipePermissionCheckProviderComponentFactory) bean).getBloombergConnector();
        case 201583102:  // identityExpiryTime
          return ((BloombergBpipePermissionCheckProviderComponentFactory) bean).getIdentityExpiryTime();
        case -1788671322:  // referenceDataProvider
          return ((BloombergBpipePermissionCheckProviderComponentFactory) bean).getReferenceDataProvider();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          ((BloombergBpipePermissionCheckProviderComponentFactory) bean).setClassifier((String) newValue);
          return;
        case -614707837:  // publishRest
          ((BloombergBpipePermissionCheckProviderComponentFactory) bean).setPublishRest((Boolean) newValue);
          return;
        case 2061648978:  // bloombergConnector
          ((BloombergBpipePermissionCheckProviderComponentFactory) bean).setBloombergConnector((BloombergConnector) newValue);
          return;
        case 201583102:  // identityExpiryTime
          ((BloombergBpipePermissionCheckProviderComponentFactory) bean).setIdentityExpiryTime((Duration) newValue);
          return;
        case -1788671322:  // referenceDataProvider
          ((BloombergBpipePermissionCheckProviderComponentFactory) bean).setReferenceDataProvider((ReferenceDataProvider) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((BloombergBpipePermissionCheckProviderComponentFactory) bean)._classifier, "classifier");
      JodaBeanUtils.notNull(((BloombergBpipePermissionCheckProviderComponentFactory) bean)._bloombergConnector, "bloombergConnector");
      JodaBeanUtils.notNull(((BloombergBpipePermissionCheckProviderComponentFactory) bean)._referenceDataProvider, "referenceDataProvider");
      super.validate(bean);
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

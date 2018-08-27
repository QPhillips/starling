/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.component.factory.infrastructure;

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
import com.opengamma.util.redis.RedisConnector;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * Component factory for {@code RedisConnector}.
 * <p>
 * This class is designed to allow protected methods to be overridden.
 */
@BeanDefinition
public class RedisConnectorComponentFactory extends AbstractComponentFactory {

  /**
   * The classifier that the factory should publish under.
   */
  @PropertyDefinition(validate = "notEmpty")
  private String _classifier;
  /**
   * The Redis host name.
   */
  @PropertyDefinition(validate = "notEmpty")
  private String _hostName;
  /**
   * The Redis host port.
   */
  @PropertyDefinition
  private int _redisPort = 6379;
  /**
   * The Redis password.
   */
  @PropertyDefinition
  private String _password;
  /**
   * The Redis timeout.
   */
  @PropertyDefinition
  private int _timeOut = Protocol.DEFAULT_TIMEOUT;

  //-------------------------------------------------------------------------
  @Override
  public void init(final ComponentRepository repo, final LinkedHashMap<String, String> configuration) throws Exception {
    final RedisConnector redisConnector = createRedisConnector(repo);
    final ComponentInfo info = new ComponentInfo(RedisConnector.class, getClassifier());
    repo.registerComponent(info, redisConnector);
  }

  /**
   * Creates the Redis connector without registering it.
   *
   * @param repo  the component repository, only used to register secondary items like lifecycle, not null
   * @return the Redis connector, not null
   */
  protected RedisConnector createRedisConnector(final ComponentRepository repo) {
    final JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxActive(Runtime.getRuntime().availableProcessors() + 5);
    return new RedisConnector(getClassifier(), getHostName(), getRedisPort(), getPassword(), poolConfig, getTimeOut());
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code RedisConnectorComponentFactory}.
   * @return the meta-bean, not null
   */
  public static RedisConnectorComponentFactory.Meta meta() {
    return RedisConnectorComponentFactory.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(RedisConnectorComponentFactory.Meta.INSTANCE);
  }

  @Override
  public RedisConnectorComponentFactory.Meta metaBean() {
    return RedisConnectorComponentFactory.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the classifier that the factory should publish under.
   * @return the value of the property, not empty
   */
  public String getClassifier() {
    return _classifier;
  }

  /**
   * Sets the classifier that the factory should publish under.
   * @param classifier  the new value of the property, not empty
   */
  public void setClassifier(String classifier) {
    JodaBeanUtils.notEmpty(classifier, "classifier");
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
   * Gets the Redis host name.
   * @return the value of the property, not empty
   */
  public String getHostName() {
    return _hostName;
  }

  /**
   * Sets the Redis host name.
   * @param hostName  the new value of the property, not empty
   */
  public void setHostName(String hostName) {
    JodaBeanUtils.notEmpty(hostName, "hostName");
    this._hostName = hostName;
  }

  /**
   * Gets the the {@code hostName} property.
   * @return the property, not null
   */
  public final Property<String> hostName() {
    return metaBean().hostName().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the Redis host port.
   * @return the value of the property
   */
  public int getRedisPort() {
    return _redisPort;
  }

  /**
   * Sets the Redis host port.
   * @param redisPort  the new value of the property
   */
  public void setRedisPort(int redisPort) {
    this._redisPort = redisPort;
  }

  /**
   * Gets the the {@code redisPort} property.
   * @return the property, not null
   */
  public final Property<Integer> redisPort() {
    return metaBean().redisPort().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the Redis password.
   * @return the value of the property
   */
  public String getPassword() {
    return _password;
  }

  /**
   * Sets the Redis password.
   * @param password  the new value of the property
   */
  public void setPassword(String password) {
    this._password = password;
  }

  /**
   * Gets the the {@code password} property.
   * @return the property, not null
   */
  public final Property<String> password() {
    return metaBean().password().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the Redis timeout.
   * @return the value of the property
   */
  public int getTimeOut() {
    return _timeOut;
  }

  /**
   * Sets the Redis timeout.
   * @param timeOut  the new value of the property
   */
  public void setTimeOut(int timeOut) {
    this._timeOut = timeOut;
  }

  /**
   * Gets the the {@code timeOut} property.
   * @return the property, not null
   */
  public final Property<Integer> timeOut() {
    return metaBean().timeOut().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public RedisConnectorComponentFactory clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      RedisConnectorComponentFactory other = (RedisConnectorComponentFactory) obj;
      return JodaBeanUtils.equal(getClassifier(), other.getClassifier()) &&
          JodaBeanUtils.equal(getHostName(), other.getHostName()) &&
          (getRedisPort() == other.getRedisPort()) &&
          JodaBeanUtils.equal(getPassword(), other.getPassword()) &&
          (getTimeOut() == other.getTimeOut()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = hash * 31 + JodaBeanUtils.hashCode(getClassifier());
    hash = hash * 31 + JodaBeanUtils.hashCode(getHostName());
    hash = hash * 31 + JodaBeanUtils.hashCode(getRedisPort());
    hash = hash * 31 + JodaBeanUtils.hashCode(getPassword());
    hash = hash * 31 + JodaBeanUtils.hashCode(getTimeOut());
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(192);
    buf.append("RedisConnectorComponentFactory{");
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
    buf.append("hostName").append('=').append(JodaBeanUtils.toString(getHostName())).append(',').append(' ');
    buf.append("redisPort").append('=').append(JodaBeanUtils.toString(getRedisPort())).append(',').append(' ');
    buf.append("password").append('=').append(JodaBeanUtils.toString(getPassword())).append(',').append(' ');
    buf.append("timeOut").append('=').append(JodaBeanUtils.toString(getTimeOut())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code RedisConnectorComponentFactory}.
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
        this, "classifier", RedisConnectorComponentFactory.class, String.class);
    /**
     * The meta-property for the {@code hostName} property.
     */
    private final MetaProperty<String> _hostName = DirectMetaProperty.ofReadWrite(
        this, "hostName", RedisConnectorComponentFactory.class, String.class);
    /**
     * The meta-property for the {@code redisPort} property.
     */
    private final MetaProperty<Integer> _redisPort = DirectMetaProperty.ofReadWrite(
        this, "redisPort", RedisConnectorComponentFactory.class, Integer.TYPE);
    /**
     * The meta-property for the {@code password} property.
     */
    private final MetaProperty<String> _password = DirectMetaProperty.ofReadWrite(
        this, "password", RedisConnectorComponentFactory.class, String.class);
    /**
     * The meta-property for the {@code timeOut} property.
     */
    private final MetaProperty<Integer> _timeOut = DirectMetaProperty.ofReadWrite(
        this, "timeOut", RedisConnectorComponentFactory.class, Integer.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "classifier",
        "hostName",
        "redisPort",
        "password",
        "timeOut");

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
        case -300756909:  // hostName
          return _hostName;
        case 1709620380:  // redisPort
          return _redisPort;
        case 1216985755:  // password
          return _password;
        case -1313942207:  // timeOut
          return _timeOut;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends RedisConnectorComponentFactory> builder() {
      return new DirectBeanBuilder<RedisConnectorComponentFactory>(new RedisConnectorComponentFactory());
    }

    @Override
    public Class<? extends RedisConnectorComponentFactory> beanType() {
      return RedisConnectorComponentFactory.class;
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
     * The meta-property for the {@code hostName} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> hostName() {
      return _hostName;
    }

    /**
     * The meta-property for the {@code redisPort} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Integer> redisPort() {
      return _redisPort;
    }

    /**
     * The meta-property for the {@code password} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> password() {
      return _password;
    }

    /**
     * The meta-property for the {@code timeOut} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Integer> timeOut() {
      return _timeOut;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          return ((RedisConnectorComponentFactory) bean).getClassifier();
        case -300756909:  // hostName
          return ((RedisConnectorComponentFactory) bean).getHostName();
        case 1709620380:  // redisPort
          return ((RedisConnectorComponentFactory) bean).getRedisPort();
        case 1216985755:  // password
          return ((RedisConnectorComponentFactory) bean).getPassword();
        case -1313942207:  // timeOut
          return ((RedisConnectorComponentFactory) bean).getTimeOut();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          ((RedisConnectorComponentFactory) bean).setClassifier((String) newValue);
          return;
        case -300756909:  // hostName
          ((RedisConnectorComponentFactory) bean).setHostName((String) newValue);
          return;
        case 1709620380:  // redisPort
          ((RedisConnectorComponentFactory) bean).setRedisPort((Integer) newValue);
          return;
        case 1216985755:  // password
          ((RedisConnectorComponentFactory) bean).setPassword((String) newValue);
          return;
        case -1313942207:  // timeOut
          ((RedisConnectorComponentFactory) bean).setTimeOut((Integer) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notEmpty(((RedisConnectorComponentFactory) bean)._classifier, "classifier");
      JodaBeanUtils.notEmpty(((RedisConnectorComponentFactory) bean)._hostName, "hostName");
      super.validate(bean);
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}

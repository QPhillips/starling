/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.component.factory.source;

import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.component.ComponentInfo;
import com.opengamma.component.ComponentRepository;
import com.opengamma.component.factory.ComponentInfoAttributes;
import com.opengamma.core.holiday.HolidaySource;
import com.opengamma.core.holiday.impl.DataHolidaySourceResource;
import com.opengamma.core.holiday.impl.NonVersionedRedisHolidaySource;
import com.opengamma.core.holiday.impl.RemoteHolidaySource;

/**
 *
 */
@BeanDefinition
public class NonVersionedRedisHolidaySourceComponentFactory extends AbstractNonVersionedRedisSourceComponentFactory {

  @Override
  public void init(final ComponentRepository repo, final LinkedHashMap<String, String> configuration) throws Exception {
    final NonVersionedRedisHolidaySource source = new NonVersionedRedisHolidaySource(getRedisConnector().getJedisPool(), getRedisPrefix());

    ComponentInfo info = new ComponentInfo(HolidaySource.class, getClassifier());
    info.addAttribute(ComponentInfoAttributes.LEVEL, 1);
    if (isPublishRest()) {
      info.addAttribute(ComponentInfoAttributes.REMOTE_CLIENT_JAVA, RemoteHolidaySource.class);
    }
    repo.registerComponent(info, source);
    if (isPublishRest()) {
      repo.getRestComponents().publish(info, new DataHolidaySourceResource(source));
    }

    info = new ComponentInfo(NonVersionedRedisHolidaySource.class, getClassifier());
    info.addAttribute(ComponentInfoAttributes.LEVEL, 1);
    repo.registerComponent(info, source);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code NonVersionedRedisHolidaySourceComponentFactory}.
   * @return the meta-bean, not null
   */
  public static NonVersionedRedisHolidaySourceComponentFactory.Meta meta() {
    return NonVersionedRedisHolidaySourceComponentFactory.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(NonVersionedRedisHolidaySourceComponentFactory.Meta.INSTANCE);
  }

  @Override
  public NonVersionedRedisHolidaySourceComponentFactory.Meta metaBean() {
    return NonVersionedRedisHolidaySourceComponentFactory.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  @Override
  public NonVersionedRedisHolidaySourceComponentFactory clone() {
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
    buf.append("NonVersionedRedisHolidaySourceComponentFactory{");
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
   * The meta-bean for {@code NonVersionedRedisHolidaySourceComponentFactory}.
   */
  public static class Meta extends AbstractNonVersionedRedisSourceComponentFactory.Meta {
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
    public BeanBuilder<? extends NonVersionedRedisHolidaySourceComponentFactory> builder() {
      return new DirectBeanBuilder<NonVersionedRedisHolidaySourceComponentFactory>(new NonVersionedRedisHolidaySourceComponentFactory());
    }

    @Override
    public Class<? extends NonVersionedRedisHolidaySourceComponentFactory> beanType() {
      return NonVersionedRedisHolidaySourceComponentFactory.class;
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

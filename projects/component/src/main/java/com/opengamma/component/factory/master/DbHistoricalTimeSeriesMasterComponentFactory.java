/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.component.factory.master;

import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.master.historicaltimeseries.HistoricalTimeSeriesMaster;
import com.opengamma.master.historicaltimeseries.impl.DataHistoricalTimeSeriesMasterResource;
import com.opengamma.master.historicaltimeseries.impl.DataTrackingHistoricalTimeSeriesMaster;
import com.opengamma.master.historicaltimeseries.impl.PermissionedHistoricalTimeSeriesMaster;
import com.opengamma.master.historicaltimeseries.impl.RemoteHistoricalTimeSeriesMaster;
import com.opengamma.master.impl.AbstractRemoteMaster;
import com.opengamma.masterdb.historicaltimeseries.DbHistoricalTimeSeriesMaster;
import com.opengamma.util.metric.OpenGammaMetricRegistry;
import com.opengamma.util.rest.AbstractDataResource;

/**
 * Component factory for the database historical time-series master.
 */
@BeanDefinition
public class DbHistoricalTimeSeriesMasterComponentFactory extends AbstractDocumentDbMasterComponentFactory<HistoricalTimeSeriesMaster, DbHistoricalTimeSeriesMaster> {

  /**
   * Creates an instance.
   */
  public DbHistoricalTimeSeriesMasterComponentFactory() {
    super("hts", HistoricalTimeSeriesMaster.class);
  }

  @Override
  protected Class<? extends AbstractRemoteMaster> getRemoteInterface() {
    return RemoteHistoricalTimeSeriesMaster.class;
  }

  //-------------------------------------------------------------------------
  @Override
  protected DbHistoricalTimeSeriesMaster createDbDocumentMaster() {
    final DbHistoricalTimeSeriesMaster master = new DbHistoricalTimeSeriesMaster(getDbConnector());
    master.registerMetrics(OpenGammaMetricRegistry.getSummaryInstance(), OpenGammaMetricRegistry.getDetailedInstance(), "DbHistoricalTimeSeriesMaster" + getClassifier());
    return master;
  }

  @Override
  protected AbstractDataResource createPublishedResource(final DbHistoricalTimeSeriesMaster dbMaster, final HistoricalTimeSeriesMaster postProcessedMaster) {
    return new DataHistoricalTimeSeriesMasterResource(postProcessedMaster);
  }

  @Override
  protected HistoricalTimeSeriesMaster wrapMasterWithTrackingInterface(final HistoricalTimeSeriesMaster postProcessedMaster) {
    return new DataTrackingHistoricalTimeSeriesMaster(postProcessedMaster);
  }

  @Override
  protected HistoricalTimeSeriesMaster postProcess(final DbHistoricalTimeSeriesMaster master) {
    return PermissionedHistoricalTimeSeriesMaster.wrap(master);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code DbHistoricalTimeSeriesMasterComponentFactory}.
   * @return the meta-bean, not null
   */
  public static DbHistoricalTimeSeriesMasterComponentFactory.Meta meta() {
    return DbHistoricalTimeSeriesMasterComponentFactory.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(DbHistoricalTimeSeriesMasterComponentFactory.Meta.INSTANCE);
  }

  @Override
  public DbHistoricalTimeSeriesMasterComponentFactory.Meta metaBean() {
    return DbHistoricalTimeSeriesMasterComponentFactory.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  @Override
  public DbHistoricalTimeSeriesMasterComponentFactory clone() {
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
    buf.append("DbHistoricalTimeSeriesMasterComponentFactory{");
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
   * The meta-bean for {@code DbHistoricalTimeSeriesMasterComponentFactory}.
   */
  public static class Meta extends AbstractDocumentDbMasterComponentFactory.Meta<HistoricalTimeSeriesMaster, DbHistoricalTimeSeriesMaster> {
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
    public BeanBuilder<? extends DbHistoricalTimeSeriesMasterComponentFactory> builder() {
      return new DirectBeanBuilder<DbHistoricalTimeSeriesMasterComponentFactory>(new DbHistoricalTimeSeriesMasterComponentFactory());
    }

    @Override
    public Class<? extends DbHistoricalTimeSeriesMasterComponentFactory> beanType() {
      return DbHistoricalTimeSeriesMasterComponentFactory.class;
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

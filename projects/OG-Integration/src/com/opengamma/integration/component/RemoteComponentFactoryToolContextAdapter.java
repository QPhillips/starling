/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.integration.component;

import java.util.Arrays;
import java.util.List;

import com.opengamma.financial.convention.DefaultConventionBundleSource;
import com.opengamma.financial.convention.InMemoryConventionBundleMaster;
import com.opengamma.financial.tool.ToolContext;

/**
 * A convenience class to pull the most likely desired masters and sources from a RemoteComponentFactory
 * and populate a ToolContext.  This eases porting of other tools that use the ToolContext.
 */
public class RemoteComponentFactoryToolContextAdapter extends ToolContext {

  private static final List<String> DEFAULT_CLASSIFIER_CHAIN = Arrays.asList(new String[] {"central", "main", "default", "shared", "combined" });
  
  public RemoteComponentFactoryToolContextAdapter(RemoteComponentFactory remoteComponentFactory) {
    this(remoteComponentFactory, DEFAULT_CLASSIFIER_CHAIN);
  }
      
  public RemoteComponentFactoryToolContextAdapter(RemoteComponentFactory remoteComponentFactory, List<String> classifierPreferences) {
    setConfigMaster(remoteComponentFactory.getConfigMaster(classifierPreferences));
    setExchangeMaster(remoteComponentFactory.getExchangeMaster(classifierPreferences));
    setHolidayMaster(remoteComponentFactory.getHolidayMaster(classifierPreferences));
    setRegionMaster(remoteComponentFactory.getRegionMaster(classifierPreferences));
    setSecurityMaster(remoteComponentFactory.getSecurityMaster(classifierPreferences));
    setPositionMaster(remoteComponentFactory.getPositionMaster(classifierPreferences));
    setPortfolioMaster(remoteComponentFactory.getPortfolioMaster(classifierPreferences));
    setHistoricalTimeSeriesMaster(remoteComponentFactory.getHistoricalTimeSeriesMaster(classifierPreferences));
    setMarketDataSnapshotMaster(remoteComponentFactory.getMarketDataSnapshotMaster(classifierPreferences));
    setConfigSource(remoteComponentFactory.getConfigSource(classifierPreferences));
    setExchangeSource(remoteComponentFactory.getExchangeSource(classifierPreferences));
    setHolidaySource(remoteComponentFactory.getHolidaySource(classifierPreferences));
    setRegionSource(remoteComponentFactory.getRegionSource(classifierPreferences));
    setSecuritySource(remoteComponentFactory.getSecuritySource(classifierPreferences));
    setPositionSource(remoteComponentFactory.getPositionSource(classifierPreferences));
    setHistoricalTimeSeriesSource(remoteComponentFactory.getHistoricalTimeSeriesSource(classifierPreferences));
    setMarketDataSnapshotSource(remoteComponentFactory.getMarketDataSnapshotSource(classifierPreferences));
    // this may need customizing per-project
    setConventionBundleSource(new DefaultConventionBundleSource(new InMemoryConventionBundleMaster()));
  }
}

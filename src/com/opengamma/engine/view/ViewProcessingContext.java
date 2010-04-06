/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.view;

import java.util.concurrent.ExecutorService;

import com.opengamma.engine.DefaultComputationTargetResolver;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.function.FunctionRepository;
import com.opengamma.engine.function.FunctionResolver;
import com.opengamma.engine.historicaldata.HistoricalDataProvider;
import com.opengamma.engine.livedata.LiveDataAvailabilityProvider;
import com.opengamma.engine.livedata.LiveDataSnapshotProvider;
import com.opengamma.engine.position.PositionMaster;
import com.opengamma.engine.security.SecurityMaster;
import com.opengamma.engine.view.cache.ViewComputationCacheSource;
import com.opengamma.engine.view.calcnode.JobRequestSender;
import com.opengamma.engine.view.calcnode.ViewProcessorQueryReceiver;
import com.opengamma.util.ArgumentChecker;

/**
 * A collection for everything relating to processing a particular view.
 *
 * @author kirk
 */
public class ViewProcessingContext {
  private final LiveDataAvailabilityProvider _liveDataAvailabilityProvider;
  private final LiveDataSnapshotProvider _liveDataSnapshotProvider;
  private final FunctionRepository _functionRepository;
  private final FunctionResolver _functionResolver;
  private final PositionMaster _positionMaster;
  private final SecurityMaster _securityMaster;
  private final HistoricalDataProvider _historicalDataProvider;
  private final ViewComputationCacheSource _computationCacheSource;
  private final JobRequestSender _computationJobRequestSender;
  private final ViewProcessorQueryReceiver _viewProcessorQueryReceiver;
  private final DefaultComputationTargetResolver _computationTargetResolver;
  private final FunctionCompilationContext _compilationContext;
  private final ExecutorService _executorService;

  public ViewProcessingContext(
      LiveDataAvailabilityProvider liveDataAvailabilityProvider,
      LiveDataSnapshotProvider liveDataSnapshotProvider,
      HistoricalDataProvider historicalDataProvider,
      FunctionRepository functionRepository,
      FunctionResolver functionResolver,
      PositionMaster positionMaster,
      SecurityMaster securityMaster,
      ViewComputationCacheSource computationCacheSource,
      JobRequestSender computationJobRequestSender,
      ViewProcessorQueryReceiver viewProcessorQueryReceiver,
      FunctionCompilationContext compilationContext,
      ExecutorService executorService
      ) {
    ArgumentChecker.checkNotNull(liveDataAvailabilityProvider, "LiveDataAvailabilityProvider");
    ArgumentChecker.checkNotNull(liveDataSnapshotProvider, "LiveDataSnapshotProvier");
    ArgumentChecker.checkNotNull(historicalDataProvider, "HistoricalDataProvider");
    ArgumentChecker.checkNotNull(functionRepository, "FunctionRepository");
    ArgumentChecker.checkNotNull(functionResolver, "FunctionResolver");
    ArgumentChecker.checkNotNull(positionMaster, "PositionMaster");
    ArgumentChecker.checkNotNull(securityMaster, "SecurityMaster");
    ArgumentChecker.checkNotNull(computationCacheSource, "ComputationCacheSource");
    ArgumentChecker.checkNotNull(computationJobRequestSender, "ComputationJobRequestSender");
    ArgumentChecker.checkNotNull(viewProcessorQueryReceiver, "ViewProcessorQueryReceiver");
    ArgumentChecker.checkNotNull(compilationContext, "CompilationContext");
    ArgumentChecker.checkNotNull(executorService, "ExecutorService");
    
    _liveDataAvailabilityProvider = liveDataAvailabilityProvider;
    _liveDataSnapshotProvider = liveDataSnapshotProvider;
    _historicalDataProvider = historicalDataProvider;
    _functionRepository = functionRepository;
    _functionResolver = functionResolver;
    _positionMaster = positionMaster;
    _securityMaster = securityMaster;
    _computationCacheSource = computationCacheSource;
    _computationJobRequestSender = computationJobRequestSender;
    _viewProcessorQueryReceiver = viewProcessorQueryReceiver;
    _compilationContext = compilationContext;
    _executorService = executorService;
    
    _computationTargetResolver = new DefaultComputationTargetResolver(securityMaster, positionMaster);
  }

  /**
   * @return the liveDataAvailabilityProvider
   */
  public LiveDataAvailabilityProvider getLiveDataAvailabilityProvider() {
    return _liveDataAvailabilityProvider;
  }

  /**
   * @return the liveDataSnapshotProvider
   */
  public LiveDataSnapshotProvider getLiveDataSnapshotProvider() {
    return _liveDataSnapshotProvider;
  }
  
  /**
   * @return the historical data provider
   */
  public HistoricalDataProvider getHistoricalDataProvider() {
    return _historicalDataProvider;
  }

  /**
   * @return the analyticFunctionRepository
   */
  public FunctionRepository getFunctionRepository() {
    return _functionRepository;
  }
  
  /**
   * @return the functionResolver
   */
  public FunctionResolver getFunctionResolver() {
    return _functionResolver;
  }

  /**
   * @return the positionMaster
   */
  public PositionMaster getPositionMaster() {
    return _positionMaster;
  }

  /**
   * @return the securityMaster
   */
  public SecurityMaster getSecurityMaster() {
    return _securityMaster;
  }

  /**
   * @return the computationCacheSource
   */
  public ViewComputationCacheSource getComputationCacheSource() {
    return _computationCacheSource;
  }

  /**
   * @return the computationJobRequestSender
   */
  public JobRequestSender getComputationJobRequestSender() {
    return _computationJobRequestSender;
  }
  
  /**
   * @return the viewProcessorQueryReceiver
   */
  public ViewProcessorQueryReceiver getViewProcessorQueryReceiver() {
    return _viewProcessorQueryReceiver;
  }

  /**
   * @return the computationTargetResolver
   */
  public DefaultComputationTargetResolver getComputationTargetResolver() {
    return _computationTargetResolver;
  }

  /**
   * @return the compilationContext
   */
  public FunctionCompilationContext getCompilationContext() {
    return _compilationContext;
  }

  /**
   * @return the executorService
   */
  public ExecutorService getExecutorService() {
    return _executorService;
  }

}

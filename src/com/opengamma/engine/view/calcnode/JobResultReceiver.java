/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.view.calcnode;

/**
 * An call-back interface for results of jobs sent using {@link JobRequestSender}.
 *
 * @author kirk
 */
public interface JobResultReceiver {
  
  void resultReceived(CalculationJobResult result);

}

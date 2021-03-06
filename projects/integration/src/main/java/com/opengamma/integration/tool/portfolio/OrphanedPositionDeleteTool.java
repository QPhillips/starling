/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.integration.tool.portfolio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.component.tool.AbstractTool;
import com.opengamma.financial.tool.ToolContext;
import com.opengamma.scripts.Scriptable;

/**
 * Tool to delete positions that are not currently in a portfolio.
 */
@Scriptable
public class OrphanedPositionDeleteTool extends AbstractTool<ToolContext> {

  /** Logger */
  private static final Logger LOGGER = LoggerFactory.getLogger(OrphanedPositionDeleteTool.class);

  // -------------------------------------------------------------------------
  /**
   * Main method to run the tool.
   *
   * @param args
   *          the standard tool arguments, not null
   */
  public static void main(final String[] args) { // CSIGNORE
    new OrphanedPositionDeleteTool().invokeAndTerminate(args);
  }

  // -------------------------------------------------------------------------
  @Override
  protected void doRun() throws Exception {
    final ToolContext toolContext = getToolContext();
    final OrphanedPositionRemover orphanedPositionRemover = new OrphanedPositionRemover(toolContext.getPortfolioMaster(), toolContext.getPositionMaster());
    LOGGER.info("running orphanedPositionRemover");
    orphanedPositionRemover.run();
    toolContext.close();
  }

}

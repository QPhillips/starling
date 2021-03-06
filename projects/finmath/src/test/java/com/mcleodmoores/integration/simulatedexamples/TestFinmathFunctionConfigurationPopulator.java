/**
 * Copyright (C) 2014-Present McLeod Moores Software Limited.  All rights reserved.
 */
package com.mcleodmoores.integration.simulatedexamples;

import java.util.Collections;

import com.google.common.collect.ImmutableList;
import com.mcleodmoores.integration.function.FinmathFunctionConfiguration;
import com.mcleodmoores.integration.function.FinmathFunctions;
import com.opengamma.component.tool.AbstractTool;
import com.opengamma.core.config.impl.ConfigItem;
import com.opengamma.engine.function.config.FunctionConfigurationDefinition;
import com.opengamma.engine.function.config.FunctionConfigurationSource;
import com.opengamma.engine.function.config.ParameterizedFunctionConfiguration;
import com.opengamma.engine.function.config.StaticFunctionConfiguration;
import com.opengamma.financial.aggregation.AggregationFunctions;
import com.opengamma.financial.currency.CurrencyFunctions;
import com.opengamma.financial.property.PropertyFunctions;
import com.opengamma.financial.target.TargetFunctions;
import com.opengamma.financial.tool.ToolContext;
import com.opengamma.financial.value.ValueFunctions;
import com.opengamma.financial.view.ViewFunctions;
import com.opengamma.master.config.ConfigMasterUtils;
import com.opengamma.scripts.Scriptable;

@Scriptable
public class TestFinmathFunctionConfigurationPopulator extends AbstractTool<ToolContext> {
  private static final String STANDARD = "STANDARD_FUNCTIONS";
  private static final String VIEW = "VIEW_FUNCTIONS";
  private static final String VALUE = "VALUE_FUNCTIONS";
  private static final String PROPERTY = "PROPERTY_FUNCTIONS";
  private static final String CURRENCY = "CURRENCY_FUNCTIONS";
  private static final String ANALYTICS = "ANALYTICS_FUNCTIONS";
  private static final String AGGREGATION = "AGGREGATION_FUNCTIONS";
  private static final String FINANCIAL = "FINANCIAL_FUNCTIONS";
  private static final String TEST = "TEST_FUNCTIONS";
  private static final String TARGET = "TARGET_FUNCTIONS";

  //-------------------------------------------------------------------------
  /**
   * Main method to run the tool.
   *
   * @param args  the standard tool arguments, not null
   */
  public static void main(final String[] args) { // CSIGNORE
    new TestFinmathFunctionConfigurationPopulator().invokeAndTerminate(args);
  }

  //-------------------------------------------------------------------------
  @Override
  protected void doRun() {
    storeFunctionDefinition(AGGREGATION, AggregationFunctions.instance());
    storeFunctionDefinition(ANALYTICS, FinmathFunctions.instance());
    storeFunctionDefinition(CURRENCY, CurrencyFunctions.instance());
    storeFunctionDefinition(PROPERTY, PropertyFunctions.instance());
    storeFunctionDefinition(VALUE, ValueFunctions.instance());
    storeFunctionDefinition(VIEW, ViewFunctions.instance());
    storeFunctionDefinition(TARGET, TargetFunctions.instance());

    final FunctionConfigurationDefinition financialFunc = new FunctionConfigurationDefinition(FINANCIAL,
        ImmutableList.of(AGGREGATION, ANALYTICS, CURRENCY, PROPERTY, TARGET, VALUE, VIEW),
        Collections.<StaticFunctionConfiguration>emptyList(),
        Collections.<ParameterizedFunctionConfiguration>emptyList());
    storeFunctionDefinition(financialFunc);

    storeFunctionDefinition(STANDARD, FinmathFunctionConfiguration.instance());

    final FunctionConfigurationDefinition functions = new FunctionConfigurationDefinition(TEST,
        ImmutableList.of(FINANCIAL, STANDARD),
        Collections.<StaticFunctionConfiguration>emptyList(),
        Collections.<ParameterizedFunctionConfiguration>emptyList());
    storeFunctionDefinition(functions);

  }

  private void storeFunctionDefinition(final FunctionConfigurationDefinition definition) {
    final ConfigItem<FunctionConfigurationDefinition> config = ConfigItem.of(definition, definition.getName(), FunctionConfigurationDefinition.class);
    ConfigMasterUtils.storeByName(getToolContext().getConfigMaster(), config);
  }

  private void storeFunctionDefinition(final String name, final FunctionConfigurationSource funcConfigSource) {
    final FunctionConfigurationDefinition definition = FunctionConfigurationDefinition.of(name, funcConfigSource);
    final ConfigItem<FunctionConfigurationDefinition> config = ConfigItem.of(definition, name, FunctionConfigurationDefinition.class);
    ConfigMasterUtils.storeByName(getToolContext().getConfigMaster(), config);
  }

}

/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.currency;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.core.historicaltimeseries.HistoricalTimeSeries;
import com.opengamma.core.value.MarketDataRequirementNames;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.function.FunctionExecutionContext;
import com.opengamma.engine.function.FunctionInputs;
import com.opengamma.engine.marketdata.ExternalIdBundleResolver;
import com.opengamma.engine.value.ValueProperties;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueRequirementNames;
import com.opengamma.financial.OpenGammaCompilationContext;
import com.opengamma.financial.analytics.timeseries.DateConstraint;
import com.opengamma.financial.analytics.timeseries.HistoricalTimeSeriesFunctionUtils;
import com.opengamma.financial.currency.CurrencyMatrixValue.CurrencyMatrixCross;
import com.opengamma.financial.currency.CurrencyMatrixValue.CurrencyMatrixFixed;
import com.opengamma.financial.currency.CurrencyMatrixValue.CurrencyMatrixValueRequirement;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.master.historicaltimeseries.HistoricalTimeSeriesResolutionResult;
import com.opengamma.master.historicaltimeseries.HistoricalTimeSeriesResolver;
import com.opengamma.timeseries.DoubleTimeSeries;
import com.opengamma.util.money.Currency;
import com.opengamma.util.tuple.Pair;
import com.opengamma.util.tuple.Pairs;

/**
 * Injects a time series implied from a value from a {@link CurrencyMatrix} into a dependency graph to satisfy the currency requirements generated by {@link CurrencySeriesConversionFunction}.
 */
public class CurrencyMatrixSeriesSourcingFunction extends AbstractCurrencyMatrixSourcingFunction {

  private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyMatrixSeriesSourcingFunction.class);

  // PLAT-2813 Don't need this if we can request HTS requirements directly
  private HistoricalTimeSeriesResolver _htsResolver;

  public CurrencyMatrixSeriesSourcingFunction() {
    this(ValueRequirementNames.HISTORICAL_FX_TIME_SERIES);
  }

  protected CurrencyMatrixSeriesSourcingFunction(final String valueRequirementName) {
    super(valueRequirementName);
  }

  protected void setHistoricalTimeSeriesResolver(final HistoricalTimeSeriesResolver htsResolver) {
    _htsResolver = htsResolver;
  }

  protected HistoricalTimeSeriesResolver getHistoricalTimeSeriesResolver() {
    return _htsResolver;
  }

  @Override
  public void init(final FunctionCompilationContext context) {
    super.init(context);
    // PLAT-2813 Don't need this if we can request HTS requirements directly
    setHistoricalTimeSeriesResolver(OpenGammaCompilationContext.getHistoricalTimeSeriesResolver(context));
  }

  @Override
  protected ValueProperties.Builder createValueProperties() {
    final ValueProperties.Builder properties = super.createValueProperties();
    properties.withAny(HistoricalTimeSeriesFunctionUtils.START_DATE_PROPERTY)
        .with(HistoricalTimeSeriesFunctionUtils.INCLUDE_START_PROPERTY, HistoricalTimeSeriesFunctionUtils.YES_VALUE, HistoricalTimeSeriesFunctionUtils.NO_VALUE)
        .withAny(HistoricalTimeSeriesFunctionUtils.END_DATE_PROPERTY)
        .with(HistoricalTimeSeriesFunctionUtils.INCLUDE_END_PROPERTY, HistoricalTimeSeriesFunctionUtils.YES_VALUE, HistoricalTimeSeriesFunctionUtils.NO_VALUE);
    return properties;
  }

  private ValueRequirement getRequirement(final ExternalIdBundleResolver resolver, final CurrencyMatrixValueRequirement valueRequirement, final ValueProperties constraints) {
    final ValueRequirement requirement = valueRequirement.getValueRequirement();
    // TODO: PLAT-2813 Don't perform the resolution here; request the time series directly
    final ExternalIdBundle targetIdentifiers = resolver.getExternalIdBundle(requirement.getTargetReference());
    if (targetIdentifiers == null) {
      return null;
    }
    final HistoricalTimeSeriesResolutionResult timeSeries = getHistoricalTimeSeriesResolver().resolve(targetIdentifiers, null, null, null, MarketDataRequirementNames.MARKET_VALUE, null);
    if (timeSeries == null) {
      return null;
    }
    // TODO: Requesting the whole time series isn't ideal but we don't know which points will be needed. Could the time series somehow be a lazy-fetch?
    // Is this really a problem - caching the whole time series at a calc node may be better than requesting different subsets each time?
    return HistoricalTimeSeriesFunctionUtils.createHTSRequirement(timeSeries, MarketDataRequirementNames.MARKET_VALUE, constraints);
  }

  private boolean getRequirements(final CurrencyMatrix matrix, final ExternalIdBundleResolver resolver, final Set<ValueRequirement> requirements, final Set<Pair<Currency, Currency>> visited,
      final Pair<Currency, Currency> currencies, final ValueProperties constraints) {
    if (!visited.add(currencies)) {
      // Gone round in a loop if we've already seen this pair
      throw new IllegalStateException();
    }
    final CurrencyMatrixValue value = matrix.getConversion(currencies.getFirst(), currencies.getSecond());
    if (value != null) {
      return value.accept(new CurrencyMatrixValueVisitor<Boolean>() {

        @Override
        public Boolean visitCross(final CurrencyMatrixCross cross) {
          return getRequirements(matrix, resolver, requirements, visited, Pairs.of(currencies.getFirst(), cross.getCrossCurrency()), constraints)
              && getRequirements(matrix, resolver, requirements, visited, Pairs.of(cross.getCrossCurrency(), currencies.getSecond()), constraints);
        }

        @Override
        public Boolean visitFixed(final CurrencyMatrixFixed fixedValue) {
          // Literal value - nothing required
          return Boolean.TRUE;
        }

        @Override
        public Boolean visitValueRequirement(final CurrencyMatrixValueRequirement valueRequirement) {
          final ValueRequirement requirement = getRequirement(resolver, valueRequirement, constraints);
          if (requirement == null) {
            return Boolean.FALSE;
          }
          requirements.add(tagInput(requirement, currencies.getFirst(), currencies.getSecond()));
          return Boolean.TRUE;
        }

      });
    } else {
      return false;
    }
  }

  protected ValueProperties getRequirementConstraints(final ValueRequirement desiredValue) {
    final ValueProperties desiredConstraints = desiredValue.getConstraints();
    final ValueProperties.Builder requiredConstraints = ValueProperties.builder();
    Set<String> values = desiredConstraints.getValues(HistoricalTimeSeriesFunctionUtils.START_DATE_PROPERTY);
    if (values == null || values.isEmpty()) {
      requiredConstraints.with(HistoricalTimeSeriesFunctionUtils.START_DATE_PROPERTY, DateConstraint.NULL.toString());
    } else {
      requiredConstraints.with(HistoricalTimeSeriesFunctionUtils.START_DATE_PROPERTY, values);
    }
    values = desiredConstraints.getValues(HistoricalTimeSeriesFunctionUtils.INCLUDE_START_PROPERTY);
    if (values == null || values.isEmpty()) {
      requiredConstraints.with(HistoricalTimeSeriesFunctionUtils.INCLUDE_START_PROPERTY, HistoricalTimeSeriesFunctionUtils.YES_VALUE);
    } else {
      requiredConstraints.with(HistoricalTimeSeriesFunctionUtils.INCLUDE_START_PROPERTY, values);
    }
    values = desiredConstraints.getValues(HistoricalTimeSeriesFunctionUtils.END_DATE_PROPERTY);
    if (values == null || values.isEmpty()) {
      requiredConstraints.with(HistoricalTimeSeriesFunctionUtils.END_DATE_PROPERTY, DateConstraint.VALUATION_TIME.toString());
    } else {
      requiredConstraints.with(HistoricalTimeSeriesFunctionUtils.END_DATE_PROPERTY, values);
    }
    values = desiredConstraints.getValues(HistoricalTimeSeriesFunctionUtils.INCLUDE_END_PROPERTY);
    if (values == null || values.isEmpty()) {
      requiredConstraints.with(HistoricalTimeSeriesFunctionUtils.INCLUDE_END_PROPERTY, HistoricalTimeSeriesFunctionUtils.YES_VALUE);
    } else {
      requiredConstraints.with(HistoricalTimeSeriesFunctionUtils.INCLUDE_END_PROPERTY, values);
    }
    return requiredConstraints.get();
  }

  @Override
  protected boolean getRequirements(final FunctionCompilationContext context, final ValueRequirement desiredValue, final CurrencyMatrix matrix, final Set<ValueRequirement> requirements, final Currency source, final Currency target) {
    return getRequirements(matrix, new ExternalIdBundleResolver(context.getComputationTargetResolver()), requirements, new HashSet<Pair<Currency, Currency>>(), Pairs.of(source, target),
        getRequirementConstraints(desiredValue));
  }

  private Object getRate(final CurrencyMatrix matrix, final ExternalIdBundleResolver resolver, final FunctionInputs inputs, final Currency source, final Currency target,
      final ValueProperties htsConstraints) {
    final CurrencyMatrixValue value = matrix.getConversion(source, target);
    final Object rate = value.accept(new CurrencyMatrixValueVisitor<Object>() {

      @Override
      public Object visitCross(final CurrencyMatrixCross cross) {
        final Object r1 = getRate(matrix, resolver, inputs, source, cross.getCrossCurrency(), htsConstraints);
        final Object r2 = getRate(matrix, resolver, inputs, cross.getCrossCurrency(), target, htsConstraints);
        return createCrossRate(r1, r2);
      }

      @Override
      public Object visitFixed(final CurrencyMatrixFixed fixedValue) {
        return fixedValue.getFixedValue();
      }

      @Override
      public Object visitValueRequirement(final CurrencyMatrixValueRequirement valueRequirement) {
        final Object marketValue = inputs.getValue(getRequirement(resolver, valueRequirement, htsConstraints));
        if (marketValue instanceof DoubleTimeSeries) {
          //TODO is this branch ever reached?
          DoubleTimeSeries<?> fxRate = (DoubleTimeSeries<?>) marketValue;
          if (valueRequirement.isReciprocal()) {
            fxRate = fxRate.reciprocal();
          }
          return fxRate;
        } else if (marketValue instanceof HistoricalTimeSeries) {
          DoubleTimeSeries<?> fxRate = ((HistoricalTimeSeries) marketValue).getTimeSeries();
          if (valueRequirement.isReciprocal()) {
            fxRate = fxRate.reciprocal();
          }
          return fxRate;
        } else {
          if (marketValue == null) {
            // Missing input case; reported elsewhere
            return null;
          }
          throw new IllegalArgumentException("Expected a time series for " + valueRequirement.toString() + ", got " + marketValue.getClass());
        }
      }

    });
    LOGGER.debug("{} to {} = {}", new Object[] {source, target, rate });
    return rate;
  }

  @Override
  protected Object getRate(final CurrencyMatrix matrix, final ValueRequirement desiredValue, final FunctionExecutionContext executionContext, final FunctionInputs inputs, final Currency source, final Currency target) {
    return getRate(matrix, new ExternalIdBundleResolver(executionContext.getComputationTargetResolver()), inputs, source, target, getRequirementConstraints(desiredValue));
  }

  public static ValueRequirement getConversionRequirement(final CurrencyPair currencies) {
    return getConversionRequirement(currencies, DateConstraint.NULL, true, DateConstraint.VALUATION_TIME, true);
  }

  public static ValueRequirement getConversionRequirement(final Currency source, final Currency target) {
    return getConversionRequirement(CurrencyPair.of(target, source));
  }

  public static ValueRequirement getConversionRequirement(final String source, final String target) {
    return getConversionRequirement(Currency.of(source), Currency.of(target));
  }

  public static ValueRequirement getConversionRequirement(final CurrencyPair currencies, final DateConstraint startDate, final boolean includeStart, final DateConstraint endDate,
      final boolean includeEnd) {
    return new ValueRequirement(ValueRequirementNames.HISTORICAL_FX_TIME_SERIES, CurrencyPair.TYPE.specification(currencies), HistoricalTimeSeriesFunctionUtils.htsConstraints(
        ValueProperties.builder(), startDate, includeStart, endDate, includeEnd).get());
  }

  public static ValueRequirement getConversionRequirement(final Currency source, final Currency target, final DateConstraint startDate, final boolean includeStart, final DateConstraint endDate,
      final boolean includeEnd) {
    return getConversionRequirement(CurrencyPair.of(target, source));
  }

  public static ValueRequirement getConversionRequirement(final String source, final String target, final DateConstraint startDate, final boolean includeStart, final DateConstraint endDate,
      final boolean includeEnd) {
    return getConversionRequirement(Currency.of(source), Currency.of(target));
  }

}

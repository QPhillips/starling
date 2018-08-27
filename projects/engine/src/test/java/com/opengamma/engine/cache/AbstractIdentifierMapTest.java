/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.cache;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.Lifecycle;
import org.testng.annotations.Test;

import com.opengamma.engine.ComputationTargetSpecification;
import com.opengamma.engine.value.ValueProperties;
import com.opengamma.engine.value.ValuePropertyNames;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.id.UniqueId;
import com.opengamma.util.monitor.OperationTimer;
import com.opengamma.util.test.TestGroup;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

/**
 * A generic suite of tests for any implementation of {@link IdentifierMap}.
 */
@Test(groups = TestGroup.INTEGRATION)
public abstract class AbstractIdentifierMapTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractIdentifierMapTest.class);

  protected abstract IdentifierMap createIdentifierMap(String testName);

  protected ValueSpecification getValueSpec(final String valueName) {
    final ValueSpecification valueSpec = new ValueSpecification("Value", ComputationTargetSpecification.of(UniqueId.of("scheme", valueName)),
        ValueProperties.with(ValuePropertyNames.FUNCTION, "mockFunctionId").get());
    return valueSpec;
  }

  protected void stopIdentifierMap(final IdentifierMap idMap) {
    if (idMap instanceof Lifecycle) {
      ((Lifecycle) idMap).stop();
    }
  }

  @Test
  public void simpleOperation() throws IOException {
    final IdentifierMap idMap = createIdentifierMap("simpleOperation");

    final Map<String, Long> identifiers = new HashMap<>();
    final LongSet seenIdentifiers = new LongOpenHashSet();
    for (int i = 0; i < 10; i++) {
      final String valueName = "value-" + i;
      final ValueSpecification valueSpec = getValueSpec(valueName);
      final long identifier = idMap.getIdentifier(valueSpec);
      assertFalse(seenIdentifiers.contains(identifier));
      seenIdentifiers.add(identifier);
      identifiers.put(valueName, identifier);
    }

    for (int j = 0; j < 5; j++) {
      final Long2ObjectMap<ValueSpecification> valueSpecs = idMap.getValueSpecifications(seenIdentifiers);
      assertEquals(seenIdentifiers.size(), valueSpecs.size());
      for (int i = 0; i < 10; i++) {
        final String valueName = "value-" + i;
        final ValueSpecification valueSpec = getValueSpec(valueName);
        final long identifier = idMap.getIdentifier(valueSpec);
        final long existingIdentifier = identifiers.get(valueName);
        assertEquals(identifier, existingIdentifier);
        assertEquals(valueSpec, idMap.getValueSpecification(identifier));
        assertEquals(valueSpec, valueSpecs.get(identifier));
      }
    }

    stopIdentifierMap(idMap);
  }

  /**
   * @param numRequirementNames
   * @param numIdentifiers
   * @param idSource
   */
  private void singleOperationGetIdentifier(final int numRequirementNames, final int numIdentifiers, final IdentifierMap idSource) {
    for (int iRequirementName = 0; iRequirementName < numRequirementNames; iRequirementName++) {
      final String requirementName = "req-" + iRequirementName;

      for (int iIdentifier = 0; iIdentifier < numIdentifiers; iIdentifier++) {
        final String identifierName = "identifier-" + iIdentifier;
        final ValueSpecification valueSpec = new ValueSpecification(requirementName, ComputationTargetSpecification.of(UniqueId.of("scheme", identifierName)),
            ValueProperties.with(ValuePropertyNames.FUNCTION, "mockFunctionId").get());
        // Just throw away the actual identifier. We don't care.
        idSource.getIdentifier(valueSpec);
      }
    }
  }

  /**
   * @param numRequirementNames
   * @param numIdentifiers
   * @param idSource
   */
  private void bulkOperationGetIdentifier(final int numRequirementNames, final int numIdentifiers, final IdentifierMap idSource) {
    for (int iRequirementName = 0; iRequirementName < numRequirementNames; iRequirementName++) {
      final Collection<ValueSpecification> valueSpecs = new ArrayList<>(numIdentifiers);
      final String requirementName = "req-" + iRequirementName;
      for (int iIdentifier = 0; iIdentifier < numIdentifiers; iIdentifier++) {
        final String identifierName = "identifier-" + iIdentifier;
        valueSpecs.add(new ValueSpecification(requirementName, ComputationTargetSpecification.of(UniqueId.of("scheme", identifierName)),
            ValueProperties.with(ValuePropertyNames.FUNCTION, "mockFunctionId").get()));
      }
      idSource.getIdentifiers(valueSpecs);
    }
  }

  protected void putPerformanceTestImpl(final boolean bulkOperation) {
    final int numRequirementNames = 100;
    final int numIdentifiers = 100;
    final long numSpecifications = (long) numRequirementNames * (long) numIdentifiers;
    final IdentifierMap idMap = createIdentifierMap("putPerformanceTestImpl-" + bulkOperation);

    final OperationTimer timer = new OperationTimer(LOGGER, "Put performance test with {} elements", numSpecifications);

    if (bulkOperation) {
      bulkOperationGetIdentifier(numRequirementNames, numIdentifiers, idMap);
    } else {
      singleOperationGetIdentifier(numRequirementNames, numIdentifiers, idMap);
    }

    stopIdentifierMap(idMap);
    final long numMillis = timer.finished();

    final double msPerPut = (double) numMillis / (double) numSpecifications;
    final double putsPerSecond = 1000.0 / msPerPut;

    LOGGER.warn("put {}-{} ({}) Split time was {} ms/put, {} puts/sec", new Object[] {numRequirementNames, numIdentifiers, bulkOperation, msPerPut, putsPerSecond });
  }

  protected void getPerformanceTestImpl(final boolean bulkOperation) {
    final IdentifierMap idMap = createIdentifierMap("getPerformanceTestImpl-" + bulkOperation);
    final int numRequirementNames = 100;
    final int numIdentifiers = 100;
    final long numSpecifications = (long) numRequirementNames * (long) numIdentifiers;
    try {
      if (bulkOperation) {
        bulkOperationGetIdentifier(numRequirementNames, numIdentifiers, idMap);
      } else {
        singleOperationGetIdentifier(numRequirementNames, numIdentifiers, idMap);
      }
      final OperationTimer timer = new OperationTimer(LOGGER, "Get performance test with {} elements", numSpecifications);
      if (bulkOperation) {
        bulkOperationGetIdentifier(numRequirementNames, numIdentifiers, idMap);
      } else {
        singleOperationGetIdentifier(numRequirementNames, numIdentifiers, idMap);
      }
      final long numMillis = timer.finished();
      final double msPerPut = (double) numMillis / (double) numSpecifications;
      final double putsPerSecond = 1000.0 / msPerPut;
      LOGGER.warn("get {}-{} ({}) Split time was {} ms/get, {} gets/sec", new Object[] {numRequirementNames, numIdentifiers, bulkOperation, msPerPut, putsPerSecond });
    } finally {
      stopIdentifierMap(idMap);
    }
  }

  @Test
  public void putPerformanceTest() {
    putPerformanceTestImpl(false);
  }

  @Test
  public void bulkPutPerformanceTest() {
    putPerformanceTestImpl(true);
  }

  @Test
  public void getPerformanceTest() {
    getPerformanceTestImpl(false);
  }

  @Test
  public void bulkGetPerformanceTest() {
    getPerformanceTestImpl(true);
  }

}

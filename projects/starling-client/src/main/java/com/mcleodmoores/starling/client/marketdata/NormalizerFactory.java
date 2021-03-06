/**
 * Copyright (C) 2015 - present McLeod Moores Software Limited.  All rights reserved.
 */
package com.mcleodmoores.starling.client.marketdata;

import java.lang.reflect.Constructor;
import java.util.Set;

import org.fudgemsg.AnnotationReflector;
import org.joda.convert.FromString;
import org.reflections.Configuration;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.util.AbstractNamedInstanceFactory;

/**
 * Factory for creating and registering named instances of Normalizer.
 * This pattern is used because it saves memory and more importantly, UI tools can query available values.
 */
public class NormalizerFactory extends AbstractNamedInstanceFactory<Normalizer> {
  /** The logger */
  private static final Logger LOGGER = LoggerFactory.getLogger(NormalizerFactory.class);
  /**
   * Singleton instance.
   */
  public static final NormalizerFactory INSTANCE = new NormalizerFactory();

  /**
   * Protected no-arg constructor.
   */
  protected NormalizerFactory() {
    super(Normalizer.class);
    final Configuration config = new ConfigurationBuilder()
        .setUrls(ClasspathHelper.forManifest(ClasspathHelper.forJavaClassPath()))
        .setScanners(new TypeAnnotationsScanner(), new SubTypesScanner(false))
        .filterInputsBy(FilterBuilder.parse(AnnotationReflector.DEFAULT_ANNOTATION_REFLECTOR_FILTER))
        .useParallelExecutor();
    final AnnotationReflector reflector = new AnnotationReflector(config);
    final Set<Class<?>> normalizerClasses = reflector.getReflector().getTypesAnnotatedWith(MarketData.class);
    for (final Class<?> normalizerClass : normalizerClasses) {
      if (normalizerClass.getDeclaredAnnotation(MarketData.class).type().equalsIgnoreCase("Normalization")) {
        try {
          final Constructor<?> constructor = normalizerClass.getDeclaredConstructor();
          constructor.setAccessible(true);
          final Normalizer normalizer = (Normalizer) constructor.newInstance();
          addInstance(normalizer, "");
        } catch (final Exception e) {
          LOGGER.warn("Could not add normalizer: {}", e.getMessage());
        }
      }
    }
  }

  /**
   * Return the named instance of a Normalizer given a name, and create one if one isn't available.
   * @param name  the name of the Normalizer
   * @return the instance of the normalizer corresponding to the name
   */
  @FromString
  public Normalizer of(final String name) {
    return INSTANCE.instance(name);
  }
}

/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.util.annotation;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

import java.lang.annotation.Annotation;
import java.util.Collection;

import org.testng.annotations.Test;
import org.threeten.bp.Instant;

import com.opengamma.util.test.TestGroup;

/**
 * Tests the {@link ClasspathScanner} class.
 */
@Test(groups = TestGroup.UNIT_SLOW)
public class ClasspathScannerTest {

  private final ClasspathScanner _scanner = new ClasspathScanner();

  /**
   *
   */
  public void testTimestamp() {
    final ClasspathScanner scanner = new ClasspathScanner();
    final Instant instant = scanner.getTimestamp();
    assertNotNull(instant);
    assertTrue(instant.isAfter(Instant.EPOCH));
    assertFalse(Instant.now().isBefore(instant));
  }

  /**
   *
   */
  public void testScanType() {
    assertAnnotation(MockType.class);
  }

  /**
   *
   */
  public void testScanField() {
    assertAnnotation(MockField.class);
  }

  /**
   *
   */
  public void testScanConstructor() {
    assertAnnotation(MockConstructor.class);
  }

  /**
   *
   */
  public void testScanMethod() {
    assertAnnotation(MockMethod.class);
  }

  /**
   *
   */
  public void testScanParameter() {
    assertAnnotation(MockParameter.class);
  }

  private void assertAnnotation(final Class<? extends Annotation> annotationClass) {
    final AnnotationCache cache = _scanner.scan(annotationClass);
    assertNotNull(cache);
    assertTrue(cache.getTimestamp().isAfter(Instant.EPOCH));
    final Collection<Class<?>> classes = cache.getClasses();
    assertNotNull(classes);
    assertEquals(1, classes.size());
    assertEquals(MockAnnotation.class, classes.iterator().next());
  }

}

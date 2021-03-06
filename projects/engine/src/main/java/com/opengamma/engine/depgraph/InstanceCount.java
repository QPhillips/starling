/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.depgraph;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.opengamma.OpenGammaRuntimeException;

/**
 * Utility class for profiling the number of active objects. For development/debug use only.
 */
/* package */final class InstanceCount {

  private static final ConcurrentMap<Class<?>, AtomicInteger> INSTANCE_COUNT = new ConcurrentHashMap<>();

  private final AtomicInteger _count;

  static {
    new Thread(new Runnable() {
      @Override
      public void run() {
        do {
          try {
            Thread.sleep(10000);
          } catch (final InterruptedException e) {
            throw new OpenGammaRuntimeException("interrupted", e);
          }
          for (final Map.Entry<Class<?>, AtomicInteger> instance : INSTANCE_COUNT.entrySet()) {
            System.out.println(instance.getKey() + "\t" + instance.getValue());
          }
        } while (true);
      }
    }).start();
  }

  InstanceCount(final Object owner) {
    AtomicInteger count = INSTANCE_COUNT.get(owner.getClass());
    if (count == null) {
      count = new AtomicInteger(1);
      final AtomicInteger existing = INSTANCE_COUNT.putIfAbsent(owner.getClass(), count);
      if (existing != null) {
        existing.incrementAndGet();
        count = existing;
      }
    } else {
      count.incrementAndGet();
    }
    _count = count;
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    _count.decrementAndGet();
  }

}

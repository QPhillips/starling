/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.timeseries.precise.instant;

import static org.testng.AssertJUnit.assertEquals;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;
import org.threeten.bp.Instant;

import com.opengamma.timeseries.DoubleTimeSeries;
import com.opengamma.timeseries.precise.PreciseDoubleTimeSeries;

/**
 * Test.
 */
@Test(groups = "unit")
public class ImmutableInstantDoubleTimeSeriesTest extends InstantDoubleTimeSeriesTest {

  @Override
  protected InstantDoubleTimeSeries createEmptyTimeSeries() {
    return ImmutableInstantDoubleTimeSeries.EMPTY_SERIES;
  }

  @Override
  protected InstantDoubleTimeSeries createStandardTimeSeries() {
    return (InstantDoubleTimeSeries) super.createStandardTimeSeries();
  }

  @Override
  protected InstantDoubleTimeSeries createStandardTimeSeries2() {
    return (InstantDoubleTimeSeries) super.createStandardTimeSeries2();
  }

  @Override
  protected InstantDoubleTimeSeries createTimeSeries(final Instant[] times, final double[] values) {
    return ImmutableInstantDoubleTimeSeries.of(times, values);
  }

  @Override
  protected InstantDoubleTimeSeries createTimeSeries(final List<Instant> times, final List<Double> values) {
    return ImmutableInstantDoubleTimeSeries.of(times, values);
  }

  @Override
  protected InstantDoubleTimeSeries createTimeSeries(final DoubleTimeSeries<Instant> dts) {
    return ImmutableInstantDoubleTimeSeries.from(dts);
  }

  //-------------------------------------------------------------------------
  //-------------------------------------------------------------------------
  //-------------------------------------------------------------------------
  public void test_of_Instant_double() {
    final InstantDoubleTimeSeries ts= ImmutableInstantDoubleTimeSeries.of(Instant.ofEpochSecond(12345), 2.0);
    assertEquals(ts.size(), 1);
    assertEquals(ts.getTimeAtIndex(0), Instant.ofEpochSecond(12345));
    assertEquals(ts.getValueAtIndex(0), 2.0);
  }

  @Test(expectedExceptions = NullPointerException.class)
  public void test_of_Instant_double_null() {
    ImmutableInstantDoubleTimeSeries.of((Instant) null, 2.0);
  }

  //-------------------------------------------------------------------------
  public void test_of_InstantArray_DoubleArray() {
    final Instant[] inDates = new Instant[] {Instant.ofEpochSecond(2222), Instant.ofEpochSecond(3333)};
    final Double[] inValues = new Double[] {2.0, 3.0};
    final InstantDoubleTimeSeries ts= ImmutableInstantDoubleTimeSeries.of(inDates, inValues);
    assertEquals(ts.size(), 2);
    assertEquals(ts.getTimeAtIndex(0), Instant.ofEpochSecond(2222));
    assertEquals(ts.getValueAtIndex(0), 2.0);
    assertEquals(ts.getTimeAtIndex(1), Instant.ofEpochSecond(3333));
    assertEquals(ts.getValueAtIndex(1), 3.0);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_of_InstantArray_DoubleArray_wrongOrder() {
    final Instant[] inDates = new Instant[] {Instant.ofEpochSecond(2222), Instant.ofEpochSecond(3333), Instant.ofEpochSecond(1111)};
    final Double[] inValues = new Double[] {2.0, 3.0, 1.0};
    ImmutableInstantDoubleTimeSeries.of(inDates, inValues);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_of_InstantArray_DoubleArray_mismatchedArrays() {
    final Instant[] inDates = new Instant[] {Instant.ofEpochSecond(2222)};
    final Double[] inValues = new Double[] {2.0, 3.0};
    ImmutableInstantDoubleTimeSeries.of(inDates, inValues);
  }

  @Test(expectedExceptions = NullPointerException.class)
  public void test_of_InstantArray_DoubleArray_nullDates() {
    final Double[] inValues = new Double[] {2.0, 3.0, 1.0};
    ImmutableInstantDoubleTimeSeries.of((Instant[]) null, inValues);
  }

  @Test(expectedExceptions = NullPointerException.class)
  public void test_of_InstantArray_DoubleArray_nullValues() {
    final Instant[] inDates = new Instant[] {Instant.ofEpochSecond(2222), Instant.ofEpochSecond(3333), Instant.ofEpochSecond(1111)};
    ImmutableInstantDoubleTimeSeries.of(inDates, (Double[]) null);
  }

  //-------------------------------------------------------------------------
  public void test_of_InstantArray_doubleArray() {
    final Instant[] inDates = new Instant[] {Instant.ofEpochSecond(2222), Instant.ofEpochSecond(3333)};
    final double[] inValues = new double[] {2.0, 3.0};
    final InstantDoubleTimeSeries ts= ImmutableInstantDoubleTimeSeries.of(inDates, inValues);
    assertEquals(ts.size(), 2);
    assertEquals(ts.getTimeAtIndex(0), Instant.ofEpochSecond(2222));
    assertEquals(ts.getValueAtIndex(0), 2.0);
    assertEquals(ts.getTimeAtIndex(1), Instant.ofEpochSecond(3333));
    assertEquals(ts.getValueAtIndex(1), 3.0);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_of_InstantArray_doubleArray_wrongOrder() {
    final Instant[] inDates = new Instant[] {Instant.ofEpochSecond(2222), Instant.ofEpochSecond(3333), Instant.ofEpochSecond(1111)};
    final double[] inValues = new double[] {2.0, 3.0, 1.0};
    ImmutableInstantDoubleTimeSeries.of(inDates, inValues);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_of_InstantArray_doubleArray_mismatchedArrays() {
    final Instant[] inDates = new Instant[] {Instant.ofEpochSecond(2222)};
    final double[] inValues = new double[] {2.0, 3.0};
    ImmutableInstantDoubleTimeSeries.of(inDates, inValues);
  }

  @Test(expectedExceptions = NullPointerException.class)
  public void test_of_InstantArray_doubleArray_nullDates() {
    final double[] inValues = new double[] {2.0, 3.0, 1.0};
    ImmutableInstantDoubleTimeSeries.of((Instant[]) null, inValues);
  }

  @Test(expectedExceptions = NullPointerException.class)
  public void test_of_InstantArray_doubleArray_nullValues() {
    final Instant[] inDates = new Instant[] {Instant.ofEpochSecond(2222), Instant.ofEpochSecond(3333), Instant.ofEpochSecond(1111)};
    ImmutableInstantDoubleTimeSeries.of(inDates, (double[]) null);
  }

  //-------------------------------------------------------------------------
  public void test_of_longArray_doubleArray() {
    final long[] inDates = new long[] {2222_000_000_000L, 3333_000_000_000L};
    final double[] inValues = new double[] {2.0, 3.0};
    final InstantDoubleTimeSeries ts= ImmutableInstantDoubleTimeSeries.of(inDates, inValues);
    assertEquals(ts.size(), 2);
    assertEquals(ts.getTimeAtIndex(0), Instant.ofEpochSecond(2222));
    assertEquals(ts.getValueAtIndex(0), 2.0);
    assertEquals(ts.getTimeAtIndex(1), Instant.ofEpochSecond(3333));
    assertEquals(ts.getValueAtIndex(1), 3.0);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_of_longArray_doubleArray_wrongOrder() {
    final long[] inDates = new long[] {2222_000_000_000L, 3333_000_000_000L, 1111_000_000_000L};
    final double[] inValues = new double[] {2.0, 3.0, 1.0};
    ImmutableInstantDoubleTimeSeries.of(inDates, inValues);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_of_longArray_doubleArray_mismatchedArrays() {
    final long[] inDates = new long[] {2222_000_000_000L};
    final double[] inValues = new double[] {2.0, 3.0};
    ImmutableInstantDoubleTimeSeries.of(inDates, inValues);
  }

  @Test(expectedExceptions = NullPointerException.class)
  public void test_of_longArray_doubleArray_nullDates() {
    final double[] inValues = new double[] {2.0, 3.0, 1.0};
    ImmutableInstantDoubleTimeSeries.of((long[]) null, inValues);
  }

  @Test(expectedExceptions = NullPointerException.class)
  public void test_of_longArray_doubleArray_nullValues() {
    final long[] inDates = new long[] {2222_000_000_000L, 3333_000_000_000L};
    ImmutableInstantDoubleTimeSeries.of(inDates, (double[]) null);
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_intersectionFirstValue_selectFirst() {
    final InstantDoubleTimeSeries dts = createStandardTimeSeries();
    final InstantDoubleTimeSeries dts2 = createStandardTimeSeries2();
    final InstantDoubleTimeSeries dts3 = ImmutableInstantDoubleTimeSeries.builder()
        .putAll(dts2).put(dts2.getEarliestTime(), -1.0).build();

    final InstantDoubleTimeSeries result1 = dts.intersectionFirstValue(dts3);
    assertEquals(3, result1.size());
    assertEquals(Double.valueOf(4.0), result1.getValueAtIndex(0));
    assertEquals(Double.valueOf(5.0), result1.getValueAtIndex(1));
    assertEquals(Double.valueOf(6.0), result1.getValueAtIndex(2));
    assertEquals(dts.getTimeAtIndex(3), result1.getTimeAtIndex(0));
    assertEquals(dts.getTimeAtIndex(4), result1.getTimeAtIndex(1));
    assertEquals(dts.getTimeAtIndex(5), result1.getTimeAtIndex(2));

    final InstantDoubleTimeSeries result2 = dts3.intersectionFirstValue(dts);
    assertEquals(3, result2.size());
    assertEquals(Double.valueOf(-1.0), result2.getValueAtIndex(0));
    assertEquals(Double.valueOf(5.0), result2.getValueAtIndex(1));
    assertEquals(Double.valueOf(6.0), result2.getValueAtIndex(2));
    assertEquals(dts.getTimeAtIndex(3), result2.getTimeAtIndex(0));
    assertEquals(dts.getTimeAtIndex(4), result2.getTimeAtIndex(1));
    assertEquals(dts.getTimeAtIndex(5), result2.getTimeAtIndex(2));
  }

  @Test
  public void test_intersectionSecondValue_selectSecond() {
    final InstantDoubleTimeSeries dts = createStandardTimeSeries();
    final InstantDoubleTimeSeries dts2 = createStandardTimeSeries2();
    final InstantDoubleTimeSeries dts3 = ImmutableInstantDoubleTimeSeries.builder()
        .putAll(dts2).put(dts2.getEarliestTime(), -1.0).build();

    final InstantDoubleTimeSeries result2 = dts.intersectionSecondValue(dts3);
    assertEquals(3, result2.size());
    assertEquals(Double.valueOf(-1.0), result2.getValueAtIndex(0));
    assertEquals(Double.valueOf(5.0), result2.getValueAtIndex(1));
    assertEquals(Double.valueOf(6.0), result2.getValueAtIndex(2));
    assertEquals(dts.getTimeAtIndex(3), result2.getTimeAtIndex(0));
    assertEquals(dts.getTimeAtIndex(4), result2.getTimeAtIndex(1));
    assertEquals(dts.getTimeAtIndex(5), result2.getTimeAtIndex(2));

    final InstantDoubleTimeSeries result1 = dts3.intersectionSecondValue(dts);
    assertEquals(3, result1.size());
    assertEquals(Double.valueOf(4.0), result1.getValueAtIndex(0));
    assertEquals(Double.valueOf(5.0), result1.getValueAtIndex(1));
    assertEquals(Double.valueOf(6.0), result1.getValueAtIndex(2));
    assertEquals(dts.getTimeAtIndex(3), result1.getTimeAtIndex(0));
    assertEquals(dts.getTimeAtIndex(4), result1.getTimeAtIndex(1));
    assertEquals(dts.getTimeAtIndex(5), result1.getTimeAtIndex(2));
  }

  //-------------------------------------------------------------------------
  public void test_toString() {
    final InstantDoubleTimeSeries ts= ImmutableInstantDoubleTimeSeries.of(Instant.ofEpochSecond(2222), 2.0);
    assertEquals("ImmutableInstantDoubleTimeSeries[(" + Instant.ofEpochSecond(2222) + ", 2.0)]", ts.toString());
  }

  //-------------------------------------------------------------------------
  //-------------------------------------------------------------------------
  //-------------------------------------------------------------------------
  public void test_builder_nothingAdded() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    assertEquals(ImmutableInstantDoubleTimeSeries.EMPTY_SERIES, bld.build());
  }

  //-------------------------------------------------------------------------
  @Override
  public void test_iterator() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    bld.put(Instant.ofEpochSecond(2222), 2.0).put(Instant.ofEpochSecond(3333), 3.0).put(Instant.ofEpochSecond(1111), 1.0);
    final InstantDoubleEntryIterator it = bld.iterator();
    assertEquals(true, it.hasNext());
    assertEquals(new AbstractMap.SimpleImmutableEntry<>(Instant.ofEpochSecond(1111), 1.0d), it.next());
    assertEquals(Instant.ofEpochSecond(1111), it.currentTime());
    assertEquals(1111_000_000_000L, it.currentTimeFast());
    assertEquals(1.0d, it.currentValue());
    assertEquals(1.0d, it.currentValueFast());
    assertEquals(Instant.ofEpochSecond(2222), it.nextTime());
    assertEquals(Instant.ofEpochSecond(3333), it.nextTime());
    assertEquals(false, it.hasNext());
  }

  public void test_iterator_empty() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    assertEquals(false, bld.iterator().hasNext());
  }

  public void test_iterator_removeFirst() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    bld.put(Instant.ofEpochSecond(2222), 2.0).put(Instant.ofEpochSecond(3333), 3.0).put(Instant.ofEpochSecond(1111), 1.0);
    final InstantDoubleEntryIterator it = bld.iterator();
    it.next();
    it.remove();
    assertEquals(2, bld.size());
    final Instant[] outDates = new Instant[] {Instant.ofEpochSecond(2222), Instant.ofEpochSecond(3333)};
    final double[] outValues = new double[] {2.0, 3.0};
    assertEquals(ImmutableInstantDoubleTimeSeries.of(outDates, outValues), bld.build());
  }

  public void test_iterator_removeMid() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    bld.put(Instant.ofEpochSecond(2222), 2.0).put(Instant.ofEpochSecond(3333), 3.0).put(Instant.ofEpochSecond(1111), 1.0);
    final InstantDoubleEntryIterator it = bld.iterator();
    it.next();
    it.next();
    it.remove();
    assertEquals(2, bld.size());
    final Instant[] outDates = new Instant[] {Instant.ofEpochSecond(1111), Instant.ofEpochSecond(3333)};
    final double[] outValues = new double[] {1.0, 3.0};
    assertEquals(ImmutableInstantDoubleTimeSeries.of(outDates, outValues), bld.build());
  }

  public void test_iterator_removeLast() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    bld.put(Instant.ofEpochSecond(2222), 2.0).put(Instant.ofEpochSecond(3333), 3.0).put(Instant.ofEpochSecond(1111), 1.0);
    final InstantDoubleEntryIterator it = bld.iterator();
    it.next();
    it.next();
    it.next();
    it.remove();
    assertEquals(2, bld.size());
    final Instant[] outDates = new Instant[] {Instant.ofEpochSecond(1111), Instant.ofEpochSecond(2222)};
    final double[] outValues = new double[] {1.0, 2.0};
    assertEquals(ImmutableInstantDoubleTimeSeries.of(outDates, outValues), bld.build());
  }

  //-------------------------------------------------------------------------
  public void test_builder_put_LD() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    bld.put(Instant.ofEpochSecond(2222), 2.0).put(Instant.ofEpochSecond(3333), 3.0).put(Instant.ofEpochSecond(1111), 1.0);
    final Instant[] outDates = new Instant[] {Instant.ofEpochSecond(1111), Instant.ofEpochSecond(2222), Instant.ofEpochSecond(3333)};
    final double[] outValues = new double[] {1.0, 2.0, 3.0};
    assertEquals(ImmutableInstantDoubleTimeSeries.of(outDates, outValues), bld.build());
  }

  public void test_builder_put_Instant_alreadyThere() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    bld.put(Instant.ofEpochSecond(2222), 2.0).put(Instant.ofEpochSecond(3333), 3.0).put(Instant.ofEpochSecond(2222), 1.0);
    final Instant[] outDates = new Instant[] {Instant.ofEpochSecond(2222), Instant.ofEpochSecond(3333)};
    final double[] outValues = new double[] {1.0, 3.0};
    assertEquals(ImmutableInstantDoubleTimeSeries.of(outDates, outValues), bld.build());
  }

  //-------------------------------------------------------------------------
  public void test_builder_put_long() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    bld.put(2222_000_000_000L, 2.0).put(3333_000_000_000L, 3.0).put(1111_000_000_000L, 1.0);
    final Instant[] outDates = new Instant[] {Instant.ofEpochSecond(1111), Instant.ofEpochSecond(2222), Instant.ofEpochSecond(3333)};
    final double[] outValues = new double[] {1.0, 2.0, 3.0};
    assertEquals(ImmutableInstantDoubleTimeSeries.of(outDates, outValues), bld.build());
  }

  public void test_builder_put_long_alreadyThere() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    bld.put(2222_000_000_000L, 2.0).put(3333_000_000_000L, 3.0).put(2222_000_000_000L, 1.0);
    final Instant[] outDates = new Instant[] {Instant.ofEpochSecond(2222), Instant.ofEpochSecond(3333)};
    final double[] outValues = new double[] {1.0, 3.0};
    assertEquals(ImmutableInstantDoubleTimeSeries.of(outDates, outValues), bld.build());
  }

  public void test_builder_put_long_big() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    final long[] outDates = new long[600];
    final double[] outValues = new double[600];
    for (int i = 0; i < 600; i++) {
      bld.put(2222_000_000_000L + i, i);
      outDates[i] = 2222_000_000_000L + i;
      outValues[i] = i;
    }
    assertEquals(ImmutableInstantDoubleTimeSeries.of(outDates, outValues), bld.build());
  }

  //-------------------------------------------------------------------------
  public void test_builder_putAll_LD() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    final Instant[] inDates = new Instant[] {Instant.ofEpochSecond(2222), Instant.ofEpochSecond(3333), Instant.ofEpochSecond(1111)};
    final double[] inValues = new double[] {2.0, 3.0, 1.0};
    bld.putAll(inDates, inValues);
    final Instant[] outDates = new Instant[] {Instant.ofEpochSecond(1111), Instant.ofEpochSecond(2222), Instant.ofEpochSecond(3333)};
    final double[] outValues = new double[] {1.0, 2.0, 3.0};
    assertEquals(ImmutableInstantDoubleTimeSeries.of(outDates, outValues), bld.build());
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_builder_putAll_Instant_mismatchedArrays() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    final Instant[] inDates = new Instant[] {Instant.ofEpochSecond(2222), Instant.ofEpochSecond(3333)};
    final double[] inValues = new double[] {2.0, 3.0, 1.0};
    bld.putAll(inDates, inValues);
  }

  //-------------------------------------------------------------------------
  public void test_builder_putAll_long() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    final long[] inDates = new long[] {2222_000_000_000L, 3333_000_000_000L, 1111_000_000_000L};
    final double[] inValues = new double[] {2.0, 3.0, 1.0};
    bld.putAll(inDates, inValues);
    final Instant[] outDates = new Instant[] {Instant.ofEpochSecond(1111), Instant.ofEpochSecond(2222), Instant.ofEpochSecond(3333)};
    final double[] outValues = new double[] {1.0, 2.0, 3.0};
    assertEquals(ImmutableInstantDoubleTimeSeries.of(outDates, outValues), bld.build());
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_builder_putAll_long_mismatchedArrays() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    final long[] inDates = new long[] {2222_000_000_000L, 3333_000_000_000L};
    final double[] inValues = new double[] {2.0, 3.0, 1.0};
    bld.putAll(inDates, inValues);
  }

  //-------------------------------------------------------------------------
  public void test_builder_putAll_DDTS() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    final long[] inDates = new long[] {1111_000_000_000L, 2222_000_000_000L, 3333_000_000_000L};
    final double[] inValues = new double[] {1.0, 2.0, 3.0};
    final PreciseDoubleTimeSeries<?> ddts = ImmutableInstantDoubleTimeSeries.of(inDates, inValues);
    bld.putAll(ddts);
    final Instant[] outDates = new Instant[] {Instant.ofEpochSecond(1111), Instant.ofEpochSecond(2222), Instant.ofEpochSecond(3333)};
    final double[] outValues = new double[] {1.0, 2.0, 3.0};
    assertEquals(ImmutableInstantDoubleTimeSeries.of(outDates, outValues), bld.build());
  }

  //-------------------------------------------------------------------------
  public void test_builder_putAll_DDTS_range_allNonEmptyBuilder() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    final long[] inDates = new long[] {1111_000_000_000L, 2222_000_000_000L, 3333_000_000_000L};
    final double[] inValues = new double[] {1.0, 2.0, 3.0};
    final PreciseDoubleTimeSeries<?> ddts = ImmutableInstantDoubleTimeSeries.of(inDates, inValues);
    bld.put(Instant.ofEpochSecond(0), 0.5).putAll(ddts, 0, 3);
    final Instant[] outDates = new Instant[] {Instant.ofEpochSecond(0), Instant.ofEpochSecond(1111), Instant.ofEpochSecond(2222), Instant.ofEpochSecond(3333)};
    final double[] outValues = new double[] {0.5, 1.0, 2.0, 3.0};
    assertEquals(ImmutableInstantDoubleTimeSeries.of(outDates, outValues), bld.build());
  }

  public void test_builder_putAll_DDTS_range_fromStart() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    final long[] inDates = new long[] {1111_000_000_000L, 2222_000_000_000L, 3333_000_000_000L};
    final double[] inValues = new double[] {1.0, 2.0, 3.0};
    final PreciseDoubleTimeSeries<?> ddts = ImmutableInstantDoubleTimeSeries.of(inDates, inValues);
    bld.putAll(ddts, 0, 1);
    final Instant[] outDates = new Instant[] {Instant.ofEpochSecond(1111)};
    final double[] outValues = new double[] {1.0};
    assertEquals(ImmutableInstantDoubleTimeSeries.of(outDates, outValues), bld.build());
  }

  public void test_builder_putAll_DDTS_range_toEnd() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    final long[] inDates = new long[] {1111_000_000_000L, 2222_000_000_000L, 3333_000_000_000L};
    final double[] inValues = new double[] {1.0, 2.0, 3.0};
    final PreciseDoubleTimeSeries<?> ddts = ImmutableInstantDoubleTimeSeries.of(inDates, inValues);
    bld.putAll(ddts, 1, 3);
    final Instant[] outDates = new Instant[] {Instant.ofEpochSecond(2222), Instant.ofEpochSecond(3333)};
    final double[] outValues = new double[] {2.0, 3.0};
    assertEquals(ImmutableInstantDoubleTimeSeries.of(outDates, outValues), bld.build());
  }

  public void test_builder_putAll_DDTS_range_empty() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    final long[] inDates = new long[] {1111_000_000_000L, 2222_000_000_000L, 3333_000_000_000L};
    final double[] inValues = new double[] {1.0, 2.0, 3.0};
    final PreciseDoubleTimeSeries<?> ddts = ImmutableInstantDoubleTimeSeries.of(inDates, inValues);
    bld.put(Instant.ofEpochSecond(0), 0.5).putAll(ddts, 1, 1);
    final Instant[] outDates = new Instant[] {Instant.ofEpochSecond(0)};
    final double[] outValues = new double[] {0.5};
    assertEquals(ImmutableInstantDoubleTimeSeries.of(outDates, outValues), bld.build());
  }

  @Test(expectedExceptions = IndexOutOfBoundsException.class)
  public void test_builder_putAll_DDTS_range_startInvalidLow() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    final long[] inDates = new long[] {1111_000_000_000L, 2222_000_000_000L, 3333_000_000_000L};
    final double[] inValues = new double[] {1.0, 2.0, 3.0};
    final PreciseDoubleTimeSeries<?> ddts = ImmutableInstantDoubleTimeSeries.of(inDates, inValues);
    bld.putAll(ddts, -1, 3);
  }

  @Test(expectedExceptions = IndexOutOfBoundsException.class)
  public void test_builder_putAll_DDTS_range_startInvalidHigh() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    final long[] inDates = new long[] {1111_000_000_000L, 2222_000_000_000L, 3333_000_000_000L};
    final double[] inValues = new double[] {1.0, 2.0, 3.0};
    final PreciseDoubleTimeSeries<?> ddts = ImmutableInstantDoubleTimeSeries.of(inDates, inValues);
    bld.putAll(ddts, 4, 2);
  }

  @Test(expectedExceptions = IndexOutOfBoundsException.class)
  public void test_builder_putAll_DDTS_range_endInvalidLow() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    final long[] inDates = new long[] {1111_000_000_000L, 2222_000_000_000L, 3333_000_000_000L};
    final double[] inValues = new double[] {1.0, 2.0, 3.0};
    final PreciseDoubleTimeSeries<?> ddts = ImmutableInstantDoubleTimeSeries.of(inDates, inValues);
    bld.putAll(ddts, 1, -1);
  }

  @Test(expectedExceptions = IndexOutOfBoundsException.class)
  public void test_builder_putAll_DDTS_range_endInvalidHigh() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    final long[] inDates = new long[] {1111_000_000_000L, 2222_000_000_000L, 3333_000_000_000L};
    final double[] inValues = new double[] {1.0, 2.0, 3.0};
    final PreciseDoubleTimeSeries<?> ddts = ImmutableInstantDoubleTimeSeries.of(inDates, inValues);
    bld.putAll(ddts, 3, 4);
  }

  @Test(expectedExceptions = IndexOutOfBoundsException.class)
  public void test_builder_putAll_DDTS_range_startEndOrder() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    final long[] inDates = new long[] {1111_000_000_000L, 2222_000_000_000L, 3333_000_000_000L};
    final double[] inValues = new double[] {1.0, 2.0, 3.0};
    final PreciseDoubleTimeSeries<?> ddts = ImmutableInstantDoubleTimeSeries.of(inDates, inValues);
    bld.putAll(ddts, 3, 2);
  }

  //-------------------------------------------------------------------------
  public void test_builder_putAll_Map() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    final Map<Instant, Double> map = new HashMap<>();
    map.put(Instant.ofEpochSecond(2222), 2.0d);
    map.put(Instant.ofEpochSecond(3333), 3.0d);
    map.put(Instant.ofEpochSecond(1111), 1.0d);
    bld.putAll(map);
    final Instant[] outDates = new Instant[] {Instant.ofEpochSecond(1111), Instant.ofEpochSecond(2222), Instant.ofEpochSecond(3333)};
    final double[] outValues = new double[] {1.0, 2.0, 3.0};
    assertEquals(ImmutableInstantDoubleTimeSeries.of(outDates, outValues), bld.build());
  }

  public void test_builder_putAll_Map_empty() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    final Map<Instant, Double> map = new HashMap<>();
    bld.put(Instant.ofEpochSecond(0), 0.5).putAll(map);
    final Instant[] outDates = new Instant[] {Instant.ofEpochSecond(0)};
    final double[] outValues = new double[] {0.5};
    assertEquals(ImmutableInstantDoubleTimeSeries.of(outDates, outValues), bld.build());
  }

  //-------------------------------------------------------------------------
  public void test_builder_clearEmpty() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    bld.clear();
    assertEquals(ImmutableInstantDoubleTimeSeries.EMPTY_SERIES, bld.build());
  }

  public void test_builder_clearSomething() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    bld.put(2222_000_000_000L, 1.0).clear();
    assertEquals(ImmutableInstantDoubleTimeSeries.EMPTY_SERIES, bld.build());
  }

  //-------------------------------------------------------------------------
  public void test_builder_toString() {
    final InstantDoubleTimeSeriesBuilder bld = ImmutableInstantDoubleTimeSeries.builder();
    assertEquals("Builder[size=1]", bld.put(2222_000_000_000L, 1.0).toString());
  }

}

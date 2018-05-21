package io.bootique.metrics.health.check;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ValueRangeTest {

    @Test
    public void testReachedThreshold_Int() {

        ValueRange<Integer> range = ValueRange.create(0, 5, 8, 11);

        assertFalse(range.reachedThreshold(-1).isPresent());
        assertEquals(ThresholdType.MIN, range.reachedThreshold(0).get().getType());
        assertEquals(ThresholdType.WARNING, range.reachedThreshold(5).get().getType());
        assertEquals(ThresholdType.WARNING, range.reachedThreshold(6).get().getType());
        assertEquals(ThresholdType.CRITICAL, range.reachedThreshold(9).get().getType());
    }

    @Test
    public void testReachedThreshold_Double() {

        ValueRange<Double> range = ValueRange.create(0., 5.1, 8.6, 11.);

        assertFalse(range.reachedThreshold(-1.).isPresent());
        assertEquals(ThresholdType.MIN, range.reachedThreshold(0.).get().getType());
        assertEquals(ThresholdType.WARNING, range.reachedThreshold(5.1).get().getType());
        assertEquals(ThresholdType.WARNING, range.reachedThreshold(5.2).get().getType());
        assertEquals(ThresholdType.CRITICAL, range.reachedThreshold(9.).get().getType());
    }

    @Test
    public void testToString() {
        ValueRange<Double> range = ValueRange.create(0., 5.1, 8.6, 11.);
        assertEquals("min:0.0, warning:5.1, critical:8.6, max:11.0", range.toString());
    }
}

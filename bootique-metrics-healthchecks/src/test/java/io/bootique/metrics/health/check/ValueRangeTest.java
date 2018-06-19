/*
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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

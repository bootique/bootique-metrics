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

import io.bootique.metrics.health.HealthCheckStatus;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;

public class ValueRangeCheckTest {

    @Test
    public void check() {

        Supplier<Integer> supplier = mock(Supplier.class);
        when(supplier.get()).thenReturn(-1, 0, 5, 6, 9, 12);

        ValueRange<Integer> range = ValueRange.create(0, 5, 8, 10);

        ValueRangeCheck<Integer> check = new ValueRangeCheck<>(range, supplier);

        assertEquals(HealthCheckStatus.UNKNOWN, check.check().getStatus());
        assertEquals(HealthCheckStatus.OK, check.check().getStatus());
        assertEquals(HealthCheckStatus.WARNING, check.check().getStatus());
        assertEquals(HealthCheckStatus.WARNING, check.check().getStatus());
        assertEquals(HealthCheckStatus.CRITICAL, check.check().getStatus());
        assertEquals(HealthCheckStatus.CRITICAL, check.check().getStatus());
    }
}

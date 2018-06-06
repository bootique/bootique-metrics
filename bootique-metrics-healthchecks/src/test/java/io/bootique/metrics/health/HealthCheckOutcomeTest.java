/**
 *    Licensed to the ObjectStyle LLC under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ObjectStyle LLC licenses
 *  this file to you under the Apache License, Version 2.0 (the
 *  “License”); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.bootique.metrics.health;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class HealthCheckOutcomeTest {

    @Test
    public void testCompareTo() {
        HealthCheckOutcome o1 = HealthCheckOutcome.outcome(HealthCheckStatus.OK, "C", null);
        HealthCheckOutcome o2 = HealthCheckOutcome.outcome(HealthCheckStatus.WARNING, "B", null);
        HealthCheckOutcome o3 = HealthCheckOutcome.outcome(HealthCheckStatus.CRITICAL, "D", null);
        HealthCheckOutcome o4 = HealthCheckOutcome.outcome(HealthCheckStatus.UNKNOWN, "A", null);

        assertTrue(o1.compareTo(o2) < 0);
        assertTrue(o2.compareTo(o3) < 0);
        assertTrue(o3.compareTo(o4) > 0);
        assertTrue(o2.compareTo(o4) < 0);
    }
}

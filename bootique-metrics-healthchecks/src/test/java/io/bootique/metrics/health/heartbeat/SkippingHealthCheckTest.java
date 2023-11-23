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
package io.bootique.metrics.health.heartbeat;

import io.bootique.metrics.health.HealthCheck;
import io.bootique.metrics.health.HealthCheckOutcome;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SkippingHealthCheckTest {

    @Test
    public void safeCheck_RunEvery1() {
        CountingHealthCheck chc = new CountingHealthCheck();
        SkippingHealthCheck shc = new SkippingHealthCheck(chc, 1);

        shc.safeCheck();
        assertEquals(1, chc.counter);

        shc.safeCheck();
        assertEquals(2, chc.counter);

        shc.safeCheck();
        assertEquals(3, chc.counter);

        shc.safeCheck();
        assertEquals(4, chc.counter);

        shc.safeCheck();
        assertEquals(5, chc.counter);
    }

    @Test
    public void safeCheck_RunEvery2() {
        CountingHealthCheck chc = new CountingHealthCheck();
        SkippingHealthCheck shc = new SkippingHealthCheck(chc, 2);

        shc.safeCheck();
        assertEquals(1, chc.counter);

        shc.safeCheck();
        assertEquals(1, chc.counter);

        shc.safeCheck();
        assertEquals(2, chc.counter);

        shc.safeCheck();
        assertEquals(2, chc.counter);

        shc.safeCheck();
        assertEquals(3, chc.counter);
    }

    static class CountingHealthCheck implements HealthCheck {

        int counter;

        @Override
        public HealthCheckOutcome check() throws Exception {
            counter++;
            return HealthCheckOutcome.ok();
        }
    }
}

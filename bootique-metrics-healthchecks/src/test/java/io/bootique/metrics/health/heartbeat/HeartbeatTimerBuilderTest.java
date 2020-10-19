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
import io.bootique.metrics.health.HealthCheckRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HeartbeatTimerBuilderTest {

    private HealthCheck success;
    private HealthCheck failure;
    private ExecutorService threadPool;
    private Timer timer;

    @Before
    public void before() {
        this.success = mock(HealthCheck.class);
        when(success.safeCheck()).thenReturn(HealthCheckOutcome.ok());

        this.failure = mock(HealthCheck.class);
        when(failure.safeCheck()).thenReturn(HealthCheckOutcome.critical("uh"));

        this.timer = null;
        this.threadPool = null;
    }

    @After
    public void after() {
        if (timer != null) {
            timer.cancel();
        }

        if (threadPool != null) {
            threadPool.shutdownNow();
        }
    }


    @Test
    public void testStart() throws InterruptedException {

        TestListener listener = new TestListener();

        HealthCheckRegistry registry = createRegistry(success, failure);

        this.threadPool = Executors.newFixedThreadPool(2);
        this.timer = new HeartbeatTimerBuilder(registry)
                .initialDelayMs(3)
                .fixedDelayMs(50)
                .listener(listener)
                .start();

        // we can't reliably predict the exact number of invocations at any given moment in the test, but we can
        // check that the heart is beating...

        int c1 = listener.counter;

        Thread.sleep(100);
        int c2 = listener.counter;
        assertTrue(c1 < c2);

        Thread.sleep(100);
        int c3 = listener.counter;
        assertTrue(c2 < c3);
    }

    private HealthCheckRegistry createRegistry(HealthCheck... checks) {

        Map<String, HealthCheck> healthChecks = new HashMap<>();
        for (int i = 0; i < checks.length; i++) {
            healthChecks.put(String.valueOf(i), checks[i]);
        }

        return new HealthCheckRegistry(healthChecks);
    }

    class TestListener implements HeartbeatListener {

        int counter;

        @Override
        public void healthChecksFinished(Map<String, HealthCheckOutcome> result) {
            counter++;
        }
    }
}

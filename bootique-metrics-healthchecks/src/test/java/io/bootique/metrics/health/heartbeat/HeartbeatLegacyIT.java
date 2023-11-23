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

import io.bootique.BQRuntime;
import io.bootique.junit5.BQTest;
import io.bootique.junit5.BQTestFactory;
import io.bootique.junit5.BQTestTool;
import io.bootique.metrics.health.HealthCheck;
import io.bootique.metrics.health.HealthCheckModule;
import io.bootique.metrics.health.HealthCheckOutcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Deprecated
@BQTest
public class HeartbeatLegacyIT {

    @BQTestTool
    final BQTestFactory testFactory = new BQTestFactory();

    private final ThreadTester threadTester = new ThreadTester();
    private HealthCheck success;
    private HealthCheck failure;

    @BeforeEach
    public void before() {
        this.success = mock(HealthCheck.class);
        when(success.safeCheck()).thenReturn(HealthCheckOutcome.ok());
        when(success.isActive()).thenReturn(true);

        this.failure = mock(HealthCheck.class);
        when(failure.safeCheck()).thenReturn(HealthCheckOutcome.critical("uh"));
        when(failure.isActive()).thenReturn(true);
    }

    @Test
    public void heartbeat() throws InterruptedException {

        TestListener listener = new TestListener();

        BQRuntime runtime = testFactory.app("-c", "classpath:io/bootique/metrics/health/heartbeat/HeartbeatLegacyIT.yml")
                .autoLoadModules()
                .module(b -> HealthCheckModule.extend(b)
                        .addHealthCheck("hc1", success)
                        .addHealthCheck("ignored", failure)
                        .addHeartbeatListener(listener))
                .createRuntime();

        threadTester.assertNoHeartbeat();

        Heartbeat hb = runtime.getInstance(Heartbeat.class);

        Thread.sleep(100);

        // not started yet...
        threadTester.assertNoHeartbeat();

        // start..
        hb.start();
        Thread.sleep(100);
        threadTester.assertHasHeartbeat();

        // we can't reliably predict the exact number of invocations at any given moment in the test, but we can
        // check that the heart is beating...

        int c1 = listener.counter;

        Thread.sleep(100);
        int c2 = listener.counter;
        assertTrue(c1 < c2);

        Thread.sleep(100);
        int c3 = listener.counter;
        assertTrue(c2 < c3);

        threadTester.assertPoolSize(2);

        runtime.shutdown();

        // give a chance to stop ... without this the assertion below would fail occasionally
        Thread.sleep(100);
        threadTester.assertNoHeartbeat();
    }

    private static class ThreadTester {

        public void assertNoHeartbeat() {
            long matched = allThreads().filter(this::isTimerThread).count();
            assertEquals(0, matched);
        }

        public void assertHasHeartbeat() {
            long matched = allThreads().filter(this::isTimerThread).count();
            assertEquals(1, matched);
        }

        public void assertPoolSize(int expected) {
            long matched = allThreads().filter(this::isPoolThread).count();
            assertEquals(expected, matched);
        }

        private boolean isTimerThread(Thread t) {
            // the name comes from HeartbeatLauncher
            return "bootique-heartbeat".equals(t.getName());
        }

        private boolean isPoolThread(Thread t) {
            // the name comes from HeartbeatFactory
            return t.getName().startsWith("bootique-healthcheck-");
        }

        private Stream<Thread> allThreads() {
            ThreadGroup tg = Thread.currentThread().getThreadGroup();
            while (tg.getParent() != null) {
                tg = tg.getParent();
            }

            Thread[] active = new Thread[tg.activeCount()];
            tg.enumerate(active);

            // there is a small chance a thread becomes inactive between 'activeCount' and 'enumerate' calls,
            // resulting in null threads in the array.. remove null threads from the result
            return Arrays.stream(active).filter(t -> t != null);
        }
    }

    static class TestListener implements HeartbeatListener {

        int counter;

        @Override
        public void healthChecksFinished(Map<String, HealthCheckOutcome> result) {
            counter++;

            // check filtering
            assertTrue(result.containsKey("hc1"));
            assertFalse(result.containsKey("ignored"));
        }
    }

}

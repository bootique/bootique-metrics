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

import io.bootique.metrics.health.HealthCheckRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

public class HeartbeatTest {

    TestHeartbeatWatch stopper = new TestHeartbeatWatch();

    @AfterEach
    public void afterEach() {
        stopper.stop();
    }

    @Test
    public void startStop() {
        HeartbeatRunner runner = new HeartbeatRunner(new HealthCheckRegistry(Map.of()), () -> Set.of(), 0, 1, 1, 1) {
            @Override
            public HeartbeatWatch start() {
                return stopper;
            }
        };

        Heartbeat hb = new Heartbeat(runner);
        assertFalse(stopper.stopped);

        hb.start();
        assertFalse(stopper.stopped);
        assertSame(stopper, hb.heartbeatWatch);

        hb.stop();
        assertTrue(stopper.stopped);
    }

    static class TestHeartbeatWatch extends HeartbeatWatch {

        boolean stopped;

        public TestHeartbeatWatch() {
            super(new Timer("test", true), Executors.newSingleThreadExecutor());
        }

        @Override
        public void stop() {
            stopped = true;
            super.stop();
        }
    }
}

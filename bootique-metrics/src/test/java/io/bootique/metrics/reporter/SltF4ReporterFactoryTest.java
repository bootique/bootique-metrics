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
package io.bootique.metrics.reporter;

import io.bootique.log.DefaultBootLogger;
import io.bootique.shutdown.DefaultShutdownManager;
import io.bootique.value.Duration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SltF4ReporterFactoryTest {

    static final DefaultShutdownManager shutdownManager = new DefaultShutdownManager(
            java.time.Duration.ZERO,
            new DefaultBootLogger(false));

    @Test
    public void period() {
        Slf4jReporterFactory factory = new Slf4jReporterFactory(shutdownManager);
        factory.setPeriod(new Duration("10min"));
        assertEquals(600_000L, factory.resolvePeriod().toMillis());
    }

    @Test
    public void period_Default() {
        Slf4jReporterFactory factory = new Slf4jReporterFactory(shutdownManager);
        assertEquals(30_000L, factory.resolvePeriod().toMillis());
    }
}

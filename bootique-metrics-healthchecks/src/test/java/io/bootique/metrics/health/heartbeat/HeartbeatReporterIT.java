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
import io.bootique.metrics.health.HealthCheckData;
import io.bootique.metrics.health.HealthCheckModule;
import io.bootique.metrics.health.HealthCheckOutcome;
import io.bootique.metrics.health.check.ValueRange;
import io.bootique.value.Percent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@BQTest
public class HeartbeatReporterIT {

    @BQTestTool
    final BQTestFactory testFactory = new BQTestFactory();

    private HealthCheck success;

    @BeforeEach
    public void before() {

        TestInMemorySinkFactory.reset();

        ValueRange<Percent> range = ValueRange.builder(Percent.class)
                .min(Percent.ZERO)
                .warning(new Percent(0.7555))
                .critical(new Percent(0.9))
                .max(Percent.HUNDRED).build();

        HealthCheckData<Percent> data = new HealthCheckData<>(new Percent(0.4), range);

        this.success = mock(HealthCheck.class);
        when(success.safeCheck()).thenReturn(HealthCheckOutcome.ok().withData(data));
        when(success.isActive()).thenReturn(true);
    }

    @Test
    public void testHeartbeat_Enabled() throws InterruptedException {

        BQRuntime runtime = testFactory.app("-c", "classpath:io/bootique/metrics/health/heartbeat/HeartbeatReporterIT.yml")
                .autoLoadModules()
                .module(b -> HealthCheckModule.extend(b)
                        .addHealthCheck("hc1", success)
                        // testing this setting...
                        .enableHeartbeatReporting()
                )
                .createRuntime();

        TestInMemorySinkFactory.assertNoReport();

        Heartbeat hb = runtime.getInstance(Heartbeat.class);

        Thread.sleep(100);

        // not started yet...
        TestInMemorySinkFactory.assertNoReport();

        // start..
        hb.start();
        Thread.sleep(100);

        // config has "percentPrecision: 3", so "75.555" will be truncated in the output
        TestInMemorySinkFactory.assertReport("OK", "hc1 OK|'hc1'=40%;75.5;90;0;100", "");
    }

    @Test
    public void testHeartbeat_Disabled() throws InterruptedException {

        BQRuntime runtime = testFactory.app("-c", "classpath:io/bootique/metrics/health/heartbeat/HeartbeatReporterIT.yml")
                .autoLoadModules()
                .module(b -> HealthCheckModule.extend(b)
                                .addHealthCheck("hc1", success)
                        // testing that NOT enabling reporting results in no reporting
                        // .enableHeartbeatReporting()
                )
                .createRuntime();

        TestInMemorySinkFactory.assertNoReport();

        Heartbeat hb = runtime.getInstance(Heartbeat.class);

        Thread.sleep(100);

        // not started yet...
        TestInMemorySinkFactory.assertNoReport();

        // start..
        hb.start();
        Thread.sleep(100);
        TestInMemorySinkFactory.assertNoReport();
    }


}

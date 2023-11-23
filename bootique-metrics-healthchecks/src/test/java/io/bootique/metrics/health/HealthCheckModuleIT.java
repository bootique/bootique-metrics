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

package io.bootique.metrics.health;

import io.bootique.BQRuntime;
import io.bootique.junit5.BQTest;
import io.bootique.junit5.BQTestFactory;
import io.bootique.junit5.BQTestTool;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@BQTest
public class HealthCheckModuleIT {

    @BQTestTool
    final BQTestFactory testFactory = new BQTestFactory();

    @Test
    public void healthcheckRegistry() {

        BQRuntime runtime = testFactory.app().autoLoadModules().createRuntime();

        HealthCheckRegistry r1 = runtime.getInstance(HealthCheckRegistry.class);
        HealthCheckRegistry r2 = runtime.getInstance(HealthCheckRegistry.class);
        assertNotNull(r1);
        assertSame(r1, r2, "HealthCheckRegistry must be a singleton");
    }

    @Test
    public void healthcheckRegistry_Contributions() {

        HealthCheckOutcome hcr = mock(HealthCheckOutcome.class);
        HealthCheck hc = mock(HealthCheck.class);
        when(hc.safeCheck()).thenReturn(hcr);

        BQRuntime runtime = testFactory
                .app()
                .module(HealthCheckModule.class)
                .module(b -> HealthCheckModule.extend(b).addHealthCheck("x", hc))
                .createRuntime();

        HealthCheckRegistry r = runtime.getInstance(HealthCheckRegistry.class);
        assertSame(hcr, r.runHealthCheck("x"));
    }
}

/**
 *  Licensed to ObjectStyle LLC under one
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

package io.bootique.metrics;

import com.codahale.metrics.MetricRegistry;
import io.bootique.BQRuntime;
import io.bootique.metrics.reporter.JmxReporterFactory;
import io.bootique.metrics.reporter.Slf4jReporterFactory;
import io.bootique.test.junit.BQTestFactory;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class MetricsModuleIT {

    @Rule
    public final BQTestFactory testFactory = new BQTestFactory();

    protected BQRuntime createRuntime(String... args) {
        return testFactory.app(args).module(MetricsModule.class).createRuntime();
    }

    @Test
    public void testMetricRegistryConfig() {

        BQRuntime runtime = createRuntime("--config", "classpath:config1.yml");
        MetricRegistryFactory factory = runtime.getInstance(MetricRegistryFactory.class);

        assertEquals(3, factory.getReporters().size());
        assertTrue(factory.getReporters().get(0) instanceof Slf4jReporterFactory);
        assertTrue(factory.getReporters().get(1) instanceof JmxReporterFactory);
        assertTrue(factory.getReporters().get(2) instanceof Slf4jReporterFactory);
    }

    @Test
    public void testMetricRegistry() {

        BQRuntime runtime = createRuntime();

        MetricRegistry r1 = runtime.getInstance(MetricRegistry.class);
        MetricRegistry r2 = runtime.getInstance(MetricRegistry.class);
        assertNotNull(r1);
        assertSame("MetricRegistry must be a singleton", r1, r2);
    }
}

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

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HealthCheckRegistryTest {

    private HealthCheck success;
    private HealthCheck inactive;
    private HealthCheck failure;
    private HealthCheck failureTh;
    private HealthCheck slowSuccess;

    @Before
    public void before() {
        this.success = mock(HealthCheck.class);
        when(success.safeCheck()).thenReturn(HealthCheckOutcome.ok());
        when(success.isActive()).thenReturn(true);

        this.inactive = mock(HealthCheck.class);
        when(inactive.safeCheck()).thenReturn(HealthCheckOutcome.ok());
        when(inactive.isActive()).thenReturn(false);

        this.failure = mock(HealthCheck.class);
        when(failure.safeCheck()).thenReturn(HealthCheckOutcome.critical("uh"));
        when(failure.isActive()).thenReturn(true);

        this.failureTh = mock(HealthCheck.class);
        when(failureTh.safeCheck()).thenReturn(HealthCheckOutcome.critical(new Throwable("uh")));
        when(failureTh.isActive()).thenReturn(true);

        this.slowSuccess = mock(HealthCheck.class);
        when(slowSuccess.isActive()).thenReturn(true);
        when(slowSuccess.safeCheck()).then(i -> {
            Thread.sleep(500);
            return HealthCheckOutcome.ok();
        });
    }

    private HealthCheckRegistry createRegistry(HealthCheck... checks) {

        Map<String, HealthCheck> healthChecks = new HashMap<>();
        for (int i = 0; i < checks.length; i++) {
            healthChecks.put(String.valueOf(i), checks[i]);
        }

        return new HealthCheckRegistry(healthChecks);
    }

    @Test
    public void testFiltered() {
        HealthCheckRegistry original = createRegistry(success, failure);
        HealthCheckRegistry filtered = original.filtered(s -> "1".equals(s));
        assertNotSame(original, filtered);

        Map<String, HealthCheckOutcome> originalResult = original.runHealthChecks();
        Map<String, HealthCheckOutcome> filteredResult = filtered.runHealthChecks();

        assertEquals(2, originalResult.size());
        assertEquals(1, filteredResult.size());
        assertEquals(HealthCheckStatus.CRITICAL, filteredResult.get("1").getStatus());
    }

    @Test
    public void testRunHealthChecks_Serial() {
        HealthCheckRegistry registry = createRegistry(success, failure);

        Map<String, HealthCheckOutcome> results = registry.runHealthChecks();
        assertEquals(2, results.size());
        assertEquals(HealthCheckStatus.OK, results.get("0").getStatus());
        assertEquals(HealthCheckStatus.CRITICAL, results.get("1").getStatus());
    }

    @Test
    public void testRunHealthChecks_Serial_Inactive() {
        HealthCheckRegistry registry = createRegistry(success, inactive);

        Map<String, HealthCheckOutcome> results = registry.runHealthChecks();
        assertEquals(1, results.size());
        assertEquals(HealthCheckStatus.OK, results.get("0").getStatus());
    }

    @Test
    public void testRunHealthChecks_Parallel() {

        HealthCheckRegistry registry = createRegistry(success, failure, failureTh);

        Map<String, HealthCheckOutcome> results = runParallel(registry, 3, 10000);
        assertEquals(3, results.size());
        assertEquals(HealthCheckStatus.OK, results.get("0").getStatus());
        assertEquals(HealthCheckStatus.CRITICAL, results.get("1").getStatus());
        assertEquals(HealthCheckStatus.CRITICAL, results.get("2").getStatus());
    }

    @Test
    public void testRunHealthChecks_Parallel_Inactive() {

        HealthCheckRegistry registry = createRegistry(success, inactive, failure);

        Map<String, HealthCheckOutcome> results = runParallel(registry, 3, 10000);
        assertEquals(2, results.size());
        assertEquals("Unexpected HC: " + results.get("0"), HealthCheckStatus.OK, results.get("0").getStatus());
        assertEquals("Unexpected HC: " + results.get("2"), HealthCheckStatus.CRITICAL, results.get("2").getStatus());
    }

    @Test
    public void testRunHealthChecks_ParallelTimeout() {

        HealthCheckRegistry registry = createRegistry(slowSuccess, success, slowSuccess);
        Map<String, HealthCheckOutcome> results = runParallel(registry, 3, 80);

        assertEquals(3, results.size());

        assertEquals(HealthCheckStatus.CRITICAL, results.get("0").getStatus());
        assertEquals("health check timed out", results.get("0").getMessage());

        assertEquals(HealthCheckStatus.OK, results.get("1").getStatus());

        assertEquals(HealthCheckStatus.CRITICAL, results.get("2").getStatus());
        assertEquals("health check timed out", results.get("2").getMessage());
    }

    private Map<String, HealthCheckOutcome> runParallel(HealthCheckRegistry registry, int threads, long timeoutMs) {
        ExecutorService threadPool = Executors.newFixedThreadPool(threads);

        try {
            return registry.runHealthChecks(threadPool, timeoutMs, TimeUnit.MILLISECONDS);
        } finally {
            threadPool.shutdownNow();
        }
    }
}

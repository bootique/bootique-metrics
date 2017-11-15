package io.bootique.health;

import io.bootique.metrics.health.HealthCheck;
import io.bootique.metrics.health.HealthCheckOutcome;
import io.bootique.metrics.health.HealthCheckRegistry;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HealthCheckRegistryTest {

    private HealthCheck success;
    private HealthCheck failure;
    private HealthCheck failureTh;
    private HealthCheck slowSuccess;

    @Before
    public void before() {
        this.success = mock(HealthCheck.class);
        when(success.safeCheck()).thenReturn(HealthCheckOutcome.healthy());

        this.failure = mock(HealthCheck.class);
        when(failure.safeCheck()).thenReturn(HealthCheckOutcome.unhealthy("uh"));

        this.failureTh = mock(HealthCheck.class);
        when(failureTh.safeCheck()).thenReturn(HealthCheckOutcome.unhealthy(new Throwable("uh")));

        this.slowSuccess = mock(HealthCheck.class);
        when(slowSuccess.safeCheck()).then(i -> {
            Thread.sleep(500);
            return HealthCheckOutcome.healthy();
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
        assertFalse(filteredResult.get("1").isHealthy());
    }

    @Test
    public void testRunHealthChecks_Serial() {
        HealthCheckRegistry registry = createRegistry(success, failure);

        Map<String, HealthCheckOutcome> results = registry.runHealthChecks();
        assertEquals(2, results.size());
        assertTrue(results.get("0").isHealthy());
        assertFalse(results.get("1").isHealthy());
    }

    @Test
    public void testRunHealthChecks_Parallel() {

        HealthCheckRegistry registry = createRegistry(success, failure, failureTh);

        Map<String, HealthCheckOutcome> results = registry.runHealthChecks(ForkJoinPool.commonPool());
        assertEquals(3, results.size());
        assertTrue(results.get("0").isHealthy());
        assertFalse(results.get("1").isHealthy());
        assertFalse(results.get("2").isHealthy());
    }

    @Test
    public void testRunHealthChecks_ParallelTimeout() {

        HealthCheckRegistry registry = createRegistry(slowSuccess, success, slowSuccess);
        Map<String, HealthCheckOutcome> results = runParallel(registry, 3, 80);

        assertEquals(3, results.size());

        assertFalse(results.get("0").isHealthy());
        assertEquals("health check timed out", results.get("0").getMessage());

        assertTrue(results.get("1").isHealthy());

        assertFalse(results.get("2").isHealthy());
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

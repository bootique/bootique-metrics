package io.bootique.metrics.health;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * An immutable registry of HealthChecks.
 *
 * @since 0.8
 */
public class HealthCheckRegistry {

    private Map<String, HealthCheck> healthChecks;

    public HealthCheckRegistry(Map<String, HealthCheck> healthChecks) {
        this.healthChecks = healthChecks;
    }

    private static HealthCheckOutcome waitForHealthCheck(
            Future<HealthCheckOutcome> inProcess,
            long singleCheckTimeout,
            TimeUnit timeoutUnit) {

        try {
            return inProcess.get(singleCheckTimeout, timeoutUnit);
        } catch (ExecutionException e) {
            return HealthCheckOutcome.unhealthy(e);
        } catch (TimeoutException e) {
            return HealthCheckOutcome.unhealthy("health check timed out");
        } catch (InterruptedException e) {
            return HealthCheckOutcome.unhealthy("health check interrupted");
        }
    }

    public HealthCheckOutcome runHealthCheck(String name) {
        HealthCheck healthCheck = healthChecks.get(name);
        if (healthCheck == null) {
            throw new IllegalArgumentException("No health check named " + name + " exists");
        }
        return healthCheck.safeCheck();
    }

    /**
     * Runs registered health checks sequentially.
     *
     * @return health checks execution results.
     */
    public Map<String, HealthCheckOutcome> runHealthChecks() {

        Map<String, HealthCheckOutcome> results = new HashMap<>();
        healthChecks.forEach((n, hc) -> results.put(n, hc.safeCheck()));
        return results;
    }

    /**
     * Runs registered health checks in parallel using provided thread pool.
     *
     * @param threadPool a thread pool to use for parallel execution of health checks.
     * @return health checks execution results.
     * @since 0.25
     */
    public Map<String, HealthCheckOutcome> runHealthChecks(ExecutorService threadPool) {
        return runHealthChecks(threadPool, 10, TimeUnit.SECONDS);
    }

    /**
     * Runs registered health checks in parallel using provided thread pool and using specified timeouts for each health
     * check.
     *
     * @param threadPool         a thread pool to use for parallel execution of health checks.
     * @param singleCheckTimeout max time to execute any single health check.
     * @param timeoutUnit        time unit for "singleCheckTimeout" value.
     * @return health checks execution results.
     * @since 0.25
     */
    public Map<String, HealthCheckOutcome> runHealthChecks(
            ExecutorService threadPool,
            long singleCheckTimeout,
            TimeUnit timeoutUnit) {

        if (healthChecks.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Future<HealthCheckOutcome>> futures = new HashMap<>();
        healthChecks.forEach((n, hc) -> futures.put(n, threadPool.submit(hc::safeCheck)));

        Map<String, HealthCheckOutcome> results = new HashMap<>();
        futures.forEach((n, f) -> results.put(n, waitForHealthCheck(f, singleCheckTimeout, timeoutUnit)));

        return results;
    }

}
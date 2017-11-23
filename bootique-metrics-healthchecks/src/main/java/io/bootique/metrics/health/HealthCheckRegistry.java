package io.bootique.metrics.health;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

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

    private static HealthCheckOutcome immediateOutcome(Future<HealthCheckOutcome> hcRunner) {

        if (hcRunner.isDone()) {
            try {
                return hcRunner.get();
            } catch (Exception e) {
                // unexpected... we should be done here...
                return HealthCheckOutcome.critical(e);
            }
        } else {
            return HealthCheckOutcome.critical("health check timed out");
        }
    }

    /**
     * Returns a new registry that contains a subset of health checks from the current registry, whose names match the
     * provided criteria.
     *
     * @param healthCheckFilter filtering criteria for health checks.
     * @return a new registry that contains a subset of health checks from the current registry, whose names match the
     * provided criteria.
     * @since 0.25
     */
    public HealthCheckRegistry filtered(Predicate<String> healthCheckFilter) {
        Map<String, HealthCheck> filtered = new HashMap<>();

        healthChecks.forEach((k, v) -> {
            if(healthCheckFilter.test(k)) {
                filtered.put(k, v);
            }
        });

        return new HealthCheckRegistry(filtered);
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
     * @param threadPool  a thread pool to use for parallel execution of health checks.
     * @param timeout     combined time to run all health checks.
     * @param timeoutUnit time unit for "timeout" value.
     * @return health checks execution results.
     * @since 0.25
     */
    public Map<String, HealthCheckOutcome> runHealthChecks(
            ExecutorService threadPool,
            long timeout,
            TimeUnit timeoutUnit) {

        if (healthChecks.isEmpty()) {
            return Collections.emptyMap();
        }

        // use the latch to ensure we can control the overall timeout, not individual health check timeouts...
        // note that if the health check pool is thread-starved, then a few slow checks would result in
        // faster checks reported as timeouts...
        CountDownLatch doneSignal = new CountDownLatch(healthChecks.size());

        Map<String, Future<HealthCheckOutcome>> futures = new HashMap<>();
        healthChecks.forEach((n, hc) -> futures.put(n, threadPool.submit(() -> run(hc, doneSignal))));

        try {
            doneSignal.await(timeout, timeoutUnit);
        } catch (InterruptedException e) {
            // let's still finish the healthcheck analysis on interrupt
        }

        Map<String, HealthCheckOutcome> results = new HashMap<>();
        futures.forEach((n, f) -> results.put(n, immediateOutcome(f)));

        return results;
    }

    private HealthCheckOutcome run(HealthCheck hc, CountDownLatch doneSignal) {
        HealthCheckOutcome outcome = hc.safeCheck();
        doneSignal.countDown();
        return outcome;
    }
}
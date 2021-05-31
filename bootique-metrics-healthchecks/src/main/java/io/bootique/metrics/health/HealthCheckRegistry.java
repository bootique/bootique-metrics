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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * An immutable registry of HealthChecks.
 *
 * @since 0.8
 */
public class HealthCheckRegistry {

    private final Map<String, HealthCheck> healthChecks;

    public HealthCheckRegistry(Map<String, HealthCheck> healthChecks) {
        this.healthChecks = healthChecks;
    }

    /**
     * @return whether a given named health check is present in the registry.
     * @since 0.25
     */
    public boolean containsHealthCheck(String name) {
        return healthChecks.containsKey(name);
    }

    /**
     * @return an immutable map of known health check names.
     * @since 1.0.RC1
     */
    public Set<String> healthCheckNames() {
        return Collections.unmodifiableSet(healthChecks.keySet());
    }

    /**
     * Returns a number of health checks present in the registry.
     *
     * @return a number of health checks present in the registry.
     * @since 0.25
     */
    public int size() {
        return healthChecks.size();
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
            if (healthCheckFilter.test(k)) {
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
        getActive().forEach((n, hc) -> results.put(n, hc.safeCheck()));
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

        Map<String, HealthCheck> activeChecks = getActive();
        if (activeChecks.isEmpty()) {
            return Collections.emptyMap();
        }

        HealthCheckExecutor healthCheckExecutor = new HealthCheckExecutor(activeChecks, threadPool);
        return healthCheckExecutor.runChecks(timeout, timeoutUnit);
    }

    // get health checks active at this instant
    private Map<String, HealthCheck> getActive() {

        if (healthChecks.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, HealthCheck> active = new HashMap<>((int) (healthChecks.size() / 0.75));

        healthChecks.forEach((k, v) -> {
            if (v.isActive()) {
                active.put(k, v);
            }
        });

        return active;
    }
}
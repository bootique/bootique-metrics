package io.bootique.metrics.healthcheck;

import java.util.HashMap;
import java.util.Map;

/**
 * An immutable registry of HealthChecks.
 *
 * @since 0.8
 */
public class HealthCheckRegistry {

    private Map<String, HealthCheck> healthchecks;

    public HealthCheckRegistry(Map<String, HealthCheck> healthchecks) {
        this.healthchecks = healthchecks;
    }

    public HealthCheckOutcome runHealthCheck(String name) {
        HealthCheck healthCheck = healthchecks.get(name);
        if (healthCheck == null) {
            throw new IllegalArgumentException("No health check named " + name + " exists");
        }
        return healthCheck.safeCheck();
    }

    public Map<String, HealthCheckOutcome> runHealthChecks() {

        Map<String, HealthCheckOutcome> results = new HashMap<>();

        healthchecks.forEach((name, healthcheck) ->
                results.put(name, healthcheck.safeCheck()));

        return results;
    }
}

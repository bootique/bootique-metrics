package io.bootique.metrics.healthcheck;

import com.codahale.metrics.health.HealthCheck;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

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

    public HealthCheck.Result runHealthCheck(String name) {
        HealthCheck healthCheck = healthchecks.get(name);
        if (healthCheck == null) {
            throw new IllegalArgumentException("No health check named " + name + " exists");
        }
        return healthCheck.execute();
    }

    public SortedMap<String, HealthCheck.Result> runHealthChecks() {
        SortedMap<String, HealthCheck.Result> results = new TreeMap<String, HealthCheck.Result>();
        for (Map.Entry<String, HealthCheck> entry : healthchecks.entrySet()) {
            HealthCheck.Result result = entry.getValue().execute();
            results.put(entry.getKey(), result);
        }
        return results;
    }
}

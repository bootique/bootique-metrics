package io.bootique.metrics.healthcheck;

import com.codahale.metrics.health.HealthCheck;

import java.util.Map;

/**
 * A factory of multiple health checks. A grouping of healthchecks for a given service or module in a single object is
 * often used for the purpose of simplifying injection. At the end these objects are used to populate
 * {@link HealthCheckRegistry}.
 *
 * @since 0.8
 */
public interface HealthCheckGroup {

    Map<String, HealthCheck> getHealthChecks();
}

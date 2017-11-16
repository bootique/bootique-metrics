package io.bootique.metrics.health.heartbeat;

import io.bootique.metrics.health.HealthCheckOutcome;

import java.util.Map;

/**
 * A listener notified of on each "heartbeat" when health checks finish.
 *
 * @since 0.25
 */
public interface HeartbeatListener {

    void healthChecksFinished(Map<String, HealthCheckOutcome> result);
}

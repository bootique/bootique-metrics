package io.bootique.metrics.health.check;

import io.bootique.metrics.health.HealthCheckStatus;

/**
 * @since 0.26
 */
public enum ThresholdType {

    MIN(HealthCheckStatus.OK),
    WARNING(HealthCheckStatus.WARNING),
    CRITICAL(HealthCheckStatus.CRITICAL),
    MAX(HealthCheckStatus.UNKNOWN);

    private HealthCheckStatus whenExceeds;

    ThresholdType(HealthCheckStatus whenExceeds) {
        this.whenExceeds = whenExceeds;
    }

    public HealthCheckStatus whenExceeds() {
        return whenExceeds;
    }
}

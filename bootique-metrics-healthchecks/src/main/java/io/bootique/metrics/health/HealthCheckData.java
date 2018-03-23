package io.bootique.metrics.health;

import io.bootique.metrics.health.check.ValueRange;

/**
 * Contains information about the metric and thresholds used to calculate the health check.
 *
 * @param <T> the type of metrics value used by the health check.
 * @since 0.26
 */
public class HealthCheckData<T extends Comparable<T>> {

    private T value;
    private ValueRange<T> thresholds;

    public HealthCheckData(T value, ValueRange<T> thresholds) {
        this.value = value;
        this.thresholds = thresholds;
    }

    public T getValue() {
        return value;
    }

    public ValueRange<T> getThresholds() {
        return thresholds;
    }
}

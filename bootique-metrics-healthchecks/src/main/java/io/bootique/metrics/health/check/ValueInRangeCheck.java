package io.bootique.metrics.health.check;

import io.bootique.metrics.health.HealthCheck;
import io.bootique.metrics.health.HealthCheckOutcome;
import io.bootique.metrics.health.HealthCheckStatus;

import java.util.function.Supplier;

public class ValueInRangeCheck<T extends Comparable<T>> implements HealthCheck {

    private ValueRange<T> range;
    private Supplier<T> valueSupplier;

    public ValueInRangeCheck(ValueRange<T> range, Supplier<T> valueSupplier) {
        this.range = range;
        this.valueSupplier = valueSupplier;
    }

    @Override
    public HealthCheckOutcome check() {

        T val = valueSupplier.get();
        HealthCheckStatus status = range.classify(val);
        switch (status) {
            case OK:
                return HealthCheckOutcome.ok();
            case WARNING:
                return forWarning(val);
            case CRITICAL:
                return forCritical(val);
            default:
                throw new RuntimeException("Unexpected status '" + status + "' for range position for value " + val);
        }
    }

    protected HealthCheckOutcome forWarning(T val) {
        return HealthCheckOutcome.warning("Value " + val + " exceeds warning threshold of " + range.getWarningThreshold());
    }

    protected HealthCheckOutcome forCritical(T val) {
        return HealthCheckOutcome.critical("Value " + val + " exceeds critical threshold of " + range.getCriticalThreshold());
    }
}

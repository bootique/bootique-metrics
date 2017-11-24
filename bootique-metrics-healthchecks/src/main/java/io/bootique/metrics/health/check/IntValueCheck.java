package io.bootique.metrics.health.check;

import io.bootique.metrics.health.HealthCheck;
import io.bootique.metrics.health.HealthCheckOutcome;

import java.util.function.Supplier;

/**
 * Defines a set of thresholds for a given int measured value, returning an outcome depending on where the current
 * measurement falls within these thresholds. Can be used with metrics Gauges.
 *
 * @since 0.25
 */
public abstract class IntValueCheck implements HealthCheck {

    private int warningThreshold;
    private int criticalThreshold;
    private Supplier<Integer> valueSupplier;
    private Supplier<HealthCheckOutcome> checker;

    public IntValueCheck(Supplier<Integer> valueSupplier, int criticalThreshold) {
        this(valueSupplier, -1, criticalThreshold, false);
    }

    public IntValueCheck(Supplier<Integer> valueSupplier, int warningThreshold, int criticalThreshold) {
        this(valueSupplier, warningThreshold, criticalThreshold, true);
    }

    protected IntValueCheck(
            Supplier<Integer> valueSupplier,
            int warningThreshold,
            int criticalThreshold,
            boolean checkWarning) {

        this.valueSupplier = valueSupplier;
        this.criticalThreshold = criticalThreshold;
        this.warningThreshold = warningThreshold;
        this.checker = checkWarning ? this::checkWarningCritical : this::checkCritical;
    }

    @Override
    public HealthCheckOutcome check() {
        return checker.get();
    }

    protected HealthCheckOutcome checkWarningCritical() {

        int val = valueSupplier.get();

        if (val <= warningThreshold) {
            return HealthCheckOutcome.ok();
        } else if (val <= criticalThreshold) {
            return forWarning(val);
        } else {
            return forCritical(val);
        }
    }

    protected HealthCheckOutcome checkCritical() {
        int val = valueSupplier.get();
        return val <= criticalThreshold ? HealthCheckOutcome.ok() : forCritical(val);
    }

    protected HealthCheckOutcome forWarning(int val) {
        return HealthCheckOutcome.warning("Value " + val + " exceeds warning threshold of " + warningThreshold);
    }

    protected HealthCheckOutcome forCritical(int val) {
        return HealthCheckOutcome.critical("Value " + val + " exceeds critical threshold of " + criticalThreshold);
    }
}

package io.bootique.metrics.health.check;

import io.bootique.metrics.health.HealthCheck;
import io.bootique.metrics.health.HealthCheckOutcome;

import java.util.function.Supplier;

/**
 * Defines a set of thresholds for a given double measured value, returning an outcome depending on where the current
 * measurement falls within these thresholds. Can be used with metrics Gauges.
 *
 * @since 0.25
 */
public class DoubleValueCheck implements HealthCheck {

    private double warningThreshold;
    private double criticalThreshold;
    private Supplier<Double> valueSupplier;
    private Supplier<HealthCheckOutcome> checker;

    public DoubleValueCheck(Supplier<Double> valueSupplier, double criticalThreshold) {
        this(valueSupplier, -1, criticalThreshold, false);
    }

    public DoubleValueCheck(Supplier<Double> valueSupplier, double warningThreshold, double criticalThreshold) {
        this(valueSupplier, warningThreshold, criticalThreshold, true);
    }

    protected DoubleValueCheck(
            Supplier<Double> valueSupplier,
            double warningThreshold,
            double criticalThreshold,
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

        double val = valueSupplier.get();

        if (val <= warningThreshold) {
            return HealthCheckOutcome.ok();
        } else if (val <= criticalThreshold) {
            return forWarning(val);
        } else {
            return forCritical(val);
        }
    }

    protected HealthCheckOutcome checkCritical() {
        double val = valueSupplier.get();
        return val <= criticalThreshold ? HealthCheckOutcome.ok() : forCritical(val);
    }

    protected HealthCheckOutcome forWarning(double val) {
        return HealthCheckOutcome.warning("Value " + val + " exceeds warning threshold of " + warningThreshold);
    }

    protected HealthCheckOutcome forCritical(double val) {
        return HealthCheckOutcome.critical("Value " + val + " exceeds critical threshold of " + criticalThreshold);
    }
}

package io.bootique.metrics.health.check;

import io.bootique.metrics.health.HealthCheckStatus;

import java.util.Objects;
import java.util.function.Function;

/**
 * @since 0.25
 */
public abstract class ValueRange<T extends Comparable<T>> {

    private T warningThreshold;
    private T criticalThreshold;
    private Function<T, HealthCheckStatus> checker;

    protected ValueRange(T warningThreshold, T criticalThreshold) {
        this.warningThreshold = warningThreshold;
        this.criticalThreshold = criticalThreshold;

        if (criticalThreshold == null) {
            checker = i -> HealthCheckStatus.OK;
        } else if (warningThreshold == null) {
            checker = this::checkCritical;
        } else {

            // both thresholds are here... validate they do not overlap

            if (warningThreshold.compareTo(criticalThreshold) >= 0) {
                throw new RuntimeException("Warning threshold '" + warningThreshold
                        + "' must be below critical '" + criticalThreshold + "'");
            }

            checker = this::checkWarningCritical;
        }
    }

    protected HealthCheckStatus checkWarningCritical(T val) {

        if (val.compareTo(warningThreshold) < 0) {
            return HealthCheckStatus.OK;
        } else if (val.compareTo(criticalThreshold) < 0) {
            return HealthCheckStatus.WARNING;
        } else {
            return HealthCheckStatus.CRITICAL;
        }
    }

    protected HealthCheckStatus checkCritical(T val) {
        return val.compareTo(criticalThreshold) < 0 ? HealthCheckStatus.OK : HealthCheckStatus.CRITICAL;
    }

    /**
     * Returns OK, WARNING or CRITICAL, depending on where the value falls within the defined range.
     *
     * @return OK, WARNING or CRITICAL, depending on where the value falls within the defined range.
     */
    public HealthCheckStatus classify(T val) {
        Objects.requireNonNull(val, "Value must be not null");
        return checker.apply(val);
    }

    public T getWarningThreshold() {
        return warningThreshold;
    }

    public T getCriticalThreshold() {
        return criticalThreshold;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();

        if (warningThreshold != null) {
            out.append(warningThreshold);
        }

        if (criticalThreshold != null) {
            if (warningThreshold != null) {
                out.append(", ");
            }

            out.append(criticalThreshold);
        }

        return out.toString();
    }
}

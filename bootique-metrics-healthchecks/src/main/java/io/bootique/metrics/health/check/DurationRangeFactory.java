package io.bootique.metrics.health.check;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.value.Duration;

/**
 * @since 0.26
 */
@BQConfig
public class DurationRangeFactory {
    private Duration min;
    private Duration warning;
    private Duration critical;
    private Duration max;

    public Duration getMin() {
        return min;
    }

    @BQConfigProperty
    public void setMin(Duration min) {
        this.min = min;
    }

    public Duration getWarning() {
        return warning;
    }

    @BQConfigProperty
    public void setWarning(Duration warning) {
        this.warning = warning;
    }

    public Duration getCritical() {
        return critical;
    }

    @BQConfigProperty
    public void setCritical(Duration critical) {
        this.critical = critical;
    }

    public Duration getMax() {
        return max;
    }

    @BQConfigProperty
    public void setMax(Duration max) {
        this.max = max;
    }

    public ValueRange<Duration> createRange() {

        ValueRange.Builder<Duration> builder = ValueRange.builder(Duration.class);

        if (min != null) {
            builder.min(min);
        }

        if (warning != null) {
            builder.warning(warning);
        }

        if (critical != null) {
            builder.critical(critical);
        }

        if (max != null) {
            builder.max(max);
        }

        return builder.build();
    }
}

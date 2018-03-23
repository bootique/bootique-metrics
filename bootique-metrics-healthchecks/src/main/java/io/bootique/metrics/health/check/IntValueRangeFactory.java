package io.bootique.metrics.health.check;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;

/**
 * @since 0.26
 */
@BQConfig
public class IntValueRangeFactory {

    private Integer min;
    private Integer warning;
    private Integer critical;
    private Integer max;

    public Integer getMin() {
        return min;
    }

    @BQConfigProperty
    public void setMin(int min) {
        this.min = min;
    }

    public Integer getWarning() {
        return warning;
    }

    @BQConfigProperty
    public void setWarning(int warning) {
        this.warning = warning;
    }

    public Integer getCritical() {
        return critical;
    }

    @BQConfigProperty
    public void setCritical(int critical) {
        this.critical = critical;
    }

    public Integer getMax() {
        return max;
    }

    @BQConfigProperty
    public void setMax(int max) {
        this.max = max;
    }

    public ValueRange<Integer> createRange() {

        ValueRange.Builder<Integer> builder = ValueRange.builder(Integer.class);

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

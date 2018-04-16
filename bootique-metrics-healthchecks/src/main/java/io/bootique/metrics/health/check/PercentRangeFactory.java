package io.bootique.metrics.health.check;

import io.bootique.annotation.BQConfigProperty;
import io.bootique.value.Percent;

/**
 * @since 0.26
 */
public class PercentRangeFactory {

    private Percent min;
    private Percent warning;
    private Percent critical;
    private Percent max;

    public PercentRangeFactory() {
        this.min = Percent.ZERO;
        this.max = Percent.HUNDRED;
    }

    @BQConfigProperty
    public void setMin(Percent min) {
        this.min = min;
    }

    public Percent getMin() {
        return min;
    }

    @BQConfigProperty
    public void setWarning(Percent warning) {
        this.warning = warning;
    }

    public Percent getMax() {
        return max;
    }

    @BQConfigProperty
    public void setCritical(Percent critical) {
        this.critical = critical;
    }

    public Percent getCritical() {
        return critical;
    }

    @BQConfigProperty
    public void setMax(Percent max) {
        this.max = max;
    }

    public Percent getWarning() {
        return warning;
    }

    public ValueRange<Percent> createRange() {

        ValueRange.Builder<Percent> builder = ValueRange.builder(Percent.class);

        if(min != null) {
            builder.min(min);
        }

        if(warning != null) {
            builder.warning(warning);
        }

        if(critical != null) {
            builder.critical(critical);
        }

        if(max != null) {
            builder.max(max);
        }

        return builder.build();
    }
}

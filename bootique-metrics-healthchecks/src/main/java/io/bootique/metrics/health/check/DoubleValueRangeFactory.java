package io.bootique.metrics.health.check;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;

/**
 * @since 0.25
 */
@BQConfig
public class DoubleValueRangeFactory {

    private Double min;
    private Double warning;
    private Double critical;
    private Double max;

    @BQConfigProperty
    public void setMin(double min) {
        this.min = min;
    }

    public Double getMin() {
        return min;
    }

    @BQConfigProperty
    public void setWarning(double warning) {
        this.warning = warning;
    }

    public Double getMax() {
        return max;
    }

    @BQConfigProperty
    public void setCritical(double critical) {
        this.critical = critical;
    }

    public Double getCritical() {
        return critical;
    }

    @BQConfigProperty
    public void setMax(double max) {
        this.max = max;
    }

    public Double getWarning() {
        return warning;
    }

    public ValueRange<Double> createRange() {

        ValueRange.Builder<Double> builder = ValueRange.builder(Double.class);

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

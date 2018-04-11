package io.bootique.metrics.health.check;

import io.bootique.metrics.health.HealthCheck;
import io.bootique.metrics.health.HealthCheckData;
import io.bootique.metrics.health.HealthCheckOutcome;

import java.util.function.Supplier;

/**
 * @param <T> the type of value that the health check will verify.
 * @since 0.25
 */
public class ValueRangeCheck<T extends Comparable<T>> implements HealthCheck {

    private ValueRange<T> range;
    private Supplier<T> valueSupplier;

    public ValueRangeCheck(ValueRange<T> range, Supplier<T> valueSupplier) {
        this.range = range;
        this.valueSupplier = valueSupplier;
    }

    @Override
    public HealthCheckOutcome check() {

        T val = valueSupplier.get();
        HealthCheckData<T> data = new HealthCheckData<>(val, range);

        return range.reachedThreshold(val)
                .map(t -> toOutcome(t, data))
                .orElse(toUnknownOutcome(data));
    }

    protected HealthCheckOutcome toOutcome(Threshold<T> th, HealthCheckData<T> data) {

        switch (th.getType()) {
            case MIN:
                return HealthCheckOutcome.ok().withData(data);
            case WARNING:
                return HealthCheckOutcome
                        .warning("Value " + data.getValue() + " reaches or exceeds warning threshold of " + th.getValue())
                        .withData(data);
            case CRITICAL:
                return HealthCheckOutcome
                        .critical("Value " + data.getValue() + " reaches or exceeds critical threshold of " + th.getValue())
                        .withData(data);
            case MAX:
                // report max as CRITICAL
                return HealthCheckOutcome
                        .critical("Value " + data.getValue() + " reaches or exceeds max threshold of " + th.getValue())
                        .withData(data);
            default:
                throw new RuntimeException("Unexpected threshold type '"
                        + th.getType()
                        + "' for range position for value " + data.getValue());
        }
    }

    protected HealthCheckOutcome toUnknownOutcome(HealthCheckData<T> data) {
        return HealthCheckOutcome
                .unknown("Value " + data.getValue() + " is outside expected min/max range")
                .withData(data);
    }
}

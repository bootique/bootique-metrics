package io.bootique.metrics.health.check;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Defines a range of incrementing values with optional min/max boundaries and internal thresholds.
 *
 * @since 0.25
 */
public class ValueRange<T extends Comparable<T>> {

    private List<Threshold<T>> thresholdsHighestFirst;

    protected ValueRange(List<Threshold<T>> thresholdsHighestFirst) {
        this.thresholdsHighestFirst = thresholdsHighestFirst;
    }

    public static <T extends Comparable<T>> Builder<T> builder(Class<T> type) {
        return new Builder<>();
    }

    public static <T extends Comparable<T>> ValueRange<T> create(T min, T warn, T critical, T max) {
        Builder<T> builder = new Builder<>();
        return builder.min(min).warning(warn).critical(critical).max(max).build();
    }

    /**
     * Returns a threshold exceeded by this value. If value is below the lowest threshold, an empty optional is returned.
     *
     * @return a threshold exceeded by this value wrapped in an Optional or an empty Optional.
     */
    public Optional<Threshold<T>> reachedThreshold(T val) {
        Objects.requireNonNull(val, "Value must be not null");

        // return a matching status for the first exceeded threshold
        for (Threshold<T> th : thresholdsHighestFirst) {

            if (th.compareTo(val) <= 0) {
                return Optional.of(th);
            }
        }

        return Optional.empty();
    }

    public List<Threshold<T>> getThresholdsHighestFirst() {
        return thresholdsHighestFirst;
    }

    public Threshold<T> getThreshold(ThresholdType type) {

        for (Threshold<T> t : thresholdsHighestFirst) {
            if (t.getType() == type) {
                return t;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();

        for (int i = thresholdsHighestFirst.size() - 1; i >= 0; i--) {
            if (i > 0) {
                out.append(", ");
            }

            out.append(thresholdsHighestFirst.get(i));
        }

        return out.toString();
    }

    public static class Builder<T extends Comparable<T>> {

        private List<Threshold<T>> thresholds;

        protected Builder() {
            this.thresholds = new ArrayList<>(4);
        }

        public Builder<T> min(T min) {
            thresholds.add(new Threshold<>(ThresholdType.MIN, min));
            return this;
        }

        public Builder<T> max(T max) {
            thresholds.add(new Threshold<>(ThresholdType.MAX, max));
            return this;
        }

        public Builder<T> warning(T warning) {
            thresholds.add(new Threshold<>(ThresholdType.WARNING, warning));
            return this;
        }

        public Builder<T> critical(T critical) {
            thresholds.add(new Threshold<>(ThresholdType.CRITICAL, critical));
            return this;
        }

        public ValueRange<T> build() {
            Collections.sort(thresholds, Collections.reverseOrder());
            return new ValueRange<T>(thresholds);
        }
    }
}

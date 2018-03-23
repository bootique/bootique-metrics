package io.bootique.metrics.health.check;

/**
 * @since 0.26
 */
public class Threshold<T extends Comparable<T>> implements Comparable<Threshold<T>> {

    private ThresholdType type;
    private T value;

    public Threshold(ThresholdType type, T value) {
        this.type = type;
        this.value = value;
    }

    public int compareTo(T otherValue) {
        // nulls-first compare...
        if (value == null) {
            return otherValue != null ? -1 : 0;
        }

        if (otherValue == null) {
            return 1;
        }

        return value.compareTo(otherValue);
    }

    @Override
    public int compareTo(Threshold<T> o) {
        return compareTo(o.value);
    }

    public T getValue() {
        return value;
    }

    public ThresholdType getType() {
        return type;
    }

    @Override
    public String toString() {

        StringBuilder out = new StringBuilder();
        out.append(type.name().toLowerCase())
                .append(":")
                .append(value);

        return out.toString();
    }
}

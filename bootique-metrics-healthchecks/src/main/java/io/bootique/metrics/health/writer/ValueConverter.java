package io.bootique.metrics.health.writer;

/**
 * @param <T>
 * @since 1.0.RC1
 */
public interface ValueConverter<T> {

    String printableValue(T value, boolean includeUnits);
}

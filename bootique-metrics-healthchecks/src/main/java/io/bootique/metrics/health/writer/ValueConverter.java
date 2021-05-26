package io.bootique.metrics.health.writer;

/**
 * @param <T>
 */
public interface ValueConverter<T> {

    String printableValue(T value, boolean includeUnits);
}

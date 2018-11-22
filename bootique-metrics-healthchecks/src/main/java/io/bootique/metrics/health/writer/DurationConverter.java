package io.bootique.metrics.health.writer;

import io.bootique.value.Duration;

public class DurationConverter implements ValueConverter<Duration> {

    @Override
    public String printableValue(Duration value, boolean includeUnits) {
        java.time.Duration jtDuration = value.getDuration();

        // total duration of java.time.duration has to be manually calculated from seconds and nanos
        long ms = (jtDuration.getSeconds() * 1_000_000_000 + jtDuration.getNano()) / 1_000_000L;
        return includeUnits ? ms + "ms" : Long.toString(ms);
    }
}

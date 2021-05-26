package io.bootique.metrics.health.writer;

import io.bootique.metrics.health.HealthCheckOutcome;
import io.bootique.metrics.health.sink.ReportSink;

import java.util.Map;

/**
 * Writes formatted health check report to the provided "sink". Report format is defined by the implementation.
 */
public interface ReportWriter {

    void write(Map<String, HealthCheckOutcome> result, ReportSink out);
}

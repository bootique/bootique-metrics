package com.nhl.bootique.metrics.reporter;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.nhl.bootique.shutdown.ShutdownManager;

import java.util.concurrent.TimeUnit;

/**
 * A {@link ReporterFactory} that produces a {@link Slf4jReporter}.
 */
@JsonTypeName("slf4j")
public class Slf4jReporterFactory implements ReporterFactory {

    @Override
    public void installReporter(MetricRegistry metricRegistry, ShutdownManager shutdownManager) {
        Slf4jReporter reporter = Slf4jReporter.forRegistry(metricRegistry).build();
        shutdownManager.addShutdownHook(reporter);

        // TODO: parameterize
        reporter.start(30, TimeUnit.SECONDS);
    }
}

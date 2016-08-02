package com.nhl.bootique.metrics;

import com.codahale.metrics.MetricRegistry;
import com.nhl.bootique.metrics.reporter.ReporterFactory;
import com.nhl.bootique.shutdown.ShutdownManager;

import java.util.List;

public class MetricRegistryFactory {

    private static final String FACTORY_TYPE_KEY = "type";

    private List<ReporterFactory> reporters;

    public MetricRegistry createMetricsRegistry(ShutdownManager shutdownManager) {

        MetricRegistry registry = new MetricRegistry();

        if (reporters != null) {
            reporters.forEach(reporterFactory -> reporterFactory.installReporter(registry, shutdownManager));
        }

        return registry;
    }

    List<ReporterFactory> getReporters() {
        return reporters;
    }

    public void setReporters(List<ReporterFactory> reporters) {
        this.reporters = reporters;
    }
}

package io.bootique.metrics;

import com.codahale.metrics.MetricRegistry;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.metrics.reporter.ReporterFactory;
import io.bootique.shutdown.ShutdownManager;

import java.util.List;

@BQConfig("Configures MetricsRegistry")
public class MetricRegistryFactory {

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

    @BQConfigProperty("A List of reporter factories.")
    public void setReporters(List<ReporterFactory> reporters) {
        this.reporters = reporters;
    }
}

package io.bootique.metrics.reporter;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.bootique.annotation.BQConfig;
import io.bootique.shutdown.ShutdownManager;

/**
 * A {@link ReporterFactory} that produces a {@link JmxReporter}.
 */
@BQConfig("Configures JMX reporter. Metrics MBeans will have 'bq.metrics' prefix.")
@JsonTypeName("jmx")
public class JmxReporterFactory implements ReporterFactory {

    @Override
    public void installReporter(MetricRegistry metricRegistry, ShutdownManager shutdownManager) {
        JmxReporter reporter = JmxReporter.forRegistry(metricRegistry).inDomain("bq.metrics").build();
        shutdownManager.addShutdownHook(reporter);
        reporter.start();
    }
}

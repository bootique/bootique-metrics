package io.bootique.metrics.reporter;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.bootique.annotation.BQConfig;
import io.bootique.shutdown.ShutdownManager;

/**
 * Superinterface of reporter factories.
 *
 * @since 0.7
 */
@BQConfig
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Slf4jReporterFactory.class)
public interface ReporterFactory {

	void installReporter(MetricRegistry metricRegistry, ShutdownManager shutdownManager);
}

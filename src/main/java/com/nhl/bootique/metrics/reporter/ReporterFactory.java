package com.nhl.bootique.metrics.reporter;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.nhl.bootique.shutdown.ShutdownManager;

/**
 * Superinterface of reporter factories.
 *
 * @since 0.7
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Slf4jReporterFactory.class)
public interface ReporterFactory {

	void installReporter(MetricRegistry metricRegistry, ShutdownManager shutdownManager);
}

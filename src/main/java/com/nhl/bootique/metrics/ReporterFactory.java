package com.nhl.bootique.metrics;

import com.codahale.metrics.MetricRegistry;
import com.nhl.bootique.shutdown.ShutdownManager;

interface ReporterFactory {

	void installReporter(MetricRegistry metricRegistry, ShutdownManager shutdownManager);

}

package com.nhl.bootique.metrics;

import java.util.Map;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.nhl.bootique.shutdown.ShutdownManager;

public class MetricRegistryProvider implements Provider<MetricRegistry> {

	private MetricRegistryFactory metricRegistryFactory;
	private ShutdownManager shutdownManager;
	private Map<String, Metric> metrics;

	@Inject
	public MetricRegistryProvider(MetricRegistryFactory metricRegistryFactory, ShutdownManager shutdownManager,
			Map<String, Metric> metrics) {
		this.metricRegistryFactory = metricRegistryFactory;
		this.shutdownManager = shutdownManager;
		this.metrics = metrics;
	}

	@Override
	public MetricRegistry get() {
		return metricRegistryFactory.createMetricsRegistry(metrics, shutdownManager);
	}
}

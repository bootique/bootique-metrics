package com.nhl.bootique.metrics;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.nhl.bootique.shutdown.ShutdownManager;

public class MetricRegistryProvider implements Provider<MetricRegistry> {

	private MetricRegistryFactory metricRegistryFactory;
	private ShutdownManager shutdownManager;

	@Inject
	public MetricRegistryProvider(MetricRegistryFactory metricRegistryFactory, ShutdownManager shutdownManager) {
		this.metricRegistryFactory = metricRegistryFactory;
		this.shutdownManager = shutdownManager;
	}

	@Override
	public MetricRegistry get() {
		return metricRegistryFactory.createMetricsRegistry(shutdownManager);
	}
}

package com.nhl.bootique.metrics;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.nhl.bootique.shutdown.ShutdownManager;

public class MetricRegistryFactory {

	private static final String FACTORY_TYPE_KEY = "type";

	// TODO: change this into polimorphic ReporterFactory interface 

	// TODO: once the above is accomplished, we can make factories
	// auto-discoverable via ServiceLoader approach..

	private List<Map<String, String>> reporters;

	public MetricRegistry createMetricsRegistry(ShutdownManager shutdownManager) {

		MetricRegistry registry = new MetricRegistry();

		if (reporters != null) {
			reporters.forEach(factorySettings -> toFactory(factorySettings).installReporter(registry, shutdownManager));
		}

		return registry;
	}

	public void setReporters(List<Map<String, String>> reporters) {
		this.reporters = reporters;
	}

	protected ReporterFactory toFactory(Map<String, String> factorySettings) {

		String type = Objects.requireNonNull(factorySettings.get(FACTORY_TYPE_KEY));

		switch (type) {

		case "jmx":
			return new JmxReporterFactory();
		case "slf4j":
			return new Slf4jReporterFactory();
		default:
			// see TODO above about custom metrics reporters...
			throw new IllegalStateException("Unsupported ");
		}
	}

	class JmxReporterFactory implements ReporterFactory {

		@Override
		public void installReporter(MetricRegistry metricRegistry, ShutdownManager shutdownManager) {
			JmxReporter reporter = JmxReporter.forRegistry(metricRegistry).inDomain("bq.metrics").build();
			shutdownManager.addShutdownHook(reporter);
			reporter.start();
		}
	}

	class Slf4jReporterFactory implements ReporterFactory {

		@Override
		public void installReporter(MetricRegistry metricRegistry, ShutdownManager shutdownManager) {
			Slf4jReporter reporter = Slf4jReporter.forRegistry(metricRegistry).build();
			shutdownManager.addShutdownHook(reporter);

			// TODO: parameterize
			reporter.start(60, TimeUnit.SECONDS);
		}
	}

}

package com.nhl.bootique.metrics;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.google.inject.Binder;
import com.google.inject.multibindings.MapBinder;

public class MetricsBinder {

	public static MetricsBinder contributeTo(Binder binder) {
		return new MetricsBinder(binder);
	}

	private Binder binder;

	MetricsBinder(Binder binder) {
		this.binder = binder;
	}

	MapBinder<String, Metric> metricsBinder() {
		return MapBinder.newMapBinder(binder, String.class, Metric.class);
	}

	/**
	 * Adds a specified metric to the injectable metrics map.
	 * 
	 * @param name
	 *            metric name.
	 * @param metric
	 *            metric instance.
	 */
	public void metric(String name, Metric metric) {
		metricsBinder().addBinding(name).toInstance(metric);
	}

	public void metrics(MetricSet metrics) {
		metrics.getMetrics().forEach((name, metric) -> metric(name, metric));
	}
}

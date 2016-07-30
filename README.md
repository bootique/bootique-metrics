[![Build Status](https://travis-ci.org/nhl/bootique-metrics.svg)](https://travis-ci.org/nhl/bootique-metrics)

# bootique-metrics

Provides [Dropwizard Metrics](http://metrics.dropwizard.io/) integration module for [Bootique](http://bootique.io).

## Quick Start:

Add the Metrics module to your Bootique app:

```xml
<dependency>
	<groupId>com.nhl.bootique.metrics</groupId>
	<artifactId>bootique-metrics</artifactId>
	<scope>compile</scope>
</dependency>
```

Inject [MetricRegistry](http://metrics.dropwizard.io/3.1.0/apidocs/com/codahale/metrics/MetricRegistry.html) anywhere 
in your code where you need to create [Meters](http://metrics.dropwizard.io/3.1.0/getting-started/#meters), 
[Gauges](http://metrics.dropwizard.io/3.1.0/getting-started/#gauges), 
[Counters](http://metrics.dropwizard.io/3.1.0/getting-started/#counters), 
[Histograms](http://metrics.dropwizard.io/3.1.0/getting-started/#histograms), 
[Timers](http://metrics.dropwizard.io/3.1.0/getting-started/#timers), etc.

```java
@Inject
public MyObject(MetricRegistry metrics) {
	this.timer = metrics.timer(MetricRegistry.name(RequestTimer.class, "work-timer"));
}
...
public void doWork() {
	Timer.Context context = this.timer.time();
	try {
		// do work
	} finally {
		long timeNanos = context.stop();
	}
}
```
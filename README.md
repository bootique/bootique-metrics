<!--
  Licensed to ObjectStyle LLC under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ObjectStyle LLC licenses
  this file to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
  -->
[![build test deploy](https://github.com/bootique/bootique-metrics/actions/workflows/maven.yml/badge.svg)](https://github.com/bootique/bootique-metrics/actions/workflows/maven.yml)	
[![Maven Central](https://img.shields.io/maven-central/v/io.bootique.metrics/bootique-metrics.svg?colorB=brightgreen)](https://search.maven.org/artifact/io.bootique.metrics/bootique-metrics/)


# bootique-metrics

Provides [Dropwizard Metrics](http://metrics.dropwizard.io/) integration module for [Bootique](http://bootique.io).
See usage example [bootique-metrics-demo](https://github.com/bootique-examples/bootique-metrics-demo).

## Quick Start:

Add the Metrics module to your Bootique app:

```xml
<dependency>
	<groupId>io.bootique.metrics</groupId>
	<artifactId>bootique-metrics</artifactId>
	<scope>compile</scope>
</dependency>
```

Inject [MetricRegistry](http://metrics.dropwizard.io/3.1.0/apidocs/com/codahale/metrics/MetricRegistry.html) anywhere 
in your code where you need to create [Meters](http://metrics.dropwizard.io/3.1.0/getting-started/#meters), 
[Gauges](http://metrics.dropwizard.io/3.1.0/getting-started/#gauges), 
[Counters](http://metrics.dropwizard.io/3.1.0/getting-started/#counters), 
[Histograms](http://metrics.dropwizard.io/3.1.0/getting-started/#histograms), 
[Timers](http://metrics.dropwizard.io/3.1.0/getting-started/#timers), etc. E.g.:

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

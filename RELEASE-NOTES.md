## 3.0.M2

* #40 Heartbeat to support skipping individual health checks
* #45 Upgrade to metrics 4.2.15
* #46 Convert TransactionIdMDC to static API
* #47 API to wrap tasks in MDC-aware context

## 2.0.B1

* #41 HeartbeatFactory: replace all "Ms" properties with Duration objects

## 1.1

* #37 Too restrictive access permissions for sink file

## 1.0

## 1.0.RC1

* #25 Metrics and thresholds data in healthchecks
* #27 Support for unit of measurement in HealthCheckData
* #28 Cleaning up APIs deprecated since <= 0.25
* #29 DeferredHealthCheck - a health checking proxy for services not available on startup
* #30 Metrics naming conventions
* #31 Aligning health checks names with metrics names 
* #32 MetricsModule uses SafeTxIdGenerator instead of StripedTransactionIdGenerator 
* #33 Support for "active" flag on health checks
* #35 Heartbeat reporting , Nagios format

## 0.25

* #15 Supported reporters are not shown when the app is run with -H
* #16 "business transaction" ids for logs correlation
* #17 Allow to run healthchecks in parallel
* #18 ReporterFactory is not a PolymorphicConfiguration
* #19 Implement "heartbeat" - a scheduler that executes a group of health checks
* #20 Split healthchecks code to a new "bootique-metrics-healthchecks"
* #21 Upgrade to Dropwizard metrics 3.2.5
* #22 HealthCheckRegistry filtering
* #23 Nagios-like health-check statuses
* #24 Add reusable health checks that can read metrics Gauges
* #26 Upgrade to bootique-modules-parent 0.8

## 0.10

* #14 Upgrade to BQ 0.23 

## 0.9

* #10 Move bootique-metrics-web under bootique-jetty repo
* #11 Upgrade to BQ 0.22

## 0.8

* #6 Upgrade to Bootique 0.20
* #7 Reimplement HealthcheckRegistry
* #8 Implement our own healthchecks servlet
* #9 Replace Dropwizard healthchecks with our own

## 0.7

* #1 Rewrite MetricRegistryFactory to use polimorphic reporter config
* #2 Maintain HealthCheckRegistry as an injectable object
* #4 Upgrade to Bootique 0.19
* #5 Move to io.bootique namespace

## 0.5

* Initial framework with basic functionality

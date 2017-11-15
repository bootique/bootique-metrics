## 0.25

* #15 Supported reporters are not shown when the app is run with -H
* #16 "business transaction" ids for logs correlation
* #18 ReporterFactory is not a PolymorphicConfiguration
* #20 Split healthchecks code to a new "bootique-metrics-healthchecks"
* #21 Upgrade to Dropwizard metrics 3.2.5

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

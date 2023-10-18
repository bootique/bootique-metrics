/*
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.bootique.metrics.health.heartbeat;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.metrics.health.HealthCheckRegistry;
import io.bootique.metrics.health.sink.ReportSinkFactory;
import io.bootique.metrics.health.sink.Slf4JReportSyncFactory;
import io.bootique.metrics.health.writer.NagiosReportWriterFactory;
import io.bootique.metrics.health.writer.ReportWriterFactory;
import io.bootique.value.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@BQConfig
public class HeartbeatFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatFactory.class);
    private static final long INITIAL_DELAY_MS_DEFAULT = 60_000L;
    private static final long FIXED_DELAY_MS_DEFAULT = 60_000L;
    private static final long HEALTH_CHECK_TIMEOUT_DEFAULT = 5_000L;

    private Duration initialDelay;
    private Duration fixedDelay;
    private int threadPoolSize;
    private Duration healthCheckTimeout;
    private List<String> healthChecks;
    private ReportSinkFactory sink;
    private ReportWriterFactory writer;

    public Heartbeat createHeartbeat(
            HealthCheckRegistry registry,
            Set<HeartbeatListener> listeners) {

        HealthCheckRegistry filtered = filterRegistry(registry);
        HeartbeatRunner runner = new HeartbeatRunner(
                filtered,
                listeners,
                getInitialDelayMs(),
                getFixedDelayMs(),
                getHealthCheckTimeoutMs(),
                getThreadPoolSize()
        );
        return new Heartbeat(runner);
    }

    public HeartbeatReporter createReporter() {
        return new HeartbeatReporter(
                createSinkFactory().createReportSyncSupplier(),
                createWriterFactory().createReportWriter());
    }

    protected HealthCheckRegistry filterRegistry(HealthCheckRegistry registry) {
        // no explicit health checks means run all available health check...
        if (healthChecks == null || healthChecks.isEmpty()) {
            return registry;
        }

        HealthCheckRegistry filtered = registry.filtered(healthChecks::contains);

        // Report health checks configured in the factory, but missing from the registry.
        // those are likely human errors and a warning is needed.
        reportMissingHealthChecks(filtered);

        return filtered;
    }

    private void reportMissingHealthChecks(HealthCheckRegistry registry) {
        int bad = healthChecks.size() - registry.size();
        if (bad > 0) {
            String badNames = healthChecks.stream()
                    .filter(hc -> !registry.containsHealthCheck(hc))
                    .collect(Collectors.joining(", "));
            LOGGER.warn("The following health check names are invalid and will be ignored: {}", badNames);
        }
    }

    @BQConfigProperty
    public void setHealthChecks(List<String> healthChecks) {
        this.healthChecks = healthChecks;
    }

    @BQConfigProperty
    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    @BQConfigProperty
    public void setFixedDelay(Duration fixedDelay) {
        this.fixedDelay = fixedDelay;
    }

    @BQConfigProperty
    public void setInitialDelay(Duration initialDelay) {
        this.initialDelay = initialDelay;
    }

    @BQConfigProperty
    public void setHealthCheckTimeout(Duration healthCheckTimeout) {
        this.healthCheckTimeout = healthCheckTimeout;
    }

    @BQConfigProperty
    public void setSink(ReportSinkFactory sink) {
        this.sink = sink;
    }

    @BQConfigProperty
    public void setWriter(ReportWriterFactory writer) {
        this.writer = writer;
    }

    protected int getThreadPoolSize() {
        return threadPoolSize > 0 ? threadPoolSize : 2;
    }

    protected long getFixedDelayMs() {
        return fixedDelay != null ? fixedDelay.getDuration().toMillis() : FIXED_DELAY_MS_DEFAULT;
    }

    protected long getInitialDelayMs() {
        return initialDelay != null ? initialDelay.getDuration().toMillis() : INITIAL_DELAY_MS_DEFAULT;
    }

    protected long getHealthCheckTimeoutMs() {
        return healthCheckTimeout != null ? healthCheckTimeout.getDuration().toMillis() : HEALTH_CHECK_TIMEOUT_DEFAULT;
    }

    protected ReportSinkFactory createSinkFactory() {
        return sink != null ? sink : new Slf4JReportSyncFactory();
    }

    protected ReportWriterFactory createWriterFactory() {
        return writer != null ? writer : new NagiosReportWriterFactory();
    }
}

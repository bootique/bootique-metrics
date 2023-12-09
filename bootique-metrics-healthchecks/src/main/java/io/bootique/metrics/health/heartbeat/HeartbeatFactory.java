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
import io.bootique.metrics.health.HealthCheck;
import io.bootique.metrics.health.HealthCheckRegistry;
import io.bootique.metrics.health.sink.ReportSinkFactory;
import io.bootique.metrics.health.sink.Slf4JReportSyncFactory;
import io.bootique.metrics.health.writer.NagiosReportWriterFactory;
import io.bootique.metrics.health.writer.ReportWriterFactory;
import io.bootique.shutdown.ShutdownManager;
import io.bootique.value.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.*;

@BQConfig
public class HeartbeatFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatFactory.class);
    private static final long INITIAL_DELAY_MS_DEFAULT = 60_000L;
    private static final long FIXED_DELAY_MS_DEFAULT = 60_000L;
    private static final long HEALTH_CHECK_TIMEOUT_DEFAULT = 5_000L;

    private final HealthCheckRegistry registry;
    // since HeartbeatReporter created by this factory is implemented as a listener, injecting listeners via Provider
    // for lazy initialization
    private final Provider<Set<HeartbeatListener>> listeners;
    private final ShutdownManager shutdownManager;

    private Duration initialDelay;
    private Duration fixedDelay;
    private int threadPoolSize;
    private Duration healthCheckTimeout;
    @Deprecated(forRemoval = true)
    private List<String> healthChecks;
    private Map<String, HeartbeatHealthCheckFactory> checks;
    private ReportSinkFactory sink;
    private ReportWriterFactory writer;

    @Inject
    public HeartbeatFactory(
            HealthCheckRegistry registry,
            Provider<Set<HeartbeatListener>> listeners,
            ShutdownManager shutdownManager) {
        this.registry = registry;
        this.listeners = listeners;
        this.shutdownManager = shutdownManager;
    }

    public Heartbeat createHeartbeat() {

        HealthCheckRegistry heartbeatRegistry = heartbeatRegistry();
        HeartbeatRunner runner = new HeartbeatRunner(
                heartbeatRegistry,
                listeners,
                getInitialDelayMs(),
                getFixedDelayMs(),
                getHealthCheckTimeoutMs(),
                getThreadPoolSize()
        );

        return shutdownManager.onShutdown(new Heartbeat(runner), Heartbeat::stop);
    }

    public HeartbeatReporter createReporter() {
        return new HeartbeatReporter(
                createSinkFactory().createReportSyncSupplier(),
                createWriterFactory().createReportWriter());
    }

    protected HealthCheckRegistry heartbeatRegistry() {

        // no explicit health checks means run all available health checks...
        if ((checks == null || checks.isEmpty()) && (healthChecks == null || healthChecks.isEmpty())) {
            return registry;
        }

        // for heartbeat, create a registry with a subset of checks
        Map<String, HealthCheck> checks = new HashMap<>();
        getCheckFactories(registry).forEach((k, v) -> checks.put(k, v.createHeartbeatCheck(k, registry)));
        return new HealthCheckRegistry(checks);
    }

    private Map<String, HeartbeatHealthCheckFactory> getCheckFactories(HealthCheckRegistry registry) {

        // filter valid, report invalid (due to user errors)
        Map<String, HeartbeatHealthCheckFactory> result = new HashMap<>();
        Set<String> badNames = new HashSet<>();

        if (checks != null && !checks.isEmpty()) {
            for (Map.Entry<String, HeartbeatHealthCheckFactory> e : checks.entrySet()) {
                if (registry.containsHealthCheck(e.getKey())) {
                    result.put(e.getKey(), e.getValue());
                } else {
                    badNames.add(e.getKey());
                }
            }
        }

        // merge "checks" with deprecated "healthChecks"
        if (healthChecks != null && !healthChecks.isEmpty()) {

            LOGGER.warn("The use of 'heartbeat.healthChecks' configuration is deprecated. Use 'heartbeat.checks' map instead.");

            for (String check : healthChecks) {
                if (registry.containsHealthCheck(check)) {
                    result.put(check, new HeartbeatHealthCheckFactory());
                } else {
                    badNames.add(check);
                }
            }
        }

        // report missing checks
        if (!badNames.isEmpty()) {
            LOGGER.warn("The following health check name(s) are invalid and will be ignored: {}", String.join(", ", badNames));
        }

        return result;
    }

    @Deprecated(since = "3.0", forRemoval = true)
    @BQConfigProperty("Deprecated since 3.0 in favor of the 'checks' map that allows to specify extra properties of heartbeat health checks")
    public void setHealthChecks(List<String> healthChecks) {
        this.healthChecks = healthChecks;
    }

    @BQConfigProperty("Configures health checks to be included in heartbeat. If omitted, all known health checks will be used")
    public void setChecks(Map<String, HeartbeatHealthCheckFactory> checks) {
        this.checks = checks;
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

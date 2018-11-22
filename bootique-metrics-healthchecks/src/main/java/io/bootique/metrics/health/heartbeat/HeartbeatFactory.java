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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@BQConfig
public class HeartbeatFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatFactory.class);

    private long initialDelayMs;
    private long fixedDelayMs;
    private int threadPoolSize;
    private long healthCheckTimeoutMs;
    private List<String> healthChecks;
    private ReportSinkFactory sink;
    private ReportWriterFactory writer;

    @BQConfigProperty
    public void setHealthChecks(List<String> healthChecks) {
        this.healthChecks = healthChecks;
    }

    public Heartbeat createHeartbeat(
            HealthCheckRegistry registry,
            Set<HeartbeatListener> listeners) {

        HealthCheckRegistry filtered = filterRegistry(registry);
        return new Heartbeat(() -> startHeartbeat(filtered, listeners));
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

    protected Runnable startHeartbeat(
            HealthCheckRegistry healthChecks,
            Set<HeartbeatListener> listeners) {

        ExecutorService threadPool = startThreadPool();
        Timer timer = new HeartbeatLauncher(healthChecks)
                .initialDelayMs(getInitialDelayMs())
                .fixedDelayMs(getFixedDelayMs())
                .healthCheckTimeoutMs(getHealthCheckTimeoutMs())
                .listeners(listeners)
                .threadPool(threadPool)
                .start();

        return () -> {

            try {
                timer.cancel();
            } catch (Throwable th) {
            }

            try {
                threadPool.shutdownNow();
            } catch (Throwable th) {
            }
        };
    }

    protected ExecutorService startThreadPool() {
        return Executors.newFixedThreadPool(getThreadPoolSize(), new HealthCheckThreadFactory());
    }

    protected int getThreadPoolSize() {
        return threadPoolSize > 0 ? threadPoolSize : 2;
    }

    @BQConfigProperty
    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    protected long getFixedDelayMs() {
        return fixedDelayMs > 0 ? fixedDelayMs : HeartbeatLauncher.FIXED_DELAY_MS_DEFAULT;
    }

    @BQConfigProperty
    public void setFixedDelayMs(long fixedDelayMs) {
        this.fixedDelayMs = fixedDelayMs;
    }

    protected long getInitialDelayMs() {
        return initialDelayMs >= 0 ? initialDelayMs : HeartbeatLauncher.INITIAL_DELAY_MS_DEFAULT;
    }

    @BQConfigProperty
    public void setInitialDelayMs(long initialDelayMs) {
        this.initialDelayMs = initialDelayMs;
    }

    protected long getHealthCheckTimeoutMs() {
        return healthCheckTimeoutMs > 0 ? healthCheckTimeoutMs : HeartbeatLauncher.HEALTH_CHECK_TIMEOUT_DEFAULT;
    }

    @BQConfigProperty
    public void setHealthCheckTimeoutMs(long healthCheckTimeoutMs) {
        this.healthCheckTimeoutMs = healthCheckTimeoutMs;
    }

    @BQConfigProperty
    public void setSink(ReportSinkFactory sink) {
        this.sink = sink;
    }

    @BQConfigProperty
    public void setWriter(ReportWriterFactory writer) {
        this.writer = writer;
    }

    protected ReportSinkFactory createSinkFactory() {
        return sink != null ? sink : new Slf4JReportSyncFactory();
    }

    protected ReportWriterFactory createWriterFactory() {
        return writer != null ? writer : new NagiosReportWriterFactory();
    }

    private static class HealthCheckThreadFactory implements ThreadFactory {

        private AtomicInteger counter = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("bootique-healthcheck-" + counter.getAndIncrement());
            t.setDaemon(true);
            return t;
        }
    }
}

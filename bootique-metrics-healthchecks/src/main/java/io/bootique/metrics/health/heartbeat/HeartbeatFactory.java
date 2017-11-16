package io.bootique.metrics.health.heartbeat;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.metrics.health.HealthCheckRegistry;

import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@BQConfig
public class HeartbeatFactory {

    private long initialDelayMs;
    private long fixedDelayMs;
    private int threadPoolSize;
    private long healthCheckTimeoutMs;
    private List<String> healthChecks;

    @BQConfigProperty
    public void setHealthChecks(List<String> healthChecks) {
        this.healthChecks = healthChecks;
    }

    public Heartbeat createHeartbeat(HealthCheckRegistry registry, Set<HeartbeatListener> listeners) {
        HealthCheckRegistry filtered = filterRegistry(registry);
        return new Heartbeat(() -> startHeartbeat(filtered, listeners));
    }

    protected HealthCheckRegistry filterRegistry(HealthCheckRegistry registry) {
        // no explicit health checks means run all available health check...
        return healthChecks.isEmpty() ? registry : registry.filtered(healthChecks::contains);
    }

    protected Timer startHeartbeat(HealthCheckRegistry healthChecks, Set<HeartbeatListener> listeners) {

        return new HeartbeatLauncher(healthChecks)
                .initialDelayMs(getInitialDelayMs())
                .fixedDelayMs(getFixedDelayMs())
                .healthCheckTimeoutMs(getHealthCheckTimeoutMs())
                .listeners(listeners)
                .threadPool(startThreadPool())
                .start();
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
        return healthCheckTimeoutMs >= 0 ? healthCheckTimeoutMs : HeartbeatLauncher.HEALTH_CHECK_TIMEOUT_DEFAULT;
    }

    @BQConfigProperty
    public void setHealthCheckTimeoutMs(long healthCheckTimeoutMs) {
        this.healthCheckTimeoutMs = healthCheckTimeoutMs;
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

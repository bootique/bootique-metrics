package io.bootique.metrics.health.heartbeat;

import io.bootique.metrics.health.HealthCheckOutcome;
import io.bootique.metrics.health.HealthCheckRegistry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * A builder of a Timer object that runs periodic health checks that constitute an application "heartbeat".
 *
 * @since 0.25
 */
public class HeartbeatLauncher {

    static final long INITIAL_DELAY_MS_DEFAULT = 60_000L;
    static final long FIXED_DELAY_MS_DEFAULT = 60_000L;
    static final long HEALTH_CHECK_TIMEOUT_DEFAULT = 5_000L;

    private long initialDelayMs;
    private long fixedDelayMs;
    private ExecutorService threadPool;
    private long healthCheckTimeoutMs;
    private Set<HeartbeatListener> listeners;
    private HealthCheckRegistry heartbeatChecks;

    public HeartbeatLauncher(HealthCheckRegistry heartbeatChecks) {
        this.heartbeatChecks = Objects.requireNonNull(heartbeatChecks);
        this.initialDelayMs = INITIAL_DELAY_MS_DEFAULT;
        this.fixedDelayMs = FIXED_DELAY_MS_DEFAULT;
        this.healthCheckTimeoutMs = HEALTH_CHECK_TIMEOUT_DEFAULT;
        this.threadPool = ForkJoinPool.commonPool();
        this.listeners = new HashSet<>();
    }

    public HeartbeatLauncher initialDelayMs(long delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("Initial delay can't be negative");
        }

        this.initialDelayMs = delay;
        return this;
    }

    public HeartbeatLauncher fixedDelayMs(long delay) {
        if (delay <= 0) {
            throw new IllegalArgumentException("Delay between heartbeats must be positive");
        }

        this.fixedDelayMs = delay;
        return this;
    }

    public HeartbeatLauncher healthCheckTimeoutMs(long timeout) {
        if (timeout <= 0) {
            throw new IllegalArgumentException("Healthcheck timeout must be positive");
        }

        this.healthCheckTimeoutMs = timeout;
        return this;
    }

    public HeartbeatLauncher threadPool(ExecutorService threadPool) {
        this.threadPool = Objects.requireNonNull(threadPool);
        return this;
    }

    public HeartbeatLauncher listener(HeartbeatListener listener) {
        this.listeners.add(Objects.requireNonNull(listener));
        return this;
    }

    public HeartbeatLauncher listeners(Collection<? extends HeartbeatListener> listeners) {
        listeners.forEach(this::listener);
        return this;
    }

    /**
     * Starts a heartbeat timer based on the provided builder parameters.
     */
    public Timer start() {
        TimerTask heartbeat = createHeartbeatTask();

        Timer heartbeatTimer = new Timer("bootique-heartbeat", true);
        heartbeatTimer.schedule(heartbeat, initialDelayMs, fixedDelayMs);
        return heartbeatTimer;
    }

    protected TimerTask createHeartbeatTask() {
        return new HeartbeatTask(createHeartbeatAction(), listeners);
    }

    protected Supplier<Map<String, HealthCheckOutcome>> createHeartbeatAction() {
        return () -> heartbeatChecks
                .runHealthChecks(threadPool, healthCheckTimeoutMs, TimeUnit.MILLISECONDS);
    }
}

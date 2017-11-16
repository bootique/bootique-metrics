package io.bootique.metrics.health.heartbeat;

import io.bootique.metrics.health.HealthCheckOutcome;
import io.bootique.metrics.health.HealthCheckRegistry;

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
public class Heartbeat {

    private long initialDelayMs;
    private long fixedDelayMs;
    private ExecutorService threadPool;
    private long healthCheckTimeoutMs;
    private Set<HeartbeatListener> listeners;
    private HealthCheckRegistry heartbeatChecks;

    protected Heartbeat(HealthCheckRegistry heartbeatChecks) {
        this.heartbeatChecks = Objects.requireNonNull(heartbeatChecks);
        this.initialDelayMs = 60_000L;
        this.fixedDelayMs = 60_000L;
        this.healthCheckTimeoutMs = 5_000L;
        this.threadPool = ForkJoinPool.commonPool();
        this.listeners = new HashSet<>();
    }

    public static Heartbeat builder(HealthCheckRegistry heartbeatChecks) {
        return new Heartbeat(heartbeatChecks);
    }

    public Heartbeat initialDelayMs(long delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("Initial delay can't be negative");
        }

        this.initialDelayMs = delay;
        return this;
    }

    public Heartbeat fixedDelayMs(long delay) {
        if (delay <= 0) {
            throw new IllegalArgumentException("Delay between heartbeats must be positive");
        }

        this.fixedDelayMs = delay;
        return this;
    }

    public Heartbeat healthCheckTimeoutMs(long timeout) {
        if (timeout <= 0) {
            throw new IllegalArgumentException("Healthcheck timeout must be positive");
        }

        this.healthCheckTimeoutMs = timeout;
        return this;
    }

    public Heartbeat threadPool(ExecutorService threadPool) {
        this.threadPool = Objects.requireNonNull(threadPool);
        return this;
    }

    public Heartbeat listener(HeartbeatListener listener) {
        this.listeners.add(Objects.requireNonNull(listener));
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

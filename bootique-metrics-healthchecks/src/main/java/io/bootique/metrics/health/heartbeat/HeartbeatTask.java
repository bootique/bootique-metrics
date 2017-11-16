package io.bootique.metrics.health.heartbeat;

import io.bootique.metrics.health.HealthCheckOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.function.Supplier;

/**
 * A {@link TimerTask} that executes heartbeat actions and notifies listeners of the results.
 *
 * @since 0.25
 */
public class HeartbeatTask extends TimerTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatTask.class);

    private Supplier<Map<String, HealthCheckOutcome>> heartbeatAction;
    private Set<HeartbeatListener> listeners;

    public HeartbeatTask(Supplier<Map<String, HealthCheckOutcome>> heartbeatAction, Set<HeartbeatListener> listeners) {
        this.heartbeatAction = heartbeatAction;
        this.listeners = listeners;
    }

    @Override
    public void run() {
        Map<String, HealthCheckOutcome> result = heartbeatAction.get();
        listeners.forEach(l -> notifyListener(l, result));
    }

    protected void notifyListener(HeartbeatListener listener, Map<String, HealthCheckOutcome> result) {
        try {
            listener.healthChecksFinished(result);
        } catch (Throwable th) {
            LOGGER.error("Error processing health check results", th);
        }
    }
}

package io.bootique.metrics.health.heartbeat;

import java.util.Timer;
import java.util.function.Supplier;

/**
 * Startable heartbeat object.
 *
 * @since 0.25
 */
public class Heartbeat {

    private final Supplier<Timer> heartbeatStarter;
    Timer heartbeatTimer;

    public Heartbeat(Supplier<Timer> heartbeatStarter) {
        this.heartbeatStarter = heartbeatStarter;
    }

    public void start() {

        // sanity check, but otherwise don't bother with startup race conditions, as Bootique infrastructure will
        // ensure single-threaded start
        if (heartbeatTimer != null) {
            throw new IllegalStateException("Heartbeat is already running.");
        }

        this.heartbeatTimer = heartbeatStarter.get();
    }

    public void stop() {
        Timer local = this.heartbeatTimer;
        if (local != null) {
            this.heartbeatTimer = null;
            local.cancel();
        }
    }
}

package io.bootique.metrics.health.heartbeat;

import java.util.function.Supplier;

/**
 * Startable heartbeat object.
 *
 * @since 0.25
 */
public class Heartbeat {

    private final Supplier<Runnable> heartbeatStarter;
    Runnable heartbeatStopper;

    public Heartbeat(Supplier<Runnable> heartbeatStarter) {
        this.heartbeatStarter = heartbeatStarter;
    }

    public void start() {

        // sanity check, but otherwise don't bother with startup race conditions, as Bootique infrastructure will
        // ensure single-threaded start
        if (heartbeatStopper != null) {
            throw new IllegalStateException("Heartbeat is already running.");
        }

        this.heartbeatStopper = heartbeatStarter.get();
    }

    public void stop() {
        Runnable local = this.heartbeatStopper;
        if (local != null) {
            this.heartbeatStopper = null;
            local.run();
        }
    }
}

package io.bootique.metrics.health.check;

import io.bootique.metrics.health.HealthCheck;
import io.bootique.metrics.health.HealthCheckOutcome;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * A {@link io.bootique.metrics.health.HealthCheck} proxy that returns unknown status until its internal supplier
 * provides a non-null HealthCheck. At that point the health check is considered "activated", and will be
 * running the real underlying check. This allows to pre-register and execute health checks for services that are
 * not yet started.
 *
 * @since 0.26
 */
public class DeferredHealthCheck implements HealthCheck {

    private static final HealthCheckOutcome UNKNOWN_OUTCOME = HealthCheckOutcome.unknown();
    private static final HealthCheck FALLBACK_CHECK = () -> UNKNOWN_OUTCOME;

    private Supplier<Optional<HealthCheck>> delegateSupplier;
    private volatile Runnable delegateUpdater;
    private volatile HealthCheck delegate;

    public DeferredHealthCheck(Supplier<Optional<HealthCheck>> delegateSupplier) {
        this.delegate = FALLBACK_CHECK;
        this.delegateSupplier = delegateSupplier;
        this.delegateUpdater = this::checkActivation;
    }

    @Override
    public HealthCheckOutcome check() throws Exception {
        delegateUpdater.run();
        return delegate.check();
    }

    private void checkActivation() {

        // I guess the same health check should not be executed in parallel, so this synchronization is just for
        // internal consistency and should not have much impact on performance.
        // And once we are activated, this method is no longer called.
        synchronized (this) {
            Optional<HealthCheck> maybeRealHC = delegateSupplier.get();
            if (maybeRealHC.isPresent()) {
                this.delegate = maybeRealHC.get();
                this.delegateUpdater = () -> {
                };
            }
        }
    }
}

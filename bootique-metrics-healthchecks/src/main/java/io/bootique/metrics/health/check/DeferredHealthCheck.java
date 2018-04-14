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

    private Supplier<Optional<HealthCheck>> maybeDelegateSupplier;
    private volatile Supplier<HealthCheck> delegateSupplier;

    public DeferredHealthCheck(Supplier<Optional<HealthCheck>> maybeDelegateSupplier) {
        this.maybeDelegateSupplier = maybeDelegateSupplier;
        this.delegateSupplier = this::checkActivation;
    }

    @Override
    public HealthCheckOutcome check() throws Exception {
        return delegateSupplier.get().check();
    }

    private HealthCheck checkActivation() {
        
        Optional<HealthCheck> maybeDelegate = maybeDelegateSupplier.get();
        if (maybeDelegate.isPresent()) {
            HealthCheck delegate = maybeDelegate.get();
            this.delegateSupplier = () -> delegate;
            return delegate;
        }

        return FALLBACK_CHECK;
    }
}

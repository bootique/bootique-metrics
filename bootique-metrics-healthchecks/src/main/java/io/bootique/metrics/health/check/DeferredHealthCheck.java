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

    private static final HealthCheck INACTIVE_CHECK = new InactiveCheck();

    private Supplier<Optional<HealthCheck>> maybeDelegateSupplier;
    private volatile Supplier<HealthCheck> delegateSupplier;

    public DeferredHealthCheck(Supplier<Optional<HealthCheck>> maybeDelegateSupplier) {
        this.maybeDelegateSupplier = maybeDelegateSupplier;
        this.delegateSupplier = this::checkActivation;
    }

    @Override
    public boolean isActive() {
        return delegateSupplier.get().isActive();
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

        // returning inactive health check results in the checking being skipped...
        return INACTIVE_CHECK;
    }

    static class InactiveCheck implements HealthCheck {
        private static final HealthCheckOutcome UNKNOWN_OUTCOME = HealthCheckOutcome.unknown();

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public HealthCheckOutcome check() {
            return UNKNOWN_OUTCOME;
        }
    }
}

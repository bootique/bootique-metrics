package io.bootique.metrics.health;

/**
 * Represents a single system health check.
 *
 * @since 0.8
 */
public interface HealthCheck {

    /**
     * Runs this health check, potentially throwing an exception on bad outcomes.
     *
     * @return outcome of the health check.
     * @throws Exception any exception that might have occurred when performing the health check.
     */
    HealthCheckOutcome check() throws Exception;

    /**
     * Returns whether this check should be executed during a registry health check run.
     *
     * @return this implementation always returns true.
     * @since 0.26
     */
    default boolean isActive() {
        return true;
    }

    /**
     * Runs this health check, catching all exceptions, turning them into unhealthy outcomes.
     *
     * @return outcome of the health check.
     */
    default HealthCheckOutcome safeCheck() {
        try {
            return check();
        } catch (Throwable th) {
            return HealthCheckOutcome.critical(th);
        }
    }
}

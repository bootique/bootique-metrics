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
     * Runs this health check, catching all exceptions, turning them into unhealthy outcomes.
     *
     * @return outcome of the health check.
     */
    default HealthCheckOutcome safeCheck() {
        try {
            return check();
        } catch (Throwable th) {
            return HealthCheckOutcome.unhealthy(th);
        }
    }
}

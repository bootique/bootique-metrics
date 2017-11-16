package io.bootique.metrics.health;

/**
 * @since 0.8
 */
public class HealthCheckOutcome {

    private static final HealthCheckOutcome HEALTHY = new HealthCheckOutcome(true, null, null);

    private boolean healthy;
    private String message;
    private Throwable error;

    private HealthCheckOutcome(boolean healthy, String message, Throwable error) {
        this.healthy = healthy;
        this.message = message;
        this.error = error;
    }

    public static HealthCheckOutcome healthy() {
        return HEALTHY;
    }

    public static HealthCheckOutcome healthy(String message) {
        return new HealthCheckOutcome(true, message, null);
    }

    public static HealthCheckOutcome unhealthy(String message) {
        return new HealthCheckOutcome(false, message, null);
    }

    public static HealthCheckOutcome unhealthy(Throwable th) {
        return new HealthCheckOutcome(false, th.getMessage(), th);
    }

    public boolean isHealthy() {
        return healthy;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getError() {
        return error;
    }

    @Override
    public String toString() {

        if (healthy) {
            return "[healthy]";
        }

        String message = this.message;

        if (message == null && error != null) {
            message = error.getMessage();
        }

        if (message == null) {
            return "[unhealthy]";
        }

        return "[unhealthy: " + message + "]";
    }
}

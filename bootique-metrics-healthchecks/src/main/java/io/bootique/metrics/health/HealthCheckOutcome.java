package io.bootique.metrics.health;

/**
 * @since 0.8
 */
public class HealthCheckOutcome implements Comparable<HealthCheckOutcome> {

    private static final HealthCheckOutcome OK = new HealthCheckOutcome(HealthCheckStatus.OK, null, null);

    private HealthCheckStatus status;
    private String message;
    private Throwable error;

    private HealthCheckOutcome(HealthCheckStatus status, String message, Throwable error) {
        this.status = status;
        this.message = message;
        this.error = error;
    }

    /**
     * @return a {@link HealthCheckOutcome} corresponding to a healthy state of the app.
     * @since 0.25
     */
    public static HealthCheckOutcome ok() {
        return OK;
    }

    /**
     * @return a {@link HealthCheckOutcome} corresponding to a healthy state of the app.
     * @since 0.25
     */
    public static HealthCheckOutcome ok(String message) {
        return new HealthCheckOutcome(HealthCheckStatus.OK, message, null);
    }

    /**
     * @return a {@link HealthCheckOutcome} corresponding to a "warning" state of the app, i.e. approaching critical.
     * @since 0.25
     */
    public static HealthCheckOutcome warning() {
        return warning(null);
    }

    /**
     * @return a {@link HealthCheckOutcome} corresponding to a "warning" state of the app, i.e. approaching critical.
     * @since 0.25
     */
    public static HealthCheckOutcome warning(String message) {
        return new HealthCheckOutcome(HealthCheckStatus.WARNING, message, null);
    }

    /**
     * @return a {@link HealthCheckOutcome} corresponding to a critical state of the app.
     * @since 0.25
     */
    public static HealthCheckOutcome critical() {
        return critical((String) null);
    }

    /**
     * @return a {@link HealthCheckOutcome} corresponding to a critical state of the app.
     * @since 0.25
     */
    public static HealthCheckOutcome critical(String message) {
        return new HealthCheckOutcome(HealthCheckStatus.CRITICAL, message, null);
    }

    /**
     * @return a {@link HealthCheckOutcome} corresponding to a critical state of the app.
     * @since 0.25
     */
    public static HealthCheckOutcome critical(Throwable th) {
        return new HealthCheckOutcome(HealthCheckStatus.CRITICAL, null, th);
    }

    /**
     * @return a {@link HealthCheckOutcome} corresponding to a state of the app that can not be asserted.
     * @since 0.25
     */
    public static HealthCheckOutcome unknown() {
        return unknown((String) null);
    }

    /**
     * @return a {@link HealthCheckOutcome} corresponding to a state of the app that can not be asserted.
     * @since 0.25
     */
    public static HealthCheckOutcome unknown(String message) {
        return new HealthCheckOutcome(HealthCheckStatus.UNKNOWN, message, null);
    }

    /**
     * @return a {@link HealthCheckOutcome} corresponding to a state of the app that can not be asserted.
     * @since 0.25
     */
    public static HealthCheckOutcome unknown(Throwable th) {
        return new HealthCheckOutcome(HealthCheckStatus.UNKNOWN, null, th);
    }

    /**
     * @return a {@link HealthCheckOutcome} with provided parameters.
     * @since 0.25
     */
    public static HealthCheckOutcome outcome(HealthCheckStatus status, String message, Throwable th) {
        return new HealthCheckOutcome(status, message, th);
    }

    /**
     * @return a {@link HealthCheckOutcome} corresponding to the healthy state of the app.
     * @deprecated since 0.25 in favor of {@link #ok()}.
     */
    @Deprecated
    public static HealthCheckOutcome healthy() {
        return ok();
    }

    /**
     * @return a {@link HealthCheckOutcome} corresponding to the healthy state of the app.
     * @deprecated since 0.25 in favor of {@link #ok(String)}.
     */
    @Deprecated
    public static HealthCheckOutcome healthy(String message) {
        return ok(message);
    }

    /**
     * @return a {@link HealthCheckOutcome} corresponding to the critical state of the app.
     * @deprecated since 0.25 in favor of {@link #critical(String)}.
     */
    @Deprecated
    public static HealthCheckOutcome unhealthy(String message) {
        return HealthCheckOutcome.critical(message);
    }

    /**
     * @return a {@link HealthCheckOutcome} corresponding to the critical state of the app.
     * @deprecated since 0.25 in favor of {@link #critical(Throwable)}.
     */
    @Deprecated
    public static HealthCheckOutcome unhealthy(Throwable th) {
        return new HealthCheckOutcome(HealthCheckStatus.CRITICAL, th.getMessage(), th);
    }

    /**
     * Compares this and another outcome by severity. Less severe outcomes are ordered prior to more severe ones.
     *
     * @param o another outcome to compare with.
     * @return an int according to {@link Comparable} contract.
     */
    @Override
    public int compareTo(HealthCheckOutcome o) {
        return status.getSeverity() - o.status.getSeverity();
    }

    /**
     * @return a boolean indicating whether the health check was fully successful.
     * @deprecated since 0.25 in favor of {@link #getStatus()}.
     */
    @Deprecated
    public boolean isHealthy() {
        return status == HealthCheckStatus.OK;
    }

    public HealthCheckStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getError() {
        return error;
    }

    @Override
    public String toString() {

        String message = this.message;

        if (message == null && error != null) {
            message = error.getMessage();
        }

        StringBuilder buffer = new StringBuilder().append("[").append(status.name());
        if (message != null) {
            buffer.append(": ").append(message);
        }

        return buffer.append("]").toString();
    }
}

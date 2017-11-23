package io.bootique.metrics.health;

/**
 * Defines the status of a health check. For simpler integration with monitoring tools, the status names and meanings
 * are taken from the
 * <a href="https://assets.nagios.com/downloads/nagioscore/docs/nagioscore/3/en/pluginapi.html">Nagios plugin spec</a>,
 * and their ordinals correspond to the Nagios plugin return codes.
 *
 * @since 0.25
 */
public enum HealthCheckStatus {
    OK(0), WARNING(1), CRITICAL(3), UNKNOWN(2);
    
    private int severity;

    HealthCheckStatus(int severity) {
        this.severity = severity;
    }

    /**
     * Returns the status severity. While the status "ordinal()" is used to match Nagios return code, severity is a
     * logical value that is similar, but does not fully match the ordinal.
     *
     * @return
     */
    public int getSeverity() {
        return severity;
    }
}


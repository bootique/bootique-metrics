package io.bootique.metrics.health;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HealthCheckStatusTest {

    @Test
    public void testMatchesNagiosCodes() {
        // health checks must follow Nagios plugin spec for names and return codes
        // from https://assets.nagios.com/downloads/nagioscore/docs/nagioscore/3/en/pluginapi.html
        assertEquals(0, HealthCheckStatus.OK.ordinal());
        assertEquals(1, HealthCheckStatus.WARNING.ordinal());
        assertEquals(2, HealthCheckStatus.CRITICAL.ordinal());
        assertEquals(3, HealthCheckStatus.UNKNOWN.ordinal());
    }
}

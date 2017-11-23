package io.bootique.metrics.health;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class HealthCheckOutcomeTest {

    @Test
    public void testCompareTo() {
        HealthCheckOutcome o1 = HealthCheckOutcome.outcome(HealthCheckStatus.OK, "C", null);
        HealthCheckOutcome o2 = HealthCheckOutcome.outcome(HealthCheckStatus.WARNING, "B", null);
        HealthCheckOutcome o3 = HealthCheckOutcome.outcome(HealthCheckStatus.CRITICAL, "D", null);
        HealthCheckOutcome o4 = HealthCheckOutcome.outcome(HealthCheckStatus.UNKNOWN, "A", null);

        assertTrue(o1.compareTo(o2) < 0);
        assertTrue(o2.compareTo(o3) < 0);
        assertTrue(o3.compareTo(o4) > 0);
        assertTrue(o2.compareTo(o4) < 0);
    }
}

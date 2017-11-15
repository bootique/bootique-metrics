package io.bootique.health;

import io.bootique.BQRuntime;
import io.bootique.metrics.health.HealthCheck;
import io.bootique.metrics.health.HealthCheckModule;
import io.bootique.metrics.health.HealthCheckOutcome;
import io.bootique.metrics.health.HealthCheckRegistry;
import io.bootique.test.junit.BQTestFactory;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HealthCheckModuleIT {

    @Rule
    public final BQTestFactory testFactory = new BQTestFactory();


    @Test
    public void testHealthcheckRegistry() {

        BQRuntime runtime = testFactory.app().autoLoadModules().createRuntime();

        HealthCheckRegistry r1 = runtime.getInstance(HealthCheckRegistry.class);
        HealthCheckRegistry r2 = runtime.getInstance(HealthCheckRegistry.class);
        assertNotNull(r1);
        assertSame("HealthCheckRegistry must be a singleton", r1, r2);
    }


    @Test
    public void testHealthcheckRegistry_Contributions() {

        HealthCheckOutcome hcr = mock(HealthCheckOutcome.class);
        HealthCheck hc = mock(HealthCheck.class);
        when(hc.safeCheck()).thenReturn(hcr);

        BQRuntime runtime = testFactory
                .app()
                .module(HealthCheckModule.class)
                .module(b -> HealthCheckModule.extend(b).addHealthCheck("x", hc))
                .createRuntime();

        HealthCheckRegistry r = runtime.getInstance(HealthCheckRegistry.class);
        assertSame(hcr, r.runHealthCheck("x"));
    }
}

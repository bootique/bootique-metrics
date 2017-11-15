package io.bootique.health;

import io.bootique.metrics.health.HealthCheckModuleProvider;
import io.bootique.test.junit.BQModuleProviderChecker;
import org.junit.Test;

public class HealthCheckModuleProviderIT {

    @Test
    public void testAutoLoadable() {
        BQModuleProviderChecker.testPresentInJar(HealthCheckModuleProvider.class);
    }
}

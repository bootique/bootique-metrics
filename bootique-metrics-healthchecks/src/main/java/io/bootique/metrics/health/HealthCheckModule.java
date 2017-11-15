package io.bootique.metrics.health;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @since 0.25
 */
public class HealthCheckModule implements Module {

    /**
     * Returns an instance of {@link HealthCheckModuleExtender} used by downstream modules to load custom extensions for
     * the HealthCheckModule. Should be invoked from a downstream Module's "configure" method.
     *
     * @param binder DI binder passed to the Module that invokes this method.
     * @return an instance of {@link HealthCheckModuleExtender} that can be used to load HealthCheckModule custom extensions.
     * @since 0.9
     */
    public static HealthCheckModuleExtender extend(Binder binder) {
        return new HealthCheckModuleExtender(binder);
    }

    @Override
    public void configure(Binder binder) {
        extend(binder).initAllExtensions();
    }

    @Provides
    @Singleton
    HealthCheckRegistry provideHealthCheckRegistry(Map<String, HealthCheck> healthChecks, Set<HealthCheckGroup> groups) {
        Map<String, HealthCheck> checks = new HashMap<>(healthChecks);
        groups.forEach(g -> checks.putAll(g.getHealthChecks()));
        return new HealthCheckRegistry(checks);
    }
}

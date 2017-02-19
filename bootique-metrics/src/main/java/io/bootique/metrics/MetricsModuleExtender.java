package io.bootique.metrics;

import com.google.inject.Binder;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import io.bootique.metrics.health.HealthCheck;
import io.bootique.metrics.health.HealthCheckGroup;

/**
 * @since 0.9
 */
public class MetricsModuleExtender {

    private Binder binder;

    private MapBinder<String, HealthCheck> healthChecks;
    private Multibinder<HealthCheckGroup> healthCheckGroups;

    MetricsModuleExtender(Binder binder) {
        this.binder = binder;
    }

    /**
     * Should be called by owning Module to initialize all contribution maps and collections. Failure to call this
     * method may result in injection failures for empty maps and collections.
     *
     * @return this extender instance.
     */
    MetricsModuleExtender initAllExtensions() {
        getOrCreateHealthCheckGroupsBinder();
        getOrCreateHealthChecksBinder();

        return this;
    }

    public MetricsModuleExtender addHealthCheck(String name, HealthCheck healthCheck) {
        getOrCreateHealthChecksBinder().addBinding(name).toInstance(healthCheck);
        return this;
    }

    public <T extends HealthCheck> MetricsModuleExtender addHealthCheck(String name, Class<T> healthCheckType) {
        getOrCreateHealthChecksBinder().addBinding(name).to(healthCheckType);
        return this;
    }

    public MetricsModuleExtender addHealthCheckGroup(HealthCheckGroup healthCheckGroup) {
        getOrCreateHealthCheckGroupsBinder().addBinding().toInstance(healthCheckGroup);
        return this;
    }

    public <T extends HealthCheckGroup> MetricsModuleExtender addHealthCheckGroup(Class<T> healthCheckGroupType) {
        getOrCreateHealthCheckGroupsBinder().addBinding().to(healthCheckGroupType);
        return this;
    }

    protected MapBinder<String, HealthCheck> getOrCreateHealthChecksBinder() {
        if (healthChecks == null) {
            healthChecks = MapBinder.newMapBinder(binder, String.class, HealthCheck.class);
        }

        return healthChecks;
    }

    protected Multibinder<HealthCheckGroup> getOrCreateHealthCheckGroupsBinder() {
        if (healthCheckGroups == null) {
            healthCheckGroups = Multibinder.newSetBinder(binder, HealthCheckGroup.class);
        }

        return healthCheckGroups;
    }
}

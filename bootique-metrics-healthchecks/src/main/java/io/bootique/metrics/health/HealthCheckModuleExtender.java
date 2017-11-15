package io.bootique.metrics.health;

import com.google.inject.Binder;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import io.bootique.ModuleExtender;

/**
 * @since 0.25
 */
public class HealthCheckModuleExtender extends ModuleExtender<HealthCheckModuleExtender> {

    private MapBinder<String, HealthCheck> healthChecks;
    private Multibinder<HealthCheckGroup> healthCheckGroups;

    public HealthCheckModuleExtender(Binder binder) {
        super(binder);
    }

    @Override
    public HealthCheckModuleExtender initAllExtensions() {
        getOrCreateHealthCheckGroupsBinder();
        getOrCreateHealthChecksBinder();

        return this;
    }

    public HealthCheckModuleExtender addHealthCheck(String name, HealthCheck healthCheck) {
        getOrCreateHealthChecksBinder().addBinding(name).toInstance(healthCheck);
        return this;
    }

    public <T extends HealthCheck> HealthCheckModuleExtender addHealthCheck(String name, Class<T> healthCheckType) {
        getOrCreateHealthChecksBinder().addBinding(name).to(healthCheckType);
        return this;
    }

    public HealthCheckModuleExtender addHealthCheckGroup(HealthCheckGroup healthCheckGroup) {
        getOrCreateHealthCheckGroupsBinder().addBinding().toInstance(healthCheckGroup);
        return this;
    }

    public <T extends HealthCheckGroup> HealthCheckModuleExtender addHealthCheckGroup(Class<T> healthCheckGroupType) {
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

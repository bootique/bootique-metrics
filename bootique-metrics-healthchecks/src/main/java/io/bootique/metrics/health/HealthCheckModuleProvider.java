package io.bootique.metrics.health;

import com.google.inject.Module;
import io.bootique.BQModuleProvider;

public class HealthCheckModuleProvider implements BQModuleProvider {

    @Override
    public Module module() {
        return new HealthCheckModule();
    }
}

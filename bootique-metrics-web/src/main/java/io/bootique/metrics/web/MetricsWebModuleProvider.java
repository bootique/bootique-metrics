package io.bootique.metrics.web;

import com.google.inject.Module;
import io.bootique.BQModuleProvider;

/**
 * @since 0.8
 */
public class MetricsWebModuleProvider implements BQModuleProvider {

    @Override
    public Module module() {
        return new MetricsWebModule();
    }
}

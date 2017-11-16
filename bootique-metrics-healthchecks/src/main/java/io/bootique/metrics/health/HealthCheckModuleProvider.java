package io.bootique.metrics.health;

import com.google.inject.Module;
import io.bootique.BQModuleProvider;
import io.bootique.metrics.health.heartbeat.HeartbeatFactory;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

public class HealthCheckModuleProvider implements BQModuleProvider {

    @Override
    public Module module() {
        return new HealthCheckModule();
    }

    @Override
    public Map<String, Type> configs() {
        return Collections.singletonMap("heartbeat", HeartbeatFactory.class);
    }
}

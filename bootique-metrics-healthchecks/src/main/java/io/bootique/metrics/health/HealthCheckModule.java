/*
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.bootique.metrics.health;

import io.bootique.BQCoreModule;
import io.bootique.BQModule;
import io.bootique.ModuleCrate;
import io.bootique.config.ConfigurationFactory;
import io.bootique.di.Binder;
import io.bootique.di.Provides;
import io.bootique.metrics.health.heartbeat.Heartbeat;
import io.bootique.metrics.health.heartbeat.HeartbeatCommand;
import io.bootique.metrics.health.heartbeat.HeartbeatFactory;
import io.bootique.metrics.health.heartbeat.HeartbeatReporter;

import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HealthCheckModule implements BQModule {

    private static final String CONFIG_PREFIX = "heartbeat";

    /**
     * Returns an instance of {@link HealthCheckModuleExtender} used by downstream modules to load custom extensions for
     * the HealthCheckModule. Should be invoked from a downstream Module's "configure" method.
     *
     * @param binder DI binder passed to the Module that invokes this method.
     * @return an instance of {@link HealthCheckModuleExtender} that can be used to load HealthCheckModule custom extensions.
     */
    public static HealthCheckModuleExtender extend(Binder binder) {
        return new HealthCheckModuleExtender(binder);
    }

    @Override
    public ModuleCrate crate() {
        return ModuleCrate.of(this)
                .description("Integrates monitoring health checks and heartbeat")
                .config(CONFIG_PREFIX, HeartbeatFactory.class)
                .build();
    }

    @Override
    public void configure(Binder binder) {
        extend(binder).initAllExtensions();

        BQCoreModule.extend(binder).addCommand(HeartbeatCommand.class);
    }

    @Provides
    @Singleton
    HealthCheckRegistry provideHealthCheckRegistry(Map<String, HealthCheck> healthChecks, Set<HealthCheckGroup> groups) {
        Map<String, HealthCheck> checks = new HashMap<>(healthChecks);
        groups.forEach(g -> checks.putAll(g.getHealthChecks()));
        return new HealthCheckRegistry(checks);
    }

    @Provides
    @Singleton
    HeartbeatFactory provideHeartbeatFactory(ConfigurationFactory configurationFactory) {
        return configurationFactory.config(HeartbeatFactory.class, CONFIG_PREFIX);
    }

    @Provides
    @Singleton
    Heartbeat provideHeartbeat(HeartbeatFactory heartbeatFactory) {
        return heartbeatFactory.createHeartbeat();
    }

    @Provides
    @Singleton
    HeartbeatReporter provideHeartbeatReporter(HeartbeatFactory heartbeatFactory) {
        return heartbeatFactory.createReporter();
    }

    @Provides
    @Singleton
    HeartbeatCommand provideHeartbeatCommand(Provider<Heartbeat> heartbeatProvider) {
        return new HeartbeatCommand(heartbeatProvider);
    }
}

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

import com.google.inject.Binder;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import io.bootique.BQCoreModule;
import io.bootique.ModuleExtender;
import io.bootique.command.Command;
import io.bootique.command.CommandDecorator;
import io.bootique.metrics.health.heartbeat.HeartbeatCommand;
import io.bootique.metrics.health.heartbeat.HeartbeatListener;

/**
 * @since 0.25
 */
public class HealthCheckModuleExtender extends ModuleExtender<HealthCheckModuleExtender> {

    private MapBinder<String, HealthCheck> healthChecks;
    private Multibinder<HealthCheckGroup> healthCheckGroups;
    private Multibinder<HeartbeatListener> heartbeatListeners;

    public HealthCheckModuleExtender(Binder binder) {
        super(binder);
    }

    @Override
    public HealthCheckModuleExtender initAllExtensions() {
        getOrCreateHealthCheckGroups();
        getOrCreateHealthChecks();
        getOrCreateHeartbeatListeners();
        return this;
    }

    public HealthCheckModuleExtender enabledHeartbeatFor(Class<? extends Command> commandType) {
        BQCoreModule.extend(binder).decorateCommand(commandType, CommandDecorator.alsoRun(HeartbeatCommand.class));
        return this;
    }

    public HealthCheckModuleExtender addHeartbeatListener(HeartbeatListener listener) {
        getOrCreateHeartbeatListeners().addBinding().toInstance(listener);
        return this;
    }

    public HealthCheckModuleExtender addHeartbeatListener(Class<? extends HeartbeatListener> listenerType) {
        getOrCreateHeartbeatListeners().addBinding().to(listenerType);
        return this;
    }

    public HealthCheckModuleExtender addHealthCheck(String name, HealthCheck healthCheck) {
        getOrCreateHealthChecks().addBinding(name).toInstance(healthCheck);
        return this;
    }

    public <T extends HealthCheck> HealthCheckModuleExtender addHealthCheck(String name, Class<T> healthCheckType) {
        getOrCreateHealthChecks().addBinding(name).to(healthCheckType);
        return this;
    }

    public HealthCheckModuleExtender addHealthCheckGroup(HealthCheckGroup healthCheckGroup) {
        getOrCreateHealthCheckGroups().addBinding().toInstance(healthCheckGroup);
        return this;
    }

    public <T extends HealthCheckGroup> HealthCheckModuleExtender addHealthCheckGroup(Class<T> healthCheckGroupType) {
        getOrCreateHealthCheckGroups().addBinding().to(healthCheckGroupType);
        return this;
    }

    protected MapBinder<String, HealthCheck> getOrCreateHealthChecks() {
        if (healthChecks == null) {
            healthChecks = MapBinder.newMapBinder(binder, String.class, HealthCheck.class);
        }

        return healthChecks;
    }

    protected Multibinder<HealthCheckGroup> getOrCreateHealthCheckGroups() {
        if (healthCheckGroups == null) {
            healthCheckGroups = Multibinder.newSetBinder(binder, HealthCheckGroup.class);
        }

        return healthCheckGroups;
    }

    protected Multibinder<HeartbeatListener> getOrCreateHeartbeatListeners() {
        if (heartbeatListeners == null) {
            heartbeatListeners = Multibinder.newSetBinder(binder, HeartbeatListener.class);
        }

        return heartbeatListeners;
    }
}

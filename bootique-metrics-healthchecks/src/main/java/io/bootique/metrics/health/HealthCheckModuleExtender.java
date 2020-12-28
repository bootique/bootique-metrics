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
import io.bootique.ModuleExtender;
import io.bootique.command.Command;
import io.bootique.command.CommandDecorator;
import io.bootique.di.Binder;
import io.bootique.di.MapBuilder;
import io.bootique.di.SetBuilder;
import io.bootique.metrics.health.heartbeat.HeartbeatCommand;
import io.bootique.metrics.health.heartbeat.HeartbeatListener;
import io.bootique.metrics.health.heartbeat.HeartbeatReporter;

/**
 * @since 0.25
 */
public class HealthCheckModuleExtender extends ModuleExtender<HealthCheckModuleExtender> {

    private MapBuilder<String, HealthCheck> healthChecks;
    private SetBuilder<HealthCheckGroup> healthCheckGroups;
    private SetBuilder<HeartbeatListener> heartbeatListeners;

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
        getOrCreateHeartbeatListeners().addInstance(listener);
        return this;
    }

    public HealthCheckModuleExtender addHeartbeatListener(Class<? extends HeartbeatListener> listenerType) {
        getOrCreateHeartbeatListeners().add(listenerType);
        return this;
    }

    /**
     * Enables heartbeat reporting. The reporter may be additionally configured via YAML ("heartbeat.sink" and
     * "heartbeat.writer" keys). Note that a presence or absence of configuration has no affect on whether the
     * reporter is enabled. Enabling only happens after calling this method.
     *
     * @return this extender instance.
     * @since 1.0.RC1
     */
    public HealthCheckModuleExtender enableHeartbeatReporting() {
        return addHeartbeatListener(HeartbeatReporter.class);
    }

    public HealthCheckModuleExtender addHealthCheck(String name, HealthCheck healthCheck) {
        getOrCreateHealthChecks().putInstance(name, healthCheck);
        return this;
    }

    public <T extends HealthCheck> HealthCheckModuleExtender addHealthCheck(String name, Class<T> healthCheckType) {
        getOrCreateHealthChecks().put(name, healthCheckType);
        return this;
    }

    public HealthCheckModuleExtender addHealthCheckGroup(HealthCheckGroup healthCheckGroup) {
        getOrCreateHealthCheckGroups().addInstance(healthCheckGroup);
        return this;
    }

    public <T extends HealthCheckGroup> HealthCheckModuleExtender addHealthCheckGroup(Class<T> healthCheckGroupType) {
        getOrCreateHealthCheckGroups().add(healthCheckGroupType);
        return this;
    }

    protected MapBuilder<String, HealthCheck> getOrCreateHealthChecks() {
        if (healthChecks == null) {
            healthChecks = newMap(String.class, HealthCheck.class);
        }

        return healthChecks;
    }

    protected SetBuilder<HealthCheckGroup> getOrCreateHealthCheckGroups() {
        if (healthCheckGroups == null) {
            healthCheckGroups = newSet(HealthCheckGroup.class);
        }

        return healthCheckGroups;
    }

    protected SetBuilder<HeartbeatListener> getOrCreateHeartbeatListeners() {
        if (heartbeatListeners == null) {
            heartbeatListeners = newSet(HeartbeatListener.class);
        }

        return heartbeatListeners;
    }
}

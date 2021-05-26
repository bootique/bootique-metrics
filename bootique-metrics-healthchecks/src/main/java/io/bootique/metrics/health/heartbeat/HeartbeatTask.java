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

package io.bootique.metrics.health.heartbeat;

import io.bootique.metrics.health.HealthCheckOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.function.Supplier;

/**
 * A {@link TimerTask} that executes heartbeat actions and notifies listeners of the results.
 */
public class HeartbeatTask extends TimerTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatTask.class);

    private Supplier<Map<String, HealthCheckOutcome>> heartbeatAction;
    private Set<HeartbeatListener> listeners;

    public HeartbeatTask(Supplier<Map<String, HealthCheckOutcome>> heartbeatAction, Set<HeartbeatListener> listeners) {
        this.heartbeatAction = heartbeatAction;
        this.listeners = listeners;
    }

    @Override
    public void run() {
        Map<String, HealthCheckOutcome> result = heartbeatAction.get();
        listeners.forEach(l -> notifyListener(l, result));
    }

    protected void notifyListener(HeartbeatListener listener, Map<String, HealthCheckOutcome> result) {
        try {
            listener.healthChecksFinished(result);
        } catch (Throwable th) {
            LOGGER.error("Error processing health check results", th);
        }
    }
}

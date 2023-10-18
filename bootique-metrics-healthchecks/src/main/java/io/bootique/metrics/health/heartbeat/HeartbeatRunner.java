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

import io.bootique.metrics.health.HealthCheckRegistry;

import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @since 3.0 converted to a class from an interface
 */
public class HeartbeatRunner {

    private final HealthCheckRegistry registry;
    private final Set<HeartbeatListener> listeners;
    private final long initialDelayMs;
    private final long fixedDelayMs;
    private final long healthCheckTimeoutMs;
    private final int threadPoolSize;

    public HeartbeatRunner(
            HealthCheckRegistry registry,
            Set<HeartbeatListener> listeners,
            long initialDelayMs,
            long fixedDelayMs,
            long healthCheckTimeoutMs,
            int threadPoolSize) {

        this.registry = Objects.requireNonNull(registry);
        this.listeners = Objects.requireNonNull(listeners);

        if (initialDelayMs < 0) {
            throw new IllegalArgumentException("Initial delay can't be negative");
        }
        this.initialDelayMs = initialDelayMs;

        this.fixedDelayMs = fixedDelayMs;
        if (fixedDelayMs <= 0) {
            throw new IllegalArgumentException("Delay between heartbeats must be positive");
        }

        this.healthCheckTimeoutMs = healthCheckTimeoutMs;
        if (healthCheckTimeoutMs <= 0) {
            throw new IllegalArgumentException("Health check timeout must be positive");
        }

        this.threadPoolSize = threadPoolSize;
    }

    public HeartbeatWatch start() {

        ExecutorService threadPool = startThreadPool();

        TimerTask heartbeat = createHeartbeatTask(threadPool);
        Timer timer = new Timer("bootique-heartbeat", true);
        timer.schedule(heartbeat, initialDelayMs, fixedDelayMs);

        return new HeartbeatWatch(timer, threadPool);
    }

    protected ExecutorService startThreadPool() {
        return Executors.newFixedThreadPool(threadPoolSize, new HealthCheckThreadFactory());
    }

    protected TimerTask createHeartbeatTask(ExecutorService threadPool) {
        return new HeartbeatTask(registry, threadPool, healthCheckTimeoutMs, listeners);
    }
}

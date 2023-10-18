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

import io.bootique.metrics.health.HealthCheck;
import io.bootique.metrics.health.HealthCheckOutcome;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @since 3.0
 */
public class SkippingHealthCheck implements HealthCheck {

    private final HealthCheck delegate;
    private final int invokeEveryXHeartbeats;
    private final AtomicLong counter;

    private volatile HealthCheckOutcome lastOutcome;

    public SkippingHealthCheck(HealthCheck delegate, int invokeEveryXHeartbeats) {
        this.delegate = delegate;
        this.invokeEveryXHeartbeats = invokeEveryXHeartbeats;
        this.counter = new AtomicLong();
    }

    @Override
    public HealthCheckOutcome check() throws Exception {

        HealthCheckOutcome localLastOutcome = this.lastOutcome;

        // run if
        // 1. this is the first check
        // 2. the previous outcome is not available (e.g. the first check never returned until the second heart beat)
        // 3. we skipped the requested number of heart beats
        long count = counter.addAndGet(1L);
        if (count == 1 || localLastOutcome == null || (count - 1) % invokeEveryXHeartbeats == 0) {
            HealthCheckOutcome o = delegate.check();
            this.lastOutcome = o;
            return o;
        }

        return localLastOutcome;
    }

    @Override
    public boolean isActive() {
        return delegate.isActive();
    }
}

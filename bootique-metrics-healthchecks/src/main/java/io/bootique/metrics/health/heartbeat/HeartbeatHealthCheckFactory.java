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

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.metrics.health.HealthCheck;
import io.bootique.metrics.health.HealthCheckRegistry;

import java.util.Objects;

/**
 * @since 3.0
 */
@BQConfig
public class HeartbeatHealthCheckFactory {

    private Integer skipHeartbeats;

    public HealthCheck createHeartbeatCheck(String name, HealthCheckRegistry registry) {

        HealthCheck check = registry.getHealthCheck(name);
        Objects.requireNonNull(check, () -> "Unknown health check: " + check);

        return skipHeartbeats != null && skipHeartbeats > 0
                // adding "1" to the skip count to recieve "invokeEveryXHeartbeats" value
                ? new SkippingHealthCheck(check, skipHeartbeats + 1)
                : check;
    }

    @BQConfigProperty("Allows this healthcheck to run less frequently, skipping some number of heartbeats")
    public HeartbeatHealthCheckFactory setSkipHeartbeats(Integer skipHeartbeats) {
        this.skipHeartbeats = skipHeartbeats;
        return this;
    }


}

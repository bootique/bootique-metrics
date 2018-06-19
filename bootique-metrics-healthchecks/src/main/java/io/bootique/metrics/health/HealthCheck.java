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

/**
 * Represents a single system health check.
 *
 * @since 0.8
 */
public interface HealthCheck {

    /**
     * Runs this health check, potentially throwing an exception on bad outcomes.
     *
     * @return outcome of the health check.
     * @throws Exception any exception that might have occurred when performing the health check.
     */
    HealthCheckOutcome check() throws Exception;

    /**
     * Returns whether this check should be executed during a registry health check run.
     *
     * @return this implementation always returns true.
     * @since 0.26
     */
    default boolean isActive() {
        return true;
    }

    /**
     * Runs this health check, catching all exceptions, turning them into unhealthy outcomes.
     *
     * @return outcome of the health check.
     */
    default HealthCheckOutcome safeCheck() {
        try {
            return check();
        } catch (Throwable th) {
            return HealthCheckOutcome.critical(th);
        }
    }
}

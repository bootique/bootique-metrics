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

package io.bootique.metrics.health.check;

import io.bootique.metrics.health.HealthCheck;
import io.bootique.metrics.health.HealthCheckOutcome;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * A {@link io.bootique.metrics.health.HealthCheck} proxy that returns unknown status until its internal supplier
 * provides a non-null HealthCheck. At that point the health check is considered "activated", and will be
 * running the real underlying check. This allows to pre-register and execute health checks for services that are
 * not yet started.
 *
 * @since 1.0.RC1
 */
public class DeferredHealthCheck implements HealthCheck {

    private static final HealthCheck INACTIVE_CHECK = new InactiveCheck();

    private Supplier<Optional<HealthCheck>> maybeDelegateSupplier;
    private volatile Supplier<HealthCheck> delegateSupplier;

    public DeferredHealthCheck(Supplier<Optional<HealthCheck>> maybeDelegateSupplier) {
        this.maybeDelegateSupplier = maybeDelegateSupplier;
        this.delegateSupplier = this::checkActivation;
    }

    @Override
    public boolean isActive() {
        return delegateSupplier.get().isActive();
    }

    @Override
    public HealthCheckOutcome check() throws Exception {
        return delegateSupplier.get().check();
    }

    private HealthCheck checkActivation() {

        Optional<HealthCheck> maybeDelegate = maybeDelegateSupplier.get();
        if (maybeDelegate.isPresent()) {
            HealthCheck delegate = maybeDelegate.get();
            this.delegateSupplier = () -> delegate;
            return delegate;
        }

        // returning inactive health check results in the checking being skipped...
        return INACTIVE_CHECK;
    }

    static class InactiveCheck implements HealthCheck {
        private static final HealthCheckOutcome UNKNOWN_OUTCOME = HealthCheckOutcome.unknown();

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public HealthCheckOutcome check() {
            return UNKNOWN_OUTCOME;
        }
    }
}

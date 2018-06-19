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
import io.bootique.metrics.health.HealthCheckData;
import io.bootique.metrics.health.HealthCheckOutcome;

import java.util.function.Supplier;

/**
 * @param <T> the type of value that the health check will verify.
 * @since 0.25
 */
public class ValueRangeCheck<T extends Comparable<T>> implements HealthCheck {

    private ValueRange<T> range;
    private Supplier<T> valueSupplier;

    public ValueRangeCheck(ValueRange<T> range, Supplier<T> valueSupplier) {
        this.range = range;
        this.valueSupplier = valueSupplier;
    }

    @Override
    public HealthCheckOutcome check() {

        T val = valueSupplier.get();
        HealthCheckData<T> data = new HealthCheckData<>(val, range);

        return range.reachedThreshold(val)
                .map(t -> toOutcome(t, data))
                .orElse(toUnknownOutcome(data));
    }

    protected HealthCheckOutcome toOutcome(Threshold<T> th, HealthCheckData<T> data) {

        switch (th.getType()) {
            case MIN:
                return HealthCheckOutcome.ok().withData(data);
            case WARNING:
                return HealthCheckOutcome
                        .warning("Value " + data.getValue() + " reaches or exceeds warning threshold of " + th.getValue())
                        .withData(data);
            case CRITICAL:
                return HealthCheckOutcome
                        .critical("Value " + data.getValue() + " reaches or exceeds critical threshold of " + th.getValue())
                        .withData(data);
            case MAX:
                // report max as CRITICAL
                return HealthCheckOutcome
                        .critical("Value " + data.getValue() + " reaches or exceeds max threshold of " + th.getValue())
                        .withData(data);
            default:
                throw new RuntimeException("Unexpected threshold type '"
                        + th.getType()
                        + "' for range position for value " + data.getValue());
        }
    }

    protected HealthCheckOutcome toUnknownOutcome(HealthCheckData<T> data) {
        return HealthCheckOutcome
                .unknown("Value " + data.getValue() + " is outside expected min/max range")
                .withData(data);
    }
}

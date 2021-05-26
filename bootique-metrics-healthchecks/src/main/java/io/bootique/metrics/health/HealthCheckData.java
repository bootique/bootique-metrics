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

import io.bootique.metrics.health.check.ValueRange;

/**
 * Contains information about the metric and thresholds used to calculate the health check.
 *
 * @param <T> the type of metrics value used by the health check.
 */
public class HealthCheckData<T extends Comparable<T>> {

    private T value;
    private ValueRange<T> thresholds;

    public HealthCheckData(T value, ValueRange<T> thresholds) {
        this.value = value;
        this.thresholds = thresholds;
    }

    public T getValue() {
        return value;
    }

    public ValueRange<T> getThresholds() {
        return thresholds;
    }
}

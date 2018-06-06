/**
 *    Licensed to the ObjectStyle LLC under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ObjectStyle LLC licenses
 *  this file to you under the Apache License, Version 2.0 (the
 *  “License”); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.bootique.metrics.health.check;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.value.Duration;

/**
 * @since 0.26
 */
@BQConfig
public class DurationRangeFactory {
    private Duration min;
    private Duration warning;
    private Duration critical;
    private Duration max;

    public Duration getMin() {
        return min;
    }

    @BQConfigProperty
    public void setMin(Duration min) {
        this.min = min;
    }

    public Duration getWarning() {
        return warning;
    }

    @BQConfigProperty
    public void setWarning(Duration warning) {
        this.warning = warning;
    }

    public Duration getCritical() {
        return critical;
    }

    @BQConfigProperty
    public void setCritical(Duration critical) {
        this.critical = critical;
    }

    public Duration getMax() {
        return max;
    }

    @BQConfigProperty
    public void setMax(Duration max) {
        this.max = max;
    }

    public ValueRange<Duration> createRange() {

        ValueRange.Builder<Duration> builder = ValueRange.builder(Duration.class);

        if (min != null) {
            builder.min(min);
        }

        if (warning != null) {
            builder.warning(warning);
        }

        if (critical != null) {
            builder.critical(critical);
        }

        if (max != null) {
            builder.max(max);
        }

        return builder.build();
    }
}

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
package io.bootique.metrics.health.writer;

import io.bootique.value.Duration;

public class DurationConverter implements ValueConverter<Duration> {

    @Override
    public String printableValue(Duration value, boolean includeUnits) {
        java.time.Duration jtDuration = value.getDuration();

        // total duration of java.time.duration has to be manually calculated from seconds and nanos
        long ms = (jtDuration.getSeconds() * 1_000_000_000 + jtDuration.getNano()) / 1_000_000L;
        return includeUnits ? ms + "ms" : Long.toString(ms);
    }
}

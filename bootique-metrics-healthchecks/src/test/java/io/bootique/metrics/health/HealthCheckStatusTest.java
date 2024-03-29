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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HealthCheckStatusTest {

    @Test
    public void matchesNagiosCodes() {
        // health checks must follow Nagios plugin spec for names and return codes
        // from https://assets.nagios.com/downloads/nagioscore/docs/nagioscore/3/en/pluginapi.html
        assertEquals(0, HealthCheckStatus.OK.ordinal());
        assertEquals(1, HealthCheckStatus.WARNING.ordinal());
        assertEquals(2, HealthCheckStatus.CRITICAL.ordinal());
        assertEquals(3, HealthCheckStatus.UNKNOWN.ordinal());
    }
}

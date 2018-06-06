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

package io.bootique.metrics.health;

/**
 * Defines the status of a health check. For simpler integration with monitoring tools, the status names and meanings
 * are taken from the
 * <a href="https://assets.nagios.com/downloads/nagioscore/docs/nagioscore/3/en/pluginapi.html">Nagios plugin spec</a>,
 * and their ordinals correspond to the Nagios plugin return codes.
 *
 * @since 0.25
 */
public enum HealthCheckStatus {
    OK(0), WARNING(1), CRITICAL(3), UNKNOWN(2);
    
    private int severity;

    HealthCheckStatus(int severity) {
        this.severity = severity;
    }

    /**
     * Returns the status severity. While the status "ordinal()" is used to match Nagios return code, severity is a
     * logical value that is similar, but does not fully match the ordinal.
     *
     * @return
     */
    public int getSeverity() {
        return severity;
    }
}


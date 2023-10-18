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

/**
 * Startable heartbeat object.
 */
public class Heartbeat {

    private final HeartbeatRunner heartbeatRunner;
    private HeartbeatWatch heartbeatWatch;

    public Heartbeat(HeartbeatRunner heartbeatRunner) {
        this.heartbeatRunner = heartbeatRunner;
    }

    public void start() {

        // sanity check, but otherwise don't bother with startup race conditions, as Bootique infrastructure will
        // ensure single-threaded start
        if (heartbeatWatch != null) {
            throw new IllegalStateException("Heartbeat is already running.");
        }

        this.heartbeatWatch = heartbeatRunner.start();
    }

    public void stop() {
        HeartbeatWatch local = this.heartbeatWatch;
        if (local != null) {
            this.heartbeatWatch = null;
            local.stop();
        }
    }
}

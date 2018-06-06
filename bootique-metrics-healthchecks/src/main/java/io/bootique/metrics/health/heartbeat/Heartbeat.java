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

package io.bootique.metrics.health.heartbeat;

import java.util.function.Supplier;

/**
 * Startable heartbeat object.
 *
 * @since 0.25
 */
public class Heartbeat {

    private final Supplier<Runnable> heartbeatStarter;
    Runnable heartbeatStopper;

    public Heartbeat(Supplier<Runnable> heartbeatStarter) {
        this.heartbeatStarter = heartbeatStarter;
    }

    public void start() {

        // sanity check, but otherwise don't bother with startup race conditions, as Bootique infrastructure will
        // ensure single-threaded start
        if (heartbeatStopper != null) {
            throw new IllegalStateException("Heartbeat is already running.");
        }

        this.heartbeatStopper = heartbeatStarter.get();
    }

    public void stop() {
        Runnable local = this.heartbeatStopper;
        if (local != null) {
            this.heartbeatStopper = null;
            local.run();
        }
    }
}

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

import javax.inject.Provider;

import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;

/**
 * A command that starts the application heartbeat. It is declared as a hidden command. To activate heartbeat, one would
 * need to use {@link io.bootique.command.CommandDecorator} to attach it to some other command.
 *
 * @since 0.25
 */
public class HeartbeatCommand extends CommandWithMetadata {

    private Provider<Heartbeat> heartbeatProvider;

    public HeartbeatCommand(Provider<Heartbeat> heartbeatProvider) {
        super(createMetadata());
        this.heartbeatProvider = heartbeatProvider;
    }

    private static CommandMetadata createMetadata() {
        return CommandMetadata.builder(HeartbeatCommand.class)
                .description("Schedules a \"heartbeat\" in-app process that wakes up periodically and runs a set of " +
                        "preconfigured application health checks.")
                // there are no practical scenarios when HeartbeatCommand needs to run standalone,
                // so hide it from the app CLI.
                .hidden()
                .build();
    }

    @Override
    public CommandOutcome run(Cli cli) {

        heartbeatProvider.get().start();
        return CommandOutcome.succeededAndForkedToBackground();
    }
}

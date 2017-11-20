package io.bootique.metrics.health.heartbeat;

import com.google.inject.Provider;
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

        // until "start and get out" strategy is implemented per https://github.com/bootique/bootique/issues/197
        // let's block indefinitely like we do in Jetty and jobs modules.
        try {
            Thread.currentThread().join();
        } catch (InterruptedException ie) {
        }

        return CommandOutcome.succeeded();
    }
}

package org.gepron1x.clans.plugin.command.futures;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionHandler;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;

public final class FutureCommandExecutionHandler<S> implements CommandExecutionHandler<S> {

    private final FutureCommandExecution<S> execution;
    private final Logger logger;

    public FutureCommandExecutionHandler(FutureCommandExecution<S> execution, Logger logger) {
        this.execution = execution;
        this.logger = logger;
    }

    @Override
    public void execute(@NonNull CommandContext<S> commandContext) {
        execution.execute(commandContext).exceptionally(t -> {
            logger.error("An error occured: ", t);
            return null;
        });

    }
}

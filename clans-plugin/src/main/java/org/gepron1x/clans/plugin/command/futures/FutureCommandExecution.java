package org.gepron1x.clans.plugin.command.futures;

import cloud.commandframework.context.CommandContext;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

@FunctionalInterface
public interface FutureCommandExecution<S> {
    CentralisedFuture<?> execute(CommandContext<S> context);
}

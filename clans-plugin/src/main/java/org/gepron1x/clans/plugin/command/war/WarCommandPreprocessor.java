package org.gepron1x.clans.plugin.command.war;

import cloud.commandframework.execution.preprocessor.CommandPreprocessingContext;
import cloud.commandframework.execution.preprocessor.CommandPreprocessor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class WarCommandPreprocessor implements CommandPreprocessor<CommandSender> {
    @Override
    public void accept(@NonNull CommandPreprocessingContext<CommandSender> context) {
        context.getInputQueue();

    }
}

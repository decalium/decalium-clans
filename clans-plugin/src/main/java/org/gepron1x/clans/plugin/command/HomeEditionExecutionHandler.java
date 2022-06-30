package org.gepron1x.clans.plugin.command;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.gepron1x.clans.api.edition.home.HomeEdition;

import java.util.function.BiConsumer;

public final class HomeEditionExecutionHandler implements CommandExecutionHandler<CommandSender> {
    private final BiConsumer<CommandContext<CommandSender>, HomeEdition> edition;

    public HomeEditionExecutionHandler(BiConsumer<CommandContext<CommandSender>, HomeEdition> edition, Component message) {
        this.edition = edition;
    }

    @Override
    public void execute(@NonNull CommandContext<CommandSender> commandContext) {

    }
}

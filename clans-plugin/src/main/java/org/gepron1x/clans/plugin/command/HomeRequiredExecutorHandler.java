package org.gepron1x.clans.plugin.command;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionHandler;
import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import io.leangen.geantyref.TypeToken;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.plugin.config.MessagesConfig;

import java.util.Objects;
import java.util.Optional;

public final class HomeRequiredExecutorHandler implements CommandExecutionHandler<CommandSender> {

    public static final CloudKey<ClanHome> HOME = SimpleCloudKey.of("decaliumclans_home", TypeToken.get(ClanHome.class));
    private final CommandExecutionHandler<CommandSender> delegate;
    private final String argument;
    private final MessagesConfig messages;

    public HomeRequiredExecutorHandler(CommandExecutionHandler<CommandSender> delegate, String argument, MessagesConfig messages) {

        this.delegate = delegate;
        this.argument = argument;
        this.messages = messages;
    }
    @Override
    public void execute(@NonNull CommandContext<CommandSender> commandContext) {
        Clan clan = commandContext.get(ClanExecutionHandler.CLAN);
        String homeName = Objects.requireNonNull(commandContext.get(argument));
        Optional<ClanHome> opt = clan.home(homeName);

        if (opt.isEmpty()) {
            commandContext.getSender().sendMessage(this.messages.commands().home().homeNotFound().with("name", homeName));
            return;
        }
        commandContext.set(HOME, opt.get());
        this.delegate.execute(commandContext);

    }
}

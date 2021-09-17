package org.gepron1x.clans.command.postprocessors;

import cloud.commandframework.execution.postprocessor.CommandPostprocessingContext;
import cloud.commandframework.execution.postprocessor.CommandPostprocessor;
import cloud.commandframework.services.types.ConsumerService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.gepron1x.clans.ClanManager;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.command.ClanCommand;
import org.gepron1x.clans.config.MessagesConfig;

public class ClanMemberCommandPostprocessor implements CommandPostprocessor<CommandSender> {
    private final ClanManager manager;
    private final MessagesConfig messages;

    public ClanMemberCommandPostprocessor(@NonNull ClanManager manager, @NonNull MessagesConfig messages) {
        this.manager = manager;
        this.messages = messages;
    }
    @Override
    public void accept(@NonNull CommandPostprocessingContext<CommandSender> context) {

        if(!context.getCommand().getCommandMeta().get(ClanCommand.CLAN_MEMBERS_ONLY).orElse(false)) return;
        CommandSender sender = context.getCommandContext().getSender();
        if(!(sender instanceof Player)) {
            sender.sendMessage(messages.commandIsOnlyForPlayers());
            ConsumerService.interrupt();
        }
        Clan clan = manager.getUserClan((Player) sender);

        if(clan == null) {
            sender.sendMessage(messages.notInClan());
            ConsumerService.interrupt();
        }

    }
}

package org.gepron1x.clans.command.postprocessors;

import cloud.commandframework.execution.postprocessor.CommandPostprocessingContext;
import cloud.commandframework.execution.postprocessor.CommandPostprocessor;
import cloud.commandframework.services.types.ConsumerService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.gepron1x.clans.ClanManager;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.clan.member.role.ClanPermission;
import org.gepron1x.clans.config.MessagesConfig;

import java.util.Objects;
import java.util.Optional;

public class ClanPermissionPostprocessor implements CommandPostprocessor<CommandSender> {
    private final ClanManager manager;
    private final MessagesConfig messages;

    public ClanPermissionPostprocessor(ClanManager manager, MessagesConfig messages) {
        this.manager = manager;
        this.messages = messages;
    }
    @Override
    public void accept(@NonNull CommandPostprocessingContext<CommandSender> ctx) {
        Optional<ClanPermission> optPermission = ctx.getCommand().getCommandMeta().get(ClanPermission.CLAN_PERMISSION);
        if(optPermission.isEmpty()) return;
        ClanPermission permission = optPermission.get();
        Player sender = (Player) ctx.getCommandContext().getSender();


        Clan clan = manager.getUserClan(sender);
        if(clan == null) {
            sender.sendMessage(messages.notInClan());
            ConsumerService.interrupt();
        }
        ClanMember member = Objects.requireNonNull(clan.getMember(sender));

        if(!member.hasPermission(permission)) {
            sender.sendMessage(messages.noClanPermission());
            ConsumerService.interrupt();
        }
     }
}

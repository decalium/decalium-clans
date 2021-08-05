package org.gepron1x.clans.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.clan.member.role.ClanPermission;
import org.gepron1x.clans.clan.member.role.ClanRole;
import org.gepron1x.clans.config.MessagesConfig;
import org.gepron1x.clans.manager.ClanManager;

import java.util.Objects;

@CommandAlias("clan")
@Subcommand("member")
public class MemberCommand extends BaseCommand {

    private final ClanManager manager;
    private final MessagesConfig messages;

    public MemberCommand(ClanManager manager, MessagesConfig messages) {

        this.manager = manager;
        this.messages = messages;
    }
    @Subcommand("set role")
    public void setRole(Player executor, OfflinePlayer target, ClanRole role) {
        if(clanCheck(executor, ClanPermission.SET_ROLE)) return;
        Clan clan = manager.getUserClan(executor);
        ClanMember member = clan.getMember(executor);
        if(!isMember(executor, target, clan)) return;

        ClanMember targetMember = Objects.requireNonNull(clan.getMember(target));
        if(targetMember.getRole().getWeight() >= member.getRole().getWeight()) {
            executor.sendMessage(messages.member().memberHasBiggerWeight().parse("target", target.getName()));
            return;
        }
        if(role.getWeight() >= member.getRole().getWeight()) {
            executor.sendMessage(messages.member().weightIsBigger());
            return;

        }

        targetMember.setRole(role);
        executor.sendMessage(messages.member().setRole().success().parse("target", target.getName()));

    }
    @Subcommand("kick")
    public void kick(Player executor, OfflinePlayer target) {
        if(!clanCheck(executor, ClanPermission.KICK_MEMBERS)) return;
        Clan clan = Objects.requireNonNull(manager.getUserClan(executor));
        if(!isMember(executor, target, clan)) return;


        if(clan.getMember(executor).getRole().getWeight() <= clan.getMember(target).getRole().getWeight()) {
            executor.sendMessage(messages.member().memberHasBiggerWeight().parse("target", target.getName()));
            return;
        }
        clan.removeMember(target);
        executor.sendMessage(messages.member().kickSuccess().parse("target", target.getName()));
    }




    private boolean clanCheck(Player executor, ClanPermission permission) {
        Clan clan = manager.getUserClan(executor);
        if(clan == null) {
            executor.sendMessage(messages.notInClan());
            return false;
        }
        if(!clan.getMember(executor).hasPermission(permission)) {
            executor.sendMessage(messages.noClanPermission());
            return false;
        }
        return true;
    }


    private boolean isMember(Player executor, OfflinePlayer player, Clan clan) {
        if(!clan.isMember(player)) {
            executor.sendMessage(messages.targetIsNotInClan());
            return false;
        }
        return true;
    }
}

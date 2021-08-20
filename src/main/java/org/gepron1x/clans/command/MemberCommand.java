package org.gepron1x.clans.command;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.clan.member.role.ClanPermission;
import org.gepron1x.clans.clan.member.role.ClanRole;
import org.gepron1x.clans.config.MessagesConfig;
import org.gepron1x.clans.ClanManager;

import java.util.Objects;

@CommandAlias("clan")
@Subcommand("member")
public class MemberCommand extends BaseClanCommand {


    public MemberCommand(ClanManager manager, MessagesConfig messages) {
        super(manager, messages);

    }
    @Subcommand("set role")
    public void setRole(Player executor, OfflinePlayer target, ClanRole role) {

        Clan clan = getClanIfPresent(executor);
        if(clan == null) return;
        if(!isMember(executor, target, clan)) return;
        if(!hasPermission(executor, clan, ClanPermission.SET_ROLE)) return;

        ClanMember member = clan.getMember(executor);
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
        Clan clan = getClanIfPresent(executor);
        if(clan == null) return;
        if(!hasPermission(executor, clan, ClanPermission.KICK)) return;
        if(!isMember(executor, target, clan)) return;


        if(clan.getMember(executor).getRole().getWeight() <= clan.getMember(target).getRole().getWeight()) {
            executor.sendMessage(messages.member().memberHasBiggerWeight().parse("target", target.getName()));
            return;
        }
        clan.removeMember(target);
        executor.sendMessage(messages.member().kickSuccess().parse("target", target.getName()));
    }






}

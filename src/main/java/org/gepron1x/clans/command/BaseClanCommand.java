package org.gepron1x.clans.command;

import co.aikar.commands.BaseCommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.member.role.ClanPermission;
import org.gepron1x.clans.config.MessagesConfig;
import org.gepron1x.clans.ClanManager;
import org.jetbrains.annotations.Nullable;

public abstract class BaseClanCommand extends BaseCommand {
    protected final ClanManager manager;
    protected MessagesConfig messages;

    public BaseClanCommand(ClanManager clanManager, MessagesConfig messages) {
        this.manager = clanManager;
        this.messages = messages;
    }

    @Nullable
    protected Clan getClanIfPresent(Player player) {
        Clan clan = manager.getUserClan(player);
        if(clan == null) player.sendMessage(messages.notInClan());
        return clan;
    }

    protected boolean hasPermission(Player executor, Clan clan, ClanPermission permission) {
        boolean has = clan.getMember(executor).hasPermission(permission);
        if(!has) executor.sendMessage(messages.noClanPermission());
        return has;
    }
    protected boolean isMember(Player executor, OfflinePlayer member, Clan clan) {
        boolean is = clan.isMember(member);
        if(!is) executor.sendMessage(messages.targetIsNotInClan());
        return is;
    }
}

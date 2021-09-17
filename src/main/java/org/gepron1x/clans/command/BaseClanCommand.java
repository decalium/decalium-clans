package org.gepron1x.clans.command;

import cloud.commandframework.CommandManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.config.MessagesConfig;
import org.gepron1x.clans.ClanManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public abstract class BaseClanCommand {
    protected final ClanManager manager;
    protected MessagesConfig messages;

    public BaseClanCommand(@NotNull ClanManager clanManager, @NotNull MessagesConfig messages) {
        this.manager = clanManager;
        this.messages = messages;
    }

    public abstract void register(@NotNull CommandManager<CommandSender> manager);

    @Nullable
    protected Clan getClanIfPresent(@NotNull Player player) {
        Clan clan = manager.getUserClan(player);
        if(clan == null) player.sendMessage(messages.notInClan());
        return clan;
    }
    @NotNull
    protected Clan getClan(@NotNull UUID uuid) {
        return Objects.requireNonNull(manager.getUserClan(uuid));
    }
    @NotNull
    protected Clan getClan(@NotNull OfflinePlayer player) {
        return getClan(player.getUniqueId());
    }
    @NotNull
    protected ClanMember getMember(@NotNull Clan clan, @NotNull OfflinePlayer player) {
        return Objects.requireNonNull(clan.getMember(player));
    }


    protected boolean isMember(Player executor, OfflinePlayer member, Clan clan) {
        boolean is = clan.isMember(member);
        if(!is) executor.sendMessage(messages.targetIsNotInClan());
        return is;
    }
}

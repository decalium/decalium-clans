package org.gepron1x.clans.plugin.command;

import cloud.commandframework.CommandManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public abstract class AbstractCommand {


    public abstract void register(CommandManager<CommandSender> manager);


    @NotNull
    protected ClanMember getMember(@NotNull Clan clan, @NotNull UUID uuid) {
        return Objects.requireNonNull(clan.getMember(uuid));
    }

    @NotNull
    protected ClanMember getMember(@NotNull Clan clan, @NotNull OfflinePlayer player) {
        return getMember(clan, player.getUniqueId());
    }

}

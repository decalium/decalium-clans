package org.gepron1x.clans.api.clan.member;

import net.kyori.adventure.util.Buildable;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface ClanMember extends Buildable<ClanMember, ClanMember.Builder> {


    @NotNull UUID getUniqueId();
    @NotNull ClanRole getRole();

    default boolean hasPermission(@NotNull ClanPermission permission) {
        return getRole().getPermissions().contains(permission);
    }


    @Contract("_ -> new")
    @NotNull ClanMember withRole(@NotNull ClanRole role);

    default Player asPlayer(@NotNull Server server) {
        return server.getPlayer(getUniqueId());
    }

    default OfflinePlayer asOffline(@NotNull Server server) {
        return server.getOfflinePlayer(getUniqueId());
    }


    interface Builder extends Buildable.Builder<ClanMember> {
        @Contract("_ -> this")
        @NotNull Builder uuid(UUID uuid);

        @Contract("_ -> this")
        @NotNull Builder role(@NotNull ClanRole role);


    }

}

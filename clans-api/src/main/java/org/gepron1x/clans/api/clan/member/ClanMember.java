package org.gepron1x.clans.api.clan.member;

import net.kyori.adventure.util.Buildable;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface ClanMember extends Buildable<ClanMember, ClanMember.Builder> {


    @NotNull UUID getUniqueId();
    @NotNull ClanRole getRole();


    @Contract("_ -> new")
    @NotNull ClanMember withRole(@NotNull ClanRole role);

    default Player asPlayer(@NotNull Server server) {
        return server.getPlayer(getUniqueId());
    }


    interface Builder extends Buildable.Builder<ClanMember> {
        @Contract("_ -> this")
        @NotNull Builder uuid(@NotNull UUID uuid);

        @Contract("_ -> this")
        @NotNull Builder role(@NotNull ClanRole role);


    }

}

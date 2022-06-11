package org.gepron1x.clans.api.clan.member;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.util.Buildable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.edition.EditionApplicable;
import org.gepron1x.clans.api.edition.member.MemberEdition;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface ClanMember extends Buildable<ClanMember, ClanMember.Builder>, ComponentLike {


    @NotNull UUID uniqueId();
    @NotNull ClanRole role();

    default boolean hasPermission(@NotNull ClanPermission permission) {
        return role().permissions().contains(permission);
    }

    @Contract("_ -> new")
    @NotNull ClanMember withRole(@NotNull ClanRole role);

    default Player asPlayer(@NotNull Server server) {
        return server.getPlayer(uniqueId());
    }

    @NotNull
    default OfflinePlayer asOffline(@NotNull Server server) {
        return server.getOfflinePlayer(uniqueId());
    }

    default Component renderName(@NotNull Server server) {
        UUID uuid = uniqueId();
        OfflinePlayer player = server.getOfflinePlayer(uuid);
        Player onlinePlayer = player.getPlayer();
        if(onlinePlayer != null) return onlinePlayer.displayName();
        String name = player.getName();
        if(name != null) return Component.text(name);
        String uuidString = uuid.toString();
        return Component.text().append(Component.text("Unknown player "))
                .append(Component.text("("+uuidString+")").clickEvent(ClickEvent.copyToClipboard(uuidString)))
                .build();

    }

    @Override
    @NotNull
    default Component asComponent() {
        return renderName(Bukkit.getServer());
    }

    interface Builder extends Buildable.Builder<ClanMember>, EditionApplicable<ClanMember, MemberEdition> {
        @Contract("_ -> this")
        @NotNull Builder uuid(UUID uuid);

        @Contract("_ -> this")
        @NotNull Builder role(@NotNull ClanRole role);


    }

}

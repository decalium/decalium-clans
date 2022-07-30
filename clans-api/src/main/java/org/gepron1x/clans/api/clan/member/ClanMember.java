/*
 * decalium-clans
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.api.clan.member;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.util.Buildable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.audience.RenderedPlayerName;
import org.gepron1x.clans.api.edition.EditionApplicable;
import org.gepron1x.clans.api.edition.member.MemberEdition;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public interface ClanMember extends Buildable<ClanMember, ClanMember.Builder>, ComponentLike {


    @NotNull UUID uniqueId();
    @NotNull ClanRole role();

    default boolean hasPermission(@NotNull ClanPermission permission) {
        return role().permissions().contains(permission);
    }

    @Contract("_ -> new")
    @NotNull ClanMember withRole(@NotNull ClanRole role);

    default Optional<Player> asPlayer(@NotNull Server server) {
        return Optional.ofNullable(server.getPlayer(uniqueId()));
    }

    @NotNull
    default OfflinePlayer asOffline(@NotNull Server server) {
        return server.getOfflinePlayer(uniqueId());
    }

    default Component renderName(@NotNull Server server) {
        return new RenderedPlayerName(uniqueId(), server).asComponent();
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
        @NotNull default Builder player(OfflinePlayer player) {
            return uuid(player.getUniqueId());
        }

        @Contract("_ -> this")
        @NotNull Builder role(@NotNull ClanRole role);


    }

}

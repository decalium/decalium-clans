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
package org.gepron1x.clans.api.repository;

import org.bukkit.OfflinePlayer;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ClanRepository {
    @NotNull CentralisedFuture<ClanCreationResult> createClan(@NotNull DraftClan draftClan);

    @NotNull CentralisedFuture<Boolean> removeClan(@NotNull Clan clan);


    @NotNull CentralisedFuture<Optional<Clan>> requestClan(@NotNull String tag);
    @NotNull CentralisedFuture<Optional<Clan>> requestUserClan(@NotNull UUID uuid);

    default CentralisedFuture<Optional<Clan>> requestUserClan(@NotNull OfflinePlayer player) {
        return requestUserClan(player.getUniqueId());
    }

    @NotNull CentralisedFuture<Set<? extends Clan>> clans();

}

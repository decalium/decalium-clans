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
package org.gepron1x.clans.api.clan.home;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.util.Buildable;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.edition.EditionApplicable;
import org.gepron1x.clans.api.edition.home.HomeEdition;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface ClanHome extends Buildable<ClanHome, ClanHome.Builder>, ComponentLike {
    @Override
    @NotNull
    default Component asComponent() {
        return displayName();
    }

    @NotNull String name();
    @NotNull Component displayName();
    @NotNull UUID creator();
    @NotNull Location location();
    @NotNull ItemStack icon();



    interface Builder extends Buildable.Builder<ClanHome>, EditionApplicable<ClanHome, HomeEdition> {
        @Contract("_ -> this")
        @NotNull Builder name(@NotNull String name);

        @Contract("_ -> this")
        @NotNull Builder displayName(@NotNull Component displayName);

        @Contract("_ -> this")
        @NotNull Builder creator(@NotNull UUID creator);

        @Contract("_ -> this")
        @NotNull Builder location(@NotNull Location location);

        @Contract("_ -> this")
        @NotNull Builder icon(@NotNull ItemStack icon);




    }
}

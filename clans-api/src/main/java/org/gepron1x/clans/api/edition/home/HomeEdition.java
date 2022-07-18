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
package org.gepron1x.clans.api.edition.home;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.edition.Edition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HomeEdition extends Edition<ClanHome> {

    @Override
    @NotNull
    default Class<ClanHome> getTarget() { return ClanHome.class; }

    HomeEdition setIcon(@Nullable ItemStack icon);
    HomeEdition move(@NotNull Location location);
    HomeEdition rename(@NotNull Component displayName);
}

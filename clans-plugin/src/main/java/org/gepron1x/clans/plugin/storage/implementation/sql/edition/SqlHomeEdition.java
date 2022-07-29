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
package org.gepron1x.clans.plugin.storage.implementation.sql.edition;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.edition.home.HomeEdition;
import org.intellij.lang.annotations.Language;
import org.jdbi.v3.core.Handle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SqlHomeEdition implements HomeEdition {
    @Language("SQL")
    private static final String UPDATE_ICON = "UPDATE `homes` SET `icon`=? WHERE `name`=? AND `clan_id`=?";
    @Language("SQL")
    private static final String UPDATE_LOCATION = "UPDATE `locations` SET `x`=?, `y`=?, `z`=?, `world`=? WHERE `home_id`=(SELECT id FROM homes WHERE `clan_id`=? AND `name`=?)";
    @Language("SQL")
    private static final String UPDATE_DISPLAY_NAME = "UPDATE `homes` SET `display_name`=? WHERE `name`=? AND `clan_id`=?";
    private final Handle handle;
    private final int clanId;
    private final String homeName;

    public SqlHomeEdition(@NotNull Handle handle, int clanId, String homeName) {

        this.handle = handle;
        this.clanId = clanId;
        this.homeName = homeName;
    }

    @Override
    public HomeEdition setIcon(@Nullable ItemStack icon) {
        handle.createUpdate(UPDATE_ICON)
                .bind(1, homeName)
                .bind(2, clanId)
                .bind(0, icon);
        return this;
    }

    @Override
    public HomeEdition move(@NotNull Location location) {
        handle.createUpdate(UPDATE_LOCATION)
                .bind(5, homeName)
                .bind(4, clanId)
                .bind(0, location.getBlockX())
                .bind(1, location.getBlockY())
                .bind(2, location.getBlockZ())
                .bind(3, location.getWorld().getName())
                .execute();
        return this;
    }

    @Override
    public HomeEdition rename(@NotNull Component displayName) {
        handle.createUpdate(UPDATE_DISPLAY_NAME)
                .bind(1, homeName)
                .bind(2, clanId)
                .bind(0, displayName)
                .execute();
        return this;
    }
}

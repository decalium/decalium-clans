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
package org.gepron1x.clans.plugin.util.pdc;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.plugin.DecaliumClansPlugin;

import java.util.Optional;
import java.util.UUID;

public final class OwnedEntity {

    private static final NamespacedKey OWNER = new NamespacedKey(JavaPlugin.getPlugin(DecaliumClansPlugin.class), "owner");

    private final Entity entity;

    public OwnedEntity(Entity entity) {
        this.entity = entity;
    }

    public Optional<UUID> owner() {
        return Optional.ofNullable(this.entity.getPersistentDataContainer().get(OWNER, UuidDataType.UUID));
    }

    public void owner(UUID owner) {
        this.entity.getPersistentDataContainer().set(OWNER, UuidDataType.UUID, owner);
    }
}

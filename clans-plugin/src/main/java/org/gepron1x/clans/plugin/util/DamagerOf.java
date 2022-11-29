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
package org.gepron1x.clans.plugin.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.gepron1x.clans.plugin.util.pdc.OwnedEntity;

import java.util.Optional;

public final class DamagerOf {

    private final Entity entity;

    public DamagerOf(Entity damager) {

        this.entity = damager;
    }


    public Optional<Player> damager() {
        if(entity instanceof Player player) {
            return Optional.of(player);
        } else if(entity instanceof Projectile projectile) {
            return Optional.ofNullable(projectile.getShooter())
                    .filter(Player.class::isInstance).map(Player.class::cast);
        } else if(entity instanceof TNTPrimed tnt) {
            return Optional.ofNullable(tnt.getSource())
                    .filter(Player.class::isInstance).map(Player.class::cast);
        }
        return new OwnedEntity(entity).owner().map(entity.getServer()::getPlayer);
    }


}

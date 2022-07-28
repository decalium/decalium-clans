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
package org.gepron1x.clans.plugin.listener;

import org.bukkit.Statistic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.gepron1x.clans.plugin.util.pdc.OwnedEntity;

public final class CrystalExplosionListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCrystalExplode(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.ENDER_CRYSTAL) return;
        if (!(event.getDamager() instanceof Player damager)) return;
        new OwnedEntity(entity).owner(damager.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager.getType() != EntityType.ENDER_CRYSTAL) return;
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        new OwnedEntity(damager).owner().map(damager.getServer()::getPlayer).ifPresent(player -> {
            player.incrementStatistic(Statistic.DAMAGE_DEALT, (int) event.getFinalDamage());
            if (entity.getHealth() - event.getFinalDamage() <= 0.0) {
                entity.setKiller(player);
            }

        });

    }
}

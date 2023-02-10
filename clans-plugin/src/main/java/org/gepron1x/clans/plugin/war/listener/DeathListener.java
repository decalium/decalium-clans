/*
 * decalium-clans
 * Copyright © 2023 George Pronyuk <https://vk.com/gpronyuk>
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
package org.gepron1x.clans.plugin.war.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.gepron1x.clans.api.war.Wars;
import org.gepron1x.clans.plugin.util.DamagerOf;

import java.util.Optional;

public final class DeathListener implements Listener {

    private final Wars wars;

    public DeathListener(Wars wars) {
        this.wars = wars;
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerDeathEvent event) {
        wars.onDeath(event.getEntity());
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(wars.currentWar(player).isPresent()) {
            player.setHealth(0);
            Optional.ofNullable(player.getLastDamageCause()).filter(EntityDamageByEntityEvent.class::isInstance)
                    .map(EntityDamageByEntityEvent.class::cast).flatMap(e -> new DamagerOf(e.getDamager()).damager()).ifPresent(player::setKiller);
        }
        wars.onDeath(player);
    }
}

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

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.gepron1x.clans.api.war.Wars;
import org.gepron1x.clans.plugin.util.DamagerOf;

import java.util.Optional;

public final class NoTeamDamageListener implements Listener {

    private final Wars wars;

    public NoTeamDamageListener(Wars wars) {
        this.wars = wars;
    }


    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Optional<Player> opt = getDamager(event.getDamager());
        if(opt.isEmpty()) return;
        Player damager = opt.orElseThrow();
        if(!(event.getEntity() instanceof Player player)) return;
        if(damager.equals(player)) return;
        wars.currentWar(damager).flatMap(war -> war.team(damager)).ifPresent(team -> {
            if(team.isMember(player)) {
                event.setCancelled(true);
            }
        });
    }

    public Optional<Player> getDamager(Entity entity) {
        return new DamagerOf(entity).damager();
    }


}

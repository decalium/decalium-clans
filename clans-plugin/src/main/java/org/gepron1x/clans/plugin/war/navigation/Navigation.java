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
package org.gepron1x.clans.plugin.war.navigation;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.util.player.PlayerReference;
import org.gepron1x.clans.api.war.Team;
import org.gepron1x.clans.api.war.War;
import org.gepron1x.clans.api.war.Wars;
import org.gepron1x.clans.plugin.config.messages.MessagesConfig;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;


public final class Navigation implements Runnable {

    private final Wars wars;
    private final MessagesConfig messages;

    public Navigation(Wars wars, MessagesConfig messages) {

        this.wars = wars;
        this.messages = messages;
    }

    @Override
    public void run() {
        for(War war : wars.currentWars()) for(Team team : war.teams()) {
            Team enemy = war.enemy(team);
            for(PlayerReference ref : team.alive()) {
                ref.ifOnline(player -> {
                    Player closest = closestPlayer(player, enemy);
                    if(closest != null) player.sendActionBar(new NavigationBar(this.messages, player, closest));
                });

            }
        }
    }

    @Nullable
    private Player closestPlayer(final Player player, final Team team) {
        Location second = player.getLocation();
        Player currentPlayer = null;
        double minDistance = 0;
        for(PlayerReference reference : team.alive()) {
            Player p = reference.orElseThrow();
            Location first = p.getLocation();
            if(!Objects.equals(first.getWorld(), second.getWorld())) continue;
            double distance = first.distanceSquared(second);
            if(currentPlayer == null || distance < minDistance) {
                currentPlayer = p;
                minDistance = distance;
            }
        }
        return currentPlayer;
    }




}

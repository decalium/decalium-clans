package org.gepron1x.clans.plugin.war.listener.navigation;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.util.player.PlayerReference;
import org.gepron1x.clans.api.war.Team;
import org.gepron1x.clans.api.war.War;
import org.gepron1x.clans.api.war.Wars;
import org.gepron1x.clans.plugin.config.MessagesConfig;
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
        Player currentPlayer = null;
        double minDistance = 0;
        for(PlayerReference reference : team.alive()) {
            Player p = reference.orElseThrow();
            Location first = p.getLocation();
            Location second = player.getLocation();
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

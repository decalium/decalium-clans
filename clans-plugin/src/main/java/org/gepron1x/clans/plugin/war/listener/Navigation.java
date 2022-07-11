package org.gepron1x.clans.plugin.war.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.gepron1x.clans.plugin.util.player.PlayerReference;
import org.gepron1x.clans.plugin.war.Team;
import org.gepron1x.clans.plugin.war.War;
import org.gepron1x.clans.plugin.war.Wars;

import java.util.Comparator;

public final class Navigation implements Runnable {

    private final Wars wars;

    public Navigation(Wars wars) {

        this.wars = wars;
    }

    @Override
    public void run() {
        for(War war : wars.currentWars()) for(Team team : war.teams()) {
            Team enemy = war.enemy(team);
            for(PlayerReference ref : team.alive()) {
                ref.ifOnline(player -> {
                    enemy.alive().stream().map(PlayerReference::orElseThrow)
                            .min(this.distanceComparator(player.getLocation()))
                            .ifPresent(p -> player.sendActionBar(new NavigationBar(player, p)));
                });

            }
        }
    }

    private Comparator<Player> distanceComparator(final Location location) {

        return (p1, p2) -> {
            double value = p1.getLocation().distanceSquared(location) - p2.getLocation().distanceSquared(location);
            return (int) value;
        };
    }




}

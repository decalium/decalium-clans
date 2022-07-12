package org.gepron1x.clans.plugin.war.listener;

import org.bukkit.entity.Player;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.gepron1x.clans.plugin.util.player.PlayerReference;
import org.gepron1x.clans.plugin.war.Team;
import org.gepron1x.clans.plugin.war.War;
import org.gepron1x.clans.plugin.war.Wars;
import org.jetbrains.annotations.Nullable;


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
            double distance = p.getLocation().distanceSquared(player.getLocation());
            if(currentPlayer == null || distance < minDistance) {
                currentPlayer = p;
                minDistance = distance;
            }
        }
        return currentPlayer;
    }




}

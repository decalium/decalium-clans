package org.gepron1x.clans.plugin.war.listener;

import com.google.common.collect.Iterators;
import org.bukkit.entity.Player;
import org.gepron1x.clans.plugin.util.player.PlayerReference;
import org.gepron1x.clans.plugin.war.Team;
import org.gepron1x.clans.plugin.war.War;
import org.gepron1x.clans.plugin.war.Wars;

import java.util.Iterator;

public final class Navigation implements Runnable {

    private final Wars wars;

    public Navigation(Wars wars) {

        this.wars = wars;
    }

    @Override
    public void run() {
        for(War war : wars.currentWars()) for(Team team : war.teams()) {
            Team enemy = war.enemy(team);
            Iterator<PlayerReference> cycling = Iterators.cycle(enemy.alive());
            for(PlayerReference ref : team.alive()) {
                Player first = ref.player().orElseThrow();
                Player second = cycling.next().player().orElseThrow();
            }
        }


    }


}

package org.gepron1x.clans.plugin.war;

import org.bukkit.entity.Player;
import org.gepron1x.clans.api.war.Team;

import java.util.Collection;
import java.util.Optional;

public interface War {

    Team enemy(Team team);

    Collection<Team> teams();

    default Optional<Team> team(Player player) {
        for(Team team : teams()) {
            if(team.isMember(player)) return Optional.of(team);
        }
        return Optional.empty();
    }


    boolean onPlayerDeath(Player player);

    boolean isEnded();


}

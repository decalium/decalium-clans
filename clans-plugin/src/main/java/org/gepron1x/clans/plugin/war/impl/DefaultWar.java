package org.gepron1x.clans.plugin.war.impl;

import org.bukkit.entity.Player;
import org.gepron1x.clans.plugin.war.Team;
import org.gepron1x.clans.plugin.war.War;

import java.util.Collection;
import java.util.Collections;

public final class DefaultWar implements War {

    private final Collection<Team> teams;

    public DefaultWar(Collection<Team> teams) {
        this.teams = teams;
    }

    @Override
    public Collection<Team> teams() {
        return Collections.unmodifiableCollection(teams);
    }

    @Override
    public boolean onPlayerDeath(Player player) {
        boolean ok = false;
        for(Team team : teams) {
            ok |= team.onDeath(player);
        }
        return ok;
    }

    @Override
    public boolean isEnded() {
        for(Team team : teams) {
            if(!team.isAlive()) return true;
        }
        return false;
    }

}

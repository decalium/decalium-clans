package org.gepron1x.clans.plugin.war.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import org.bukkit.entity.Player;
import org.gepron1x.clans.plugin.war.Team;
import org.gepron1x.clans.plugin.war.War;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

public final class DefaultWar implements War {

    private final Collection<Team> teams;

    public DefaultWar(Collection<Team> teams) {
        this.teams = teams;
    }

    @Override
    public Team enemy(Team team) {
        Preconditions.checkArgument(this.teams.contains(team), "Team is not in the war");
        Iterator<Team> iterator = Iterators.cycle(this.teams);
        Team next = iterator.next();
        while(next.equals(team)) {
            next = iterator.next();
        }
        return next;
    }

    @Override
    public Collection<Team> teams() {
        return Collections.unmodifiableCollection(teams);
    }

    @Override
    public boolean onPlayerDeath(Player player) {
        boolean ok = false;
        for(Team team : teams) {
            if(team.onDeath(player)) ok = true;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultWar that = (DefaultWar) o;
        return teams.equals(that.teams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teams);
    }
}

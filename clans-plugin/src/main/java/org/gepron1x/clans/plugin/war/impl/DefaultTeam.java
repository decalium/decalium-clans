package org.gepron1x.clans.plugin.war.impl;

import com.google.common.base.MoreObjects;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.plugin.util.player.PlayerReference;
import org.gepron1x.clans.plugin.war.Team;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;

public final class DefaultTeam implements Team {
    private final ClanReference clan;
    private final Collection<PlayerReference> members;
    private final Collection<PlayerReference> alive;

    public DefaultTeam(ClanReference clan, Collection<PlayerReference> members) {
        this.clan = clan;
        this.members = members;
        this.alive = new HashSet<>(members);
    }

    @Override
    public ClanReference clan() {
        return this.clan;
    }

    @Override
    public Collection<PlayerReference> members() {
        return this.members;
    }

    @Override
    public Collection<PlayerReference> alive() {
        return Collections.unmodifiableCollection(this.alive);
    }

    @Override
    public boolean onDeath(Player player) {
        return alive.removeIf(ref -> ref.player().map(p -> p.equals(player)).orElse(false));
    }

    @Override
    public boolean isAlive() {
        return !this.alive.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultTeam that = (DefaultTeam) o;
        return clan.equals(that.clan) && members.equals(that.members) && alive.equals(that.alive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clan, members, alive);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("clan", clan)
                .add("members", members)
                .add("alive", alive)
                .toString();
    }
}

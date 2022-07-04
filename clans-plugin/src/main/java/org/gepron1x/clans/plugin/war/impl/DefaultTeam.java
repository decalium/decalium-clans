package org.gepron1x.clans.plugin.war.impl;

import org.bukkit.entity.Player;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.plugin.util.player.PlayerReference;
import org.gepron1x.clans.plugin.war.Team;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

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
        return this.alive.isEmpty();
    }
}

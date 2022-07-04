package org.gepron1x.clans.plugin.war.impl;

import com.google.common.base.Preconditions;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.util.ClanOnlinePlayers;
import org.gepron1x.clans.plugin.util.player.PlayerReference;
import org.gepron1x.clans.plugin.war.War;
import org.gepron1x.clans.plugin.war.Wars;

import java.util.*;
import java.util.stream.Collectors;

public final class DefaultWars implements Wars {


    private final Set<War> currentWars = new HashSet<>();
    private final Server server;

    public DefaultWars(Server server) {

        this.server = server;
    }
    @Override
    public War create(ClanReference first, ClanReference second) {
        Preconditions.checkState(first.cached().isPresent() || second.cached().isPresent(), "no online players in some of clan!");
        return new DefaultWar(
                List.of(createFromClan(first), createFromClan(second))
        );

    }

    private DefaultTeam createFromClan(ClanReference reference) {
        Preconditions.checkState(reference.cached().isPresent(), "No online players in clan "+ reference);
        Clan clan = reference.cached().orElseThrow();
        return new DefaultTeam(
                reference,
                new ClanOnlinePlayers(clan, this.server).players()
                        .stream()
                        .map(PlayerReference::reference)
                        .collect(Collectors.toSet())
        );
    }

    @Override
    public Optional<War> currentWar(Player player) {
        return Optional.empty();
    }

    @Override
    public Collection<War> currentWars() {
        return Collections.unmodifiableCollection(this.currentWars);
    }

    @Override
    public void onDeath(Player player) {
        for(War war : this.currentWars) {
            if(war.onPlayerDeath(player)) return;
        }
    }
}

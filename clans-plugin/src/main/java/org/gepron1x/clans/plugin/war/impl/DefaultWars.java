package org.gepron1x.clans.plugin.war.impl;

import com.google.common.base.Preconditions;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.util.ClanOnlinePlayers;
import org.gepron1x.clans.plugin.util.player.PlayerReference;
import org.gepron1x.clans.plugin.war.Team;
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
    public void start(War war) {
        Preconditions.checkArgument(!this.currentWars.contains(war), "war already started");
        this.currentWars.add(war);
    }

    @Override
    public War create(Team first, Team second) {
        Preconditions.checkState(first.clan().cached().isPresent() || second.clan().cached().isPresent(), "no online players in some of clan!");
        return new DefaultWar(
                List.of(first, second)
        );
    }

    @Override
    public Team createTeam(ClanReference ref) {
        return createFromClan(ref);
    }

    private DefaultTeam createFromClan(ClanReference reference) {
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
        Iterator<War> iterator = this.currentWars.iterator();
        while(iterator.hasNext()) {
            War war = iterator.next();
            if(!war.onPlayerDeath(player)) continue;
            if(war.isEnded()) iterator.remove();
        }

    }

    @Override
    public void end(War war) {
        this.currentWars.remove(war);

    }

    @Override
    public void cleanEnded() {
        this.currentWars.removeIf(War::isEnded);
    }

}

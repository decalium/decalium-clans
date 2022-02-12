package org.gepron1x.clans.plugin.war;

import net.kyori.adventure.util.Buildable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class ClanWarPreparation {
    private final Map<UUID, ClanWarTeam.Builder> teams;

    public ClanWarPreparation(Map<UUID, ClanWarTeam.Builder> teams) {
        this.teams = teams;
    }

    public ClanWarTeam.Builder getTeamBuilder(UUID leader) {
        return this.teams.get(leader);
    }

    public Set<ClanWarTeam> prepare() {
        return teams.values().stream().map(Buildable.Builder::build).collect(Collectors.toSet());
    }


}

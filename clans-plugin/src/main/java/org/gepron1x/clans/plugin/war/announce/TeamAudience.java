package org.gepron1x.clans.plugin.war.announce;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.gepron1x.clans.plugin.war.Team;
import org.jetbrains.annotations.NotNull;

public final class TeamAudience implements ForwardingAudience {
    private final Team team;

    public TeamAudience(Team team) {

        this.team = team;
    }
    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return team.alive();
    }
}

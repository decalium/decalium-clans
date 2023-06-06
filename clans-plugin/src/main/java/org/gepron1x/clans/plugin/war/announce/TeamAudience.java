package org.gepron1x.clans.plugin.war.announce;

import net.kyori.adventure.audience.Audience;
import org.gepron1x.clans.api.chat.GroupAudience;
import org.gepron1x.clans.api.war.Team;
import org.jetbrains.annotations.NotNull;

public final class TeamAudience implements GroupAudience {
    private final Team team;

    public TeamAudience(Team team) {

        this.team = team;
    }
    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return team.alive();
    }
}

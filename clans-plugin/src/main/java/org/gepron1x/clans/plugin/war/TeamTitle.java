package org.gepron1x.clans.plugin.war;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public final class TeamTitle implements ComponentLike {

    private final Team team;

    public TeamTitle(Team team) {

        this.team = team;
    }
    @Override
    public @NotNull Component asComponent() {
        return Component.text()
                .append(team.clan().orElseThrow().displayName())
                .append(Component.space())
                .append(Component.text(team.alive().size()+"/"+team.members().size()))
                .build();
    }
}

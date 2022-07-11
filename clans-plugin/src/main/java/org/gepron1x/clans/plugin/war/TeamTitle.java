package org.gepron1x.clans.plugin.war;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.gepron1x.clans.plugin.chat.resolvers.ClanTagResolver;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.jetbrains.annotations.NotNull;

public final class TeamTitle implements ComponentLike {

    private final Team team;
    private final MessagesConfig messages;

    public TeamTitle(Team team, MessagesConfig messages) {

        this.team = team;
        this.messages = messages;
    }
    @Override
    public @NotNull Component asComponent() {
        return messages.war().bossBarFormat().with(ClanTagResolver.clan(team.clan().orElseThrow()))
                .with("alive", team.alive().size()).with("members", team.members().size()).asComponent();
    }
}

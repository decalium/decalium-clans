/*
 * decalium-clans
 * Copyright © 2023 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.plugin.war;

import com.google.common.base.MoreObjects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.gepron1x.clans.api.chat.ClanTagResolver;
import org.gepron1x.clans.api.war.Team;
import org.gepron1x.clans.plugin.config.messages.MessagesConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamTitle teamTitle = (TeamTitle) o;
        return team.equals(teamTitle.team) && messages.equals(teamTitle.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, messages);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("team", team)
                .toString();
    }
}

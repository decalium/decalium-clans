/*
 * decalium-clans
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.gepron1x.clans.api.chat.ClanTagResolver;
import org.gepron1x.clans.api.war.Team;
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

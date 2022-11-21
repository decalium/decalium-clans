/*
 * decalium-clans-rewrite
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans-rewrite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans-rewrite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans-rewrite. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.plugin.level;

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.api.edition.EmptyClanEdition;
import org.gepron1x.clans.api.exception.DescribingException;
import org.gepron1x.clans.plugin.config.messages.MessagesConfig;
import org.gepron1x.clans.plugin.config.settings.Levels;
import org.jetbrains.annotations.NotNull;

public final class LeveledEdition implements EmptyClanEdition {

    private final Clan clan;
    private final Levels.PerLevel perLevel;
    private final MessagesConfig messages;

    public LeveledEdition(Clan clan, Levels.PerLevel perLevel, MessagesConfig messages) {
        this.clan = clan;
        this.perLevel = perLevel;
        this.messages = messages;
    }

    @Override
    public ClanEdition addMember(@NotNull ClanMember member) {
        if(clan.members().size() >= perLevel.slots()) {
            throw new DescribingException(messages.level().tooManyHomes().with("slots", perLevel.slots()));
        }
        return this;
    }

    @Override
    public ClanEdition addHome(@NotNull ClanHome home) {
        if(clan.homes().size() >= perLevel.homes()) {
            throw new DescribingException(messages.level().tooManyHomes().with("homes", perLevel.homes()));
        }
        return this;
    }
}

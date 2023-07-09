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
package org.gepron1x.clans.plugin.clan;

import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.decoration.CombinedDecoration;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.OptionalInt;
import java.util.UUID;

public interface DelegatingClan extends DraftClan {


    DraftClan delegate();

    @Override
    @NotNull
    default String tag() {
        return delegate().tag();
    }

	@Override
	@NotNull
	default CombinedDecoration tagDecoration() {
		return delegate().tagDecoration();
	}

    @Override
    default @NotNull Component displayName() {
        return delegate().displayName();
    }

    @Override
    default @NotNull ClanMember owner() {
        return delegate().owner();
    }

    @Override
    default @NotNull @Unmodifiable Map<UUID, ? extends ClanMember> memberMap() {
        return delegate().memberMap();
    }

    @Override
    default @NotNull @Unmodifiable Map<String, ? extends ClanHome> homeMap() {
        return delegate().homeMap();
    }

    @Override
    @NotNull
    default DraftClan.Builder toBuilder() {
        return delegate().toBuilder();
    }

    @Override
    default OptionalInt statistic(@NotNull StatisticType type) {
        return delegate().statistic(type);
    }

    @Override
    @NotNull @Unmodifiable
    default Map<StatisticType, Integer> statistics() {
        return delegate().statistics();
    }

}

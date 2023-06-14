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
package org.gepron1x.clans.api.edition;

import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.edition.home.HomeEdition;
import org.gepron1x.clans.api.edition.member.MemberEdition;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public interface ClanEdition extends Edition<DraftClan> {

    @Override
    @NotNull
    default Class<DraftClan> getTarget() { return DraftClan.class; }

    ClanEdition rename(@NotNull Component displayName);

    ClanEdition setStatistic(@NotNull StatisticType type, int value);

    ClanEdition owner(@NotNull ClanMember owner);

    default ClanEdition setStatistics(@NotNull Map<StatisticType, Integer> statistics) {
        statistics.forEach(this::setStatistic);
        return this;
    }

    default ClanEdition upgrade() {
        return incrementStatistic(StatisticType.LEVEL);
    }


    ClanEdition addStatistics(@NotNull Map<StatisticType, Integer> statistics);

    ClanEdition incrementStatistic(@NotNull StatisticType type);
    ClanEdition removeStatistic(@NotNull StatisticType type);

    ClanEdition addMember(@NotNull ClanMember member);

    default ClanEdition addMembers(@NotNull Collection<ClanMember> members) {
        members.forEach(this::addMember);
        return this;
    }
    ClanEdition removeMember(@NotNull ClanMember member);
    ClanEdition editMember(@NotNull UUID uuid, @NotNull Consumer<MemberEdition> consumer);

    ClanEdition addHome(@NotNull ClanHome home);

    default ClanEdition addHomes(@NotNull Collection<ClanHome> homes) {
        homes.forEach(this::addHome);
        return this;
    }
    ClanEdition removeHome(@NotNull ClanHome home);
    ClanEdition editHome(@NotNull String name, @NotNull Consumer<HomeEdition> consumer);





}

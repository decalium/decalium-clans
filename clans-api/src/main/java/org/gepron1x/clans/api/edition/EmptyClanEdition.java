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
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.api.decoration.CombinedDecoration;
import org.gepron1x.clans.api.edition.home.HomeEdition;
import org.gepron1x.clans.api.edition.member.MemberEdition;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public interface EmptyClanEdition extends ClanEdition {

    @Override
    default ClanEdition rename(@NotNull Component displayName) {
        return this;
    }

	@Override
	default ClanEdition decoration(@NotNull CombinedDecoration decoration) {
		return this;
	}

    @Override
    default ClanEdition setStatistic(@NotNull StatisticType type, int value) {
        return this;
    }

    @Override
    default ClanEdition owner(@NotNull ClanMember owner) {
        return this;
    }

    @Override
    default ClanEdition addStatistics(@NotNull Map<StatisticType, Integer> statistics) {
        return this;
    }

    @Override
    default ClanEdition incrementStatistic(@NotNull StatisticType type) {
        return this;
    }

    @Override
    default ClanEdition removeStatistic(@NotNull StatisticType type) {
        return this;
    }

    @Override
    default ClanEdition addMember(@NotNull ClanMember member) {
        return this;
    }


    @Override
    default ClanEdition removeMember(@NotNull ClanMember member) {
        return this;
    }

    @Override
    default ClanEdition editMember(@NotNull UUID uuid, @NotNull Consumer<MemberEdition> consumer) {
        return this;
    }

    @Override
    default ClanEdition addHome(@NotNull ClanHome home) {
        return this;
    }

    @Override
    default ClanEdition removeHome(@NotNull ClanHome home) {
        return this;
    }

    @Override
    default ClanEdition editHome(@NotNull String name, @NotNull Consumer<HomeEdition> consumer) {
        return this;
    }

    interface EmptyHomeEdition extends HomeEdition {
        @Override
        default HomeEdition setIcon(@NotNull ItemStack icon) {
            return this;
        }

        @Override
        default HomeEdition move(@NotNull Location location) {
            return this;
        }

        @Override
        default HomeEdition rename(@NotNull Component displayName) {
            return this;
        }

        @Override
        default HomeEdition upgrade() {
            return this;
        }

        @Override
        default HomeEdition downgrade() {
            return this;
        }
    }

    interface EmptyMemberEdition extends MemberEdition {
        @Override
        default MemberEdition appoint(@NotNull ClanRole role) {
            return this;
        }
    }
}

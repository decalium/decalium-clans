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
package org.gepron1x.clans.plugin.storage.implementation.sql.edition;

import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.decoration.CombinedDecoration;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.api.edition.home.HomeEdition;
import org.gepron1x.clans.api.edition.member.MemberEdition;
import org.gepron1x.clans.api.exception.MemberAlreadyInClanException;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.gepron1x.clans.plugin.storage.implementation.sql.common.SavableHomes;
import org.gepron1x.clans.plugin.storage.implementation.sql.common.SavableMembers;
import org.gepron1x.clans.plugin.storage.implementation.sql.common.SavableStatistics;
import org.intellij.lang.annotations.Language;
import org.jdbi.v3.core.Handle;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public final class SqlClanEdition implements ClanEdition {
	@Language("SQL")
	private static final String UPDATE_DISPLAY_NAME = "UPDATE `clans` SET `display_name`=? WHERE `id`=?";
	@Language("SQL")
	private static final String DELETE_STATISTIC = "DELETE FROM `statistics` WHERE `clan_id`=? AND `type`=?";

	@Language("SQL")
	private static final String DELETE_MEMBER = "DELETE FROM `members` WHERE `uuid`=?";
	@Language("SQL")
	private static final String DELETE_HOME = "DELETE FROM `homes` WHERE `clan_id`=? AND `name`=?";

	@Language("SQL")
	private static final String UPDATE_OWNER = "UPDATE `clans` SET `owner`=? WHERE `id`=?";
	@Language("SQL")
	private static final String UPDATE_DECORATION = "UPDATE `clans` SET `decoration`=? WHERE `id`=?";
	private final Handle handle;
	private final int clanId;

	public SqlClanEdition(@NotNull Handle handle, int clanId) {
		this.handle = handle;
		this.clanId = clanId;
	}

	@Override
	public ClanEdition rename(@NotNull Component displayName) {
		handle.createUpdate(UPDATE_DISPLAY_NAME)
				.bind(0, displayName)
				.bind(1, clanId)
				.execute();
		return this;
	}

	@Override
	public ClanEdition decoration(@NotNull CombinedDecoration decoration) {
		handle.createUpdate(UPDATE_DECORATION).bind(0, decoration).bind(1, clanId).execute();
		return this;
	}

	@Override
	public ClanEdition setStatistic(@NotNull StatisticType type, int value) {
		return setStatistics(Map.of(type, value));
	}

	@Override
	public ClanEdition owner(@NotNull ClanMember owner) {
		handle.createUpdate(UPDATE_OWNER).bind(0, owner.uniqueId()).bind(1, clanId).execute();
		return this;
	}

	@Override
	public ClanEdition setStatistics(@NotNull Map<StatisticType, Integer> statistics) {
		new SavableStatistics(clanId, statistics).execute(handle);
		return this;
	}

	@Override
	public ClanEdition addStatistics(@NotNull Map<StatisticType, Integer> statistics) {
		return setStatistics(statistics);
	}

	@Override
	public ClanEdition incrementStatistic(@NotNull StatisticType type) {
		return addStatistics(Map.of(type, 1));
	}

	@Override
	public ClanEdition removeStatistic(@NotNull StatisticType type) {
		handle.createUpdate(DELETE_STATISTIC)
				.bind(0, clanId)
				.bind(1, type).execute();
		return this;

	}

	@Override
	public ClanEdition addMember(@NotNull ClanMember member) {
		return addMembers(Collections.singleton(member));
	}

	@Override
	public ClanEdition addMembers(@NotNull Collection<ClanMember> members) {
		if (new SavableMembers(clanId, members).execute(handle) < members.size())
			throw new MemberAlreadyInClanException();
		return this;
	}

	@Override
	public ClanEdition removeMember(@NotNull ClanMember member) {
		handle.createUpdate(DELETE_MEMBER).bind(0, member.uniqueId()).execute();
		return this;
	}

	@Override
	public ClanEdition editMember(@NotNull UUID member, @NotNull Consumer<MemberEdition> consumer) {
		consumer.accept(new SqlMemberEdition(handle, clanId, member));
		return this;
	}

	@Override
	public ClanEdition addHome(@NotNull ClanHome home) {
		return addHomes(Collections.singleton(home));
	}

	@Override
	public ClanEdition addHomes(@NotNull Collection<ClanHome> homes) {
		new SavableHomes(clanId, homes).execute(handle);
		return this;
	}

	@Override
	public ClanEdition removeHome(@NotNull ClanHome home) {
		handle.createUpdate(DELETE_HOME)
				.bind(0, clanId)
				.bind(1, home.name()).execute();
		return this;
	}

	@Override
	public ClanEdition editHome(@NotNull String name, @NotNull Consumer<HomeEdition> consumer) {
		consumer.accept(new SqlHomeEdition(handle, clanId, name));
		return this;
	}

}

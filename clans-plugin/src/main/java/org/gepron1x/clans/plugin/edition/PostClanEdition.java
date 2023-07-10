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
package org.gepron1x.clans.plugin.edition;

import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.api.edition.EmptyClanEdition;
import org.gepron1x.clans.api.edition.member.MemberEdition;
import org.gepron1x.clans.plugin.wg.WgRegionSet;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

public final class PostClanEdition implements EmptyClanEdition {
	private final WgRegionSet regionSet;

	public PostClanEdition(WgRegionSet regionSet) {
		this.regionSet = regionSet;
	}

	@Override
	public ClanEdition addMember(@NotNull ClanMember member) {
		regionSet.addMember(member.uniqueId());
		return this;
	}

	@Override
	public ClanEdition removeMember(@NotNull ClanMember member) {
		regionSet.removeMember(member.uniqueId());
		return this;
	}

	@Override
	public ClanEdition editMember(@NotNull UUID uuid, @NotNull Consumer<MemberEdition> consumer) {
		consumer.accept(new PostMemberEdition());
		return this;
	}

	private static class PostMemberEdition implements MemberEdition {


		@Override
		public MemberEdition appoint(@NotNull ClanRole role) {
			return this;
		}
	}
}

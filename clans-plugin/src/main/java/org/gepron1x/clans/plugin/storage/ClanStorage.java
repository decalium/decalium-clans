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
package org.gepron1x.clans.plugin.storage;

import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.IdentifiedDraftClan;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.api.repository.ClanCreationResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public interface ClanStorage {


	void initialize();

	void shutdown();

	@Nullable IdentifiedDraftClan loadClan(@NotNull String tag);

	@Nullable IdentifiedDraftClan loadClan(int id);

	@Nullable IdentifiedDraftClan loadUserClan(@NotNull UUID uuid);


	@NotNull Set<IdentifiedDraftClanImpl> loadClans();

	SaveResult saveClan(@NotNull DraftClan clan);

	void applyEdition(int id, @NotNull Consumer<ClanEdition> consumer);

	boolean removeClan(int id);

	record SaveResult(int id, ClanCreationResult.Status status) {
		public static final SaveResult ALREADY_EXISTS = new SaveResult(Integer.MIN_VALUE, ClanCreationResult.Status.ALREADY_EXISTS);
		public static final SaveResult MEMBERS_IN_OTHER_CLANS = new SaveResult(Integer.MIN_VALUE, ClanCreationResult.Status.MEMBERS_IN_OTHER_CLANS);

		public static SaveResult success(int id) {
			return new SaveResult(id, ClanCreationResult.Status.SUCCESS);
		}


	}


}

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
package org.gepron1x.clans.api.repository;

import org.gepron1x.clans.api.clan.Clan;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

public record ClanCreationResult(@Nullable Clan clan, @NotNull Status status) {

	private static final ClanCreationResult ALREADY_EXISTS = failure(Status.ALREADY_EXISTS);
	private static final ClanCreationResult MEMBERS_IN_OTHER_CLANS = failure(Status.MEMBERS_IN_OTHER_CLANS);

	public enum Status {
		SUCCESS,
		ALREADY_EXISTS,
		MEMBERS_IN_OTHER_CLANS;

		public boolean isSuccess() {
			return this == SUCCESS;
		}
	}

	public boolean isSuccess() {
		return clan != null;
	}

	public static ClanCreationResult success(@NotNull Clan clan) {
		return new ClanCreationResult(clan, Status.SUCCESS);
	}

	public void ifSuccess(Consumer<Clan> consumer) {
		if (clan != null) consumer.accept(clan);
	}

	public Clan orElseThrow() {
		if (clan == null) throw new NoSuchElementException("no clan present");
		return clan;
	}

	public Optional<Clan> asOptional() {
		return Optional.ofNullable(clan);
	}

	public static ClanCreationResult failure(@NotNull Status status) {
		return new ClanCreationResult(null, status);
	}

	public static ClanCreationResult alreadyExists() {
		return ALREADY_EXISTS;
	}

	public static ClanCreationResult membersInOtherClans() {
		return MEMBERS_IN_OTHER_CLANS;
	}
}

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
package org.gepron1x.clans.api.reference;

import com.google.common.base.MoreObjects;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Objects;
import java.util.Optional;

public final class TagClanReference implements ClanReference {
	private final CachingClanRepository repository;
	private final String tag;

	public TagClanReference(CachingClanRepository repository, String tag) {

		this.repository = repository;
		this.tag = tag;
	}

	@Override
	public @NotNull CentralisedFuture<Optional<Clan>> clan() {
		return this.repository.requestClan(this.tag);
	}

	@Override
	public Optional<Clan> cached() {
		return this.repository.clanIfCached(tag);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TagClanReference that = (TagClanReference) o;
		return tag.equals(that.tag);
	}

	@Override
	public int hashCode() {
		return Objects.hash(repository, tag);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("repository", repository)
				.add("tag", tag)
				.toString();
	}
}

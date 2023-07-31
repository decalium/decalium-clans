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
package org.gepron1x.clans.plugin;

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.repository.ClanCreationResult;
import org.gepron1x.clans.api.repository.ClanRepository;
import org.gepron1x.clans.api.repository.ClanTop;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public abstract class AdaptingClanRepository implements ClanRepository {

	private final ClanRepository repository;
	private final ClanDecorator decorator;

	public AdaptingClanRepository(ClanRepository repository, ClanDecorator decorator) {
		this.repository = repository;
		this.decorator = decorator;
	}


	@Override
	public @NotNull CentralisedFuture<ClanCreationResult> createClan(@NotNull DraftClan draftClan) {
		return this.repository.createClan(draftClan)
				.thenApply(result ->
						result.map(decorator)
				);
	}

	@Override
	public @NotNull ClanTop top() {
		return new Top();
	}

	@Override
	public @NotNull CentralisedFuture<Boolean> removeClan(@NotNull Clan clan) {
		return this.repository.removeClan(clan);
	}

	@Override
	public @NotNull CentralisedFuture<Optional<Clan>> requestClan(@NotNull String tag) {
		return this.repository.requestClan(tag).thenApply(optional -> optional.map(decorator));
	}

	@Override
	public @NotNull CentralisedFuture<Optional<Clan>> requestUserClan(@NotNull UUID uuid) {
		return this.repository.requestUserClan(uuid).thenApply(optional -> optional.map(decorator));
	}

	@Override
	public @NotNull CentralisedFuture<Stream<? extends Clan>> clans() {
		return this.repository.clans().thenApply(clans -> clans.map(decorator));
	}

	private final class Top implements ClanTop {

		@Override
		public CentralisedFuture<Stream<Clan>> sortBy(StatisticType type) {
			return repository.top().sortBy(type).thenApply(stream -> stream.map(AdaptingClanRepository.this::adapt));
		}

		@Override
		public CentralisedFuture<Stream<Clan>> sortBy(StatisticType type, int limit) {
			return repository.top().sortBy(type, limit).thenApply(stream -> stream.map(AdaptingClanRepository.this::adapt));
		}
	}

	protected Clan adapt(Clan clan) {
		return decorator.apply(clan);
	}

}

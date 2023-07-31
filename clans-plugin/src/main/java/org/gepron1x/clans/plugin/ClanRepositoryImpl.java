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
import org.gepron1x.clans.api.clan.IdentifiedDraftClan;
import org.gepron1x.clans.api.repository.ClanCreationResult;
import org.gepron1x.clans.api.repository.ClanRepository;
import org.gepron1x.clans.api.repository.ClanTop;
import org.gepron1x.clans.plugin.clan.ClanImpl;
import org.gepron1x.clans.plugin.storage.ClanStorage;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public final class ClanRepositoryImpl implements ClanRepository {
	private final ClanStorage storage;
	private final FactoryOfTheFuture futuresFactory;

	public ClanRepositoryImpl(@NotNull ClanStorage storage, @NotNull FactoryOfTheFuture futuresFactory) {
		this.storage = storage;
		this.futuresFactory = futuresFactory;
	}

	@Override
	public @NotNull CentralisedFuture<ClanCreationResult> createClan(@NotNull DraftClan draftClan) {
		return futuresFactory.supplyAsync(() -> storage.saveClan(draftClan)).thenApplySync(saveResult -> {
			if (saveResult.status() != ClanCreationResult.Status.SUCCESS) {
				return ClanCreationResult.failure(saveResult.status());
			}
			int id = saveResult.id();
			return ClanCreationResult.success(new ClanImpl(id, draftClan, this.storage, this.futuresFactory));
		});
	}

	@Override
	public @NotNull CentralisedFuture<Boolean> removeClan(@NotNull Clan clan) {
		return futuresFactory.supplyAsync(() -> this.storage.removeClan(clan.id()));
	}

	@Override
	public @NotNull CentralisedFuture<Optional<Clan>> requestClan(@NotNull String tag) {
		return futuresFactory.supplyAsync(() -> Optional.ofNullable(this.storage.loadClan(tag)).map(this::adapt));
	}

	@Override
	public @NotNull CentralisedFuture<Optional<Clan>> requestUserClan(@NotNull UUID uuid) {
		return futuresFactory.supplyAsync(() -> Optional.ofNullable(this.storage.loadUserClan(uuid)).map(this::adapt));
	}

	@Override
	public @NotNull ClanTop top() {
		return new ClanTopImpl(futuresFactory, storage);
	}

	@Override
	public @NotNull CentralisedFuture<Stream<? extends Clan>> clans() {
		return futuresFactory.supplyAsync(() -> this.storage.loadClans().stream().map(this::adapt));
	}

	Clan adapt(IdentifiedDraftClan draftClan) {
		return new ClanImpl(draftClan.id(), draftClan, this.storage, this.futuresFactory);
	}
}

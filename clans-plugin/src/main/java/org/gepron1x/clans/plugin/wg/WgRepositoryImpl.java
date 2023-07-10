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
package org.gepron1x.clans.plugin.wg;

import com.sk89q.worldguard.WorldGuard;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.shield.GlobalRegions;
import org.gepron1x.clans.plugin.AdaptingClanRepository;
import org.gepron1x.clans.plugin.config.Configs;
import org.gepron1x.clans.plugin.storage.RegionStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class WgRepositoryImpl extends AdaptingClanRepository implements CachingClanRepository {


	public record AsyncRegionStorage(FactoryOfTheFuture futures, RegionStorage storage) {
		public CentralisedFuture<?> saveAsync() {
			return futures.runAsync(storage::save);
		}
	}


	private final CachingClanRepository clanRepository;
	private final WorldGuard worldGuard;
	private final GlobalRegions regions;
	private final AsyncRegionStorage regionStorage;

	public WgRepositoryImpl(CachingClanRepository repository, Configs configs, WorldGuard worldGuard, GlobalRegions regions, AsyncRegionStorage regionStorage) {
		super(repository, clan -> new WgClan(clan, configs, worldGuard.getPlatform().getRegionContainer(), regions.clanRegions(clan)));
		this.clanRepository = repository;
		this.worldGuard = worldGuard;
		this.regions = regions;
		this.regionStorage = regionStorage;
	}

	@Override
	public @NotNull CentralisedFuture<Boolean> removeClan(@NotNull Clan clan) {
		return regionStorage.saveAsync().thenCompose($ -> super.removeClan(clan)).thenApplySync(bool -> {
			regions.clanRegions(clan).clear();
			return bool;
		});
	}

	@Override
	public Optional<Clan> userClanIfCached(@NotNull UUID uuid) {
		return clanRepository.userClanIfCached(uuid).map(this::adapt);
	}

	@Override
	public Optional<Clan> clanIfCached(@NotNull String tag) {
		return clanRepository.clanIfCached(tag).map(this::adapt);
	}

	@Override
	public @UnmodifiableView Collection<Clan> cachedClans() {
		return clanRepository.cachedClans().stream().map(this::adapt).collect(Collectors.toUnmodifiableSet());
	}
}

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
package org.gepron1x.clans.plugin.cache;

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.repository.ClanCreationResult;
import org.gepron1x.clans.api.repository.ClanRepository;
import org.gepron1x.clans.api.repository.ClanTop;
import org.gepron1x.clans.plugin.AdaptingClanRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public final class CachingClanRepositoryImpl extends AdaptingClanRepository implements CachingClanRepository {


	private final FactoryOfTheFuture futuresFactory;
	private final ClanRepository repository;
	private final ClanCache cache;

	public CachingClanRepositoryImpl(ClanRepository repository, FactoryOfTheFuture futuresFactory, ClanCache cache) {
		super(repository, clan -> new CachingClan(clan, cache, futuresFactory));
		this.futuresFactory = futuresFactory;
		this.repository = repository;
		this.cache = cache;
	}


	@Override
	public @NotNull CentralisedFuture<ClanCreationResult> createClan(@NotNull DraftClan draftClan) {
		if (cache.isCached(draftClan.tag())) return futuresFactory.completedFuture(ClanCreationResult.alreadyExists());
		for(ClanMember member : draftClan.members()) {
			if(cache.getUserClan(member.uniqueId()) != null) return futuresFactory.completedFuture(ClanCreationResult.membersInOtherClans());
		}
		return repository.createClan(draftClan).thenApply(result -> {
			return result.map(clan -> {
				var c = new CachingClan(clan, cache, futuresFactory);
				cache.cacheClan(c);
				return c;
			});
		});
	}

	@Override
	public @NotNull CentralisedFuture<Boolean> removeClan(@NotNull Clan clan) {
		return this.repository.removeClan(clan).thenApply(bool -> {
			if (bool) cache.removeClan(clan.tag());
			return bool;
		});
	}

	@Override
	public @NotNull CentralisedFuture<Optional<Clan>> requestClan(@NotNull String tag) {
		Clan clan = cache.getClan(tag);
		if (clan != null) {
			return this.futuresFactory.completedFuture(Optional.of(clan));
		}
		return super.requestClan(tag);
	}

	@Override
	public @NotNull ClanTop top() {
		return repository.top(); // no need to cache
	}

	@Override
	public @NotNull CentralisedFuture<Optional<Clan>> requestUserClan(@NotNull UUID uuid) {
		Clan clan = cache.getUserClan(uuid);
		if (clan != null) return this.futuresFactory.completedFuture(Optional.of(clan));
		return super.requestUserClan(uuid);
	}


	@Override
	public Optional<Clan> userClanIfCached(@NotNull UUID uuid) {
		return Optional.ofNullable(cache.getUserClan(uuid));
	}

	@Override
	public Optional<Clan> clanIfCached(@NotNull String tag) {
		return Optional.ofNullable(cache.getClan(tag));
	}

	@Override
	public @UnmodifiableView Collection<Clan> cachedClans() {
		return cache.getClans();
	}


}

package org.gepron1x.clans.plugin;

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.IdentifiedDraftClan;
import org.gepron1x.clans.api.repository.ClanTop;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.gepron1x.clans.plugin.clan.ClanImpl;
import org.gepron1x.clans.plugin.storage.ClanStorage;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.stream.Stream;

public final class ClanTopImpl implements ClanTop {

	private final FactoryOfTheFuture futures;
	private final ClanStorage storage;

	public ClanTopImpl(FactoryOfTheFuture futures, ClanStorage storage) {

		this.futures = futures;
		this.storage = storage;
	}
	@Override
	public CentralisedFuture<Stream<Clan>> sortBy(StatisticType type) {
		return futures.supplyAsync(() -> storage.top().top(type)).thenApply(list -> list.stream()
				.map(this::adapt));
	}

	@Override
	public CentralisedFuture<Stream<Clan>> sortBy(StatisticType type, int limit) {
		return futures.supplyAsync(() -> storage.top().top(type, limit)).thenApply(list -> list.stream().map(this::adapt));
	}

	private Clan adapt(IdentifiedDraftClan clan) {
		return new ClanImpl(clan.id(), clan, storage, futures);
	}
}

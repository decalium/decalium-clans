package org.gepron1x.clans.plugin.papi;

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.exception.ExceptionHandler;
import org.gepron1x.clans.api.repository.ClanTop;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.jetbrains.annotations.Nullable;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ClanTopCache implements Runnable {


	private final Map<StatisticType, List<Clan>> cache = new ConcurrentHashMap<>();
	private final ClanTop top;
	private final int limit;



	public ClanTopCache(ClanTop top, int limit) {
		this.top = top;
		this.limit = limit;
	}


	@Override
	public void run() {
		cache.keySet().forEach(this::cache);
	}

	public CentralisedFuture<?> cache(StatisticType type) {
		return top.sortBy(type, this.limit).thenAccept(stream -> cache.put(type, stream.toList()))
				.exceptionally(ExceptionHandler.catchException(Throwable::printStackTrace));
	}

	public List<Clan> top(StatisticType type) {
		return cache.computeIfAbsent(type, key -> List.of());
	}

	public @Nullable Clan top(StatisticType type, int position) {
		var top = top(type);
		if(top.size() <= position) return null;
		return top.get(position);
	}
}

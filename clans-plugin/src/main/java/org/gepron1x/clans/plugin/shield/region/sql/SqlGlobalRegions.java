package org.gepron1x.clans.plugin.shield.region.sql;

import org.bukkit.block.Block;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.reference.TagClanReference;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.shield.ClanRegions;
import org.gepron1x.clans.api.shield.GlobalRegions;
import org.gepron1x.clans.plugin.shield.region.ClanRegionsImpl;
import org.gepron1x.clans.plugin.storage.implementation.sql.SqlQueue;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class SqlGlobalRegions implements GlobalRegions {

	static final String REGION_ID = "decaliumclans_region_id";


	private final Map<Integer, ClanRegions> regions;
	private final SqlQueue queue;

	private final AtomicInteger idCounter;
	private final CachingClanRepository repository;



	public SqlGlobalRegions(Map<Integer, ClanRegions> regions, SqlQueue queue, AtomicInteger idCounter, CachingClanRepository repository) {

		this.regions = regions;
		this.queue = queue;
		this.idCounter = idCounter;
		this.repository = repository;
	}

	@Override
	public Set<ClanRegions> listRegions() {
		return Set.copyOf(regions.values());
	}

	@Override
	public ClanRegions clanRegions(Clan clan) {
		return regions.computeIfAbsent(clan.id(), id -> {
			return new SqlClanRegions(
					new ClanRegionsImpl(new HashMap<>(), new TagClanReference(repository, clan.tag()), idCounter), id, queue);
		});
	}

	@Override
	public void remove(int id) {
		for(ClanRegions regions : regions.values()) {
			regions.region(id).ifPresent(regions::remove);
		}
		queue.add(handle -> handle.execute("DELETE FROM `regions` WHERE `id`=?", id));
	}

	@Override
	public Optional<Integer> regionId(Block block) {
		return Optional.empty(); // not supported really;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SqlGlobalRegions that = (SqlGlobalRegions) o;
		return Objects.equals(regions, that.regions) && Objects.equals(idCounter, that.idCounter);
	}

	@Override
	public int hashCode() {
		return Objects.hash(regions, idCounter);
	}
}

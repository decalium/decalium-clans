package org.gepron1x.clans.plugin.storage.implementation.sql;

import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.reference.TagClanReference;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.shield.ClanRegion;
import org.gepron1x.clans.api.shield.ClanRegions;
import org.gepron1x.clans.api.shield.GlobalRegions;
import org.gepron1x.clans.plugin.shield.region.ClanRegionsImpl;
import org.gepron1x.clans.plugin.shield.region.sql.SqlClanRegions;
import org.gepron1x.clans.plugin.shield.region.sql.SqlGlobalRegions;
import org.gepron1x.clans.plugin.storage.RegionStorage;
import org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row.ClanRegionMapper;
import org.jdbi.v3.core.Jdbi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class SqlRegionStorage implements RegionStorage {

	private final Jdbi jdbi;
	private final CachingClanRepository repository;
	private final SqlQueue queue;

	private record TagAndId(String tag, int id) {}

	public SqlRegionStorage(Jdbi jdbi, CachingClanRepository repository, SqlQueue queue) {

		this.jdbi = jdbi;
		this.repository = repository;
		this.queue = queue;
	}

	@Override
	public GlobalRegions loadRegions() {
		return jdbi.withHandle(handle -> {
			var regionMap = handle.createQuery("SELECT * FROM `regions_simple`")
					.registerColumnMapper(ClanReference.class, (r, i, ctx) -> new TagClanReference(repository, r.getString(i)))
					.registerRowMapper(new ClanRegionMapper())
					.reduceRows(new LinkedHashMap<TagAndId, Map<Integer, ClanRegion>>(), (map, rowView) -> {
						Map<Integer, ClanRegion> regions = map.computeIfAbsent(
								new TagAndId(rowView.getColumn("clan_tag", String.class), rowView.getColumn("clan_id", Integer.class)),
								tagId -> new LinkedHashMap<>());
						ClanRegion region = rowView.getRow(ClanRegion.class);
						regions.put(region.id(), region);
						return map;
					});
			AtomicInteger idCounter = new AtomicInteger(
					handle.createQuery("SELECT MAX(`id`) AS max_id FROM `regions`").map((r, ctx) -> r.getInt(1)).findFirst().orElse(1)
			);
			Map<Integer, ClanRegions> clanRegions = new LinkedHashMap<>();
			for(var entry : regionMap.entrySet()) {
				ClanReference reference = new TagClanReference(repository, entry.getKey().tag);
				clanRegions.put(entry.getKey().id,
						new SqlClanRegions(new ClanRegionsImpl(entry.getValue(), reference, idCounter),
								entry.getKey().id, queue)
				);
			}
			return new SqlGlobalRegions(clanRegions, queue, idCounter, repository);
		});
	}

	@Override
	public void save() {
		queue.run(jdbi);
	}
}

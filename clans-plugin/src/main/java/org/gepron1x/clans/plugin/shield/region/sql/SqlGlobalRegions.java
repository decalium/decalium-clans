package org.gepron1x.clans.plugin.shield.region.sql;

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.shield.ClanRegion;
import org.gepron1x.clans.api.shield.ClanRegions;
import org.gepron1x.clans.api.shield.GlobalRegions;
import org.gepron1x.clans.plugin.storage.implementation.sql.AsyncJdbi;
import org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row.ClanRegionMapper;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Collection;

public final class SqlGlobalRegions implements GlobalRegions {

	private final AsyncJdbi jdbi;

	public SqlGlobalRegions(AsyncJdbi jdbi) {

		this.jdbi = jdbi;
	}
	@Override
	public CentralisedFuture<Collection<ClanRegion>> listRegions() {
		return jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM `regions_simple`").map(new ClanRegionMapper(jdbi)).list());
	}

	@Override
	public ClanRegions clanRegions(Clan clan) {
		return new SqlClanRegions(clan, jdbi);
	}
}

package org.gepron1x.clans.plugin.shield.region.wg;

import com.sk89q.worldguard.WorldGuard;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.shield.ClanRegion;
import org.gepron1x.clans.api.shield.ClanRegions;
import org.gepron1x.clans.api.shield.GlobalRegions;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Collection;

public final class WgGlobalRegions implements GlobalRegions {

	private final GlobalRegions regions;
	private final WorldGuard worldGuard;

	public WgGlobalRegions(GlobalRegions regions, WorldGuard worldGuard) {

		this.regions = regions;
		this.worldGuard = worldGuard;
	}
	@Override
	public CentralisedFuture<Collection<ClanRegion>> listRegions() {
		return regions.listRegions();
	}

	@Override
	public ClanRegions clanRegions(Clan clan) {
		return regions.clanRegions(clan);
	}
}

package org.gepron1x.clans.plugin.shield.region.wg;

import com.sk89q.worldguard.WorldGuard;
import org.bukkit.Location;
import org.gepron1x.clans.api.shield.ClanRegion;
import org.gepron1x.clans.api.shield.ClanRegions;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Optional;
import java.util.Set;

public final class WgClanRegions implements ClanRegions {

	private final ClanRegions regions;
	private final WorldGuard worldGuard;

	public WgClanRegions(ClanRegions regions, WorldGuard worldGuard) {

		this.regions = regions;
		this.worldGuard = worldGuard;
	}
	@Override
	public CentralisedFuture<Set<ClanRegion>> regions() {
		return regions.regions();
	}

	@Override
	public CentralisedFuture<Optional<ClanRegion>> region(int id) {
		return regions.region(id);
	}

	@Override
	public CentralisedFuture<ClanRegion> create(Location location) {
		return regions.create(location);
	}
}

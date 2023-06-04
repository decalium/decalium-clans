package org.gepron1x.clans.plugin.shield.region;

import org.bukkit.Location;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.shield.ClanRegion;
import org.gepron1x.clans.api.shield.ClanRegions;
import org.gepron1x.clans.api.shield.Shield;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public final class ClanRegionsImpl implements ClanRegions {

	private final Map<Integer, ClanRegion> regions;
	private final ClanReference clan;
	private final AtomicInteger idCounter;

	public ClanRegionsImpl(Map<Integer, ClanRegion> regions, ClanReference clan, AtomicInteger idCounter) {

		this.regions = regions;
		this.clan = clan;
		this.idCounter = idCounter;
	}

	@Override
	public Set<ClanRegion> regions() {
		return Set.copyOf(regions.values());
	}

	@Override
	public Optional<ClanRegion> region(int id) {
		return Optional.ofNullable(regions.get(id));
	}

	@Override
	public Optional<ClanRegion> region(Location location) {
		return Optional.empty();
	}

	@Override
	public void remove(ClanRegion region) {
		regions.remove(region.id());
	}

	@Override
	public ClanRegion create(Location location) {
		RegionImpl region = new RegionImpl(idCounter.incrementAndGet(), clan, location.clone(), Shield.NONE);
		regions.put(region.id(), region);
		return region;
	}
}

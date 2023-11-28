package org.gepron1x.clans.plugin.shield.region;

import com.google.common.base.MoreObjects;
import org.bukkit.Location;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.region.ClanRegion;
import org.gepron1x.clans.api.region.ClanRegions;
import org.gepron1x.clans.api.region.Shield;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class ClanRegionsImpl implements ClanRegions {

	private final Map<Integer, ClanRegion> globalRegionMap;
	private final Map<Integer, ClanRegion> regions;
	private final ClanReference clan;
	private final AtomicInteger idCounter;

	public ClanRegionsImpl(Map<Integer, ClanRegion> globalRegionMap, Map<Integer, ClanRegion> regions, ClanReference clan, AtomicInteger idCounter) {
		this.globalRegionMap = globalRegionMap;

		this.regions = regions;
		this.clan = clan;
		this.idCounter = idCounter;
	}

	@Override
	public Collection<ClanRegion> regions() {
		return Collections.unmodifiableCollection(regions.values());
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
		globalRegionMap.put(region.id(), region);
		regions.put(region.id(), region);
		return region;
	}

	@Override
	public void clear() {
		this.regions.clear();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ClanRegionsImpl that = (ClanRegionsImpl) o;
		return Objects.equals(regions, that.regions) && Objects.equals(clan, that.clan) && Objects.equals(idCounter, that.idCounter);
	}

	@Override
	public int hashCode() {
		return Objects.hash(regions, clan, idCounter);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("regions", regions)
				.add("clan", clan)
				.add("idCounter", idCounter)
				.toString();
	}
}

package org.gepron1x.clans.plugin.shield.region.wg;

import com.google.common.base.MoreObjects;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import eu.decentsoftware.holograms.api.DHAPI;
import org.bukkit.Location;
import org.gepron1x.clans.api.shield.ClanRegion;
import org.gepron1x.clans.api.shield.ClanRegions;
import org.gepron1x.clans.api.shield.GlobalRegions;
import org.gepron1x.clans.api.shield.RegionOverlapsException;
import org.gepron1x.clans.plugin.config.Configs;
import org.gepron1x.clans.plugin.wg.ProtectedRegionOf;
import org.gepron1x.clans.plugin.wg.WgExtension;
import org.gepron1x.clans.plugin.wg.WgRegionSet;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public final class WgClanRegions implements ClanRegions {

	private final ClanRegions regions;
	private final GlobalRegions global;
	private final RegionContainer container;
	private final Configs configs;

	public WgClanRegions(ClanRegions regions, GlobalRegions global, RegionContainer container, Configs configs) {

		this.regions = regions;
		this.global = global;
		this.container = container;
		this.configs = configs;
	}



	@Override
	public Set<ClanRegion> regions() {
		return regions.regions().stream().map(r -> new WgClanRegion(r, container, configs)).collect(Collectors.toSet());
	}

	@Override
	public Optional<ClanRegion> region(int id) {
		return regions.region(id).map(r -> new WgClanRegion(r, container, configs));
	}

	@Override
	public Optional<ClanRegion> region(Location location) {
		return container.createQuery().getApplicableRegions(BukkitAdapter.adapt(location)).getRegions().stream()
				.map(r -> r.getFlag(WgExtension.REGION_ID)).filter(Objects::nonNull).findAny()
				.map(this::region).orElseGet(() -> regions.region(location));
	}

	@Override
	public void remove(ClanRegion region) {
		regions.remove(region);
		new ProtectedRegionOf(container, region).remove();
		DHAPI.removeHologram(WgExtension.regionName(region));
	}

	@Override
	public ClanRegion create(Location location) {
		ClanRegion r = regions.create(location);
		ProtectedRegion region = new RegionCreation(configs, r).create();
		RegionManager manager = requireNonNull(container.get(BukkitAdapter.adapt(location.getWorld())));
		var iterator = global.listRegions().stream().flatMap(regions -> regions.regions().stream())
				.filter(region1 -> Objects.equals(region1.location().getWorld(), location.getWorld())).iterator();
		region.getIntersectingRegions(new WgRegionSet(container, () -> iterator).protectedRegions()).stream()
				.filter(overlapping -> overlapping.getFlag(WgExtension.REGION_ID) != null).findAny()
				.ifPresent($ -> {
					 remove(r);
					 throw new RegionOverlapsException(r);
				});
		manager.addRegion(region);
		r.clan().ifPresent(clan -> {
			var holo = new RegionHologram(r, clan, configs);
			region.setFlag(WgExtension.CLAN, clan.tag());
			holo.update();
			holo.update();
		});

		return new WgClanRegion(r, container, configs);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		WgClanRegions that = (WgClanRegions) o;
		return Objects.equals(regions, that.regions) && Objects.equals(global, that.global);
	}

	@Override
	public int hashCode() {
		return Objects.hash(regions, global);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("regions", regions)
				.toString();
	}
}

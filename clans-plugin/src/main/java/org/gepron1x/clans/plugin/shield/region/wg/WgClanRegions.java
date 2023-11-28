package org.gepron1x.clans.plugin.shield.region.wg;

import com.google.common.base.MoreObjects;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import eu.decentsoftware.holograms.api.DHAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.region.ClanRegion;
import org.gepron1x.clans.api.region.ClanRegions;
import org.gepron1x.clans.api.region.GlobalRegions;
import org.gepron1x.clans.api.region.RegionOverlapsException;
import org.gepron1x.clans.plugin.config.Configs;
import org.gepron1x.clans.plugin.shield.region.RegionBlock;
import org.gepron1x.clans.plugin.wg.ProtectedRegionOf;
import org.gepron1x.clans.plugin.wg.WgExtension;
import org.gepron1x.clans.plugin.wg.WgRegionSet;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
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
	public Collection<ClanRegion> regions() {
		return regions.regions().stream().map(r -> new WgClanRegion(r, container, configs)).collect(Collectors.toUnmodifiableList());
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
		RegionBlock.remove(region.location().getBlock());
	}

	@Override
	public ClanRegion create(Location location) {
		var dummy = RegionCreation.create("dummy", location, configs.config().homes().homeRegionRadius());
		if (checkIntersecting(dummy, location)) throw new RegionOverlapsException();

		ClanRegion r = regions.create(location);

		ProtectedRegion region = new RegionCreation(configs, r).create();
		r.clan().cached().ifPresent(clan -> clan.memberMap().keySet().forEach(region.getMembers()::addPlayer));
		RegionManager manager = requireNonNull(container.get(BukkitAdapter.adapt(location.getWorld())));
		manager.addRegion(region);
		r.clan().ifPresent(clan -> {
			var holo = new RegionHologram(r, clan, configs);
			holo.update();
		});
		RegionBlock.set(location.getBlock(), r.id());
		return new WgClanRegion(r, container, configs);
	}

	@Override
	public void clear() {
		new WgRegionSet(container, regions.regions()).clear();
		regions.forEach(r -> {
			DHAPI.removeHologram(WgExtension.regionName(r));
			r.location().getBlock().setType(Material.AIR);
			RegionBlock.remove(r.location().getBlock());
		});
		regions.clear();
	}

	public void updateRegions() {
		Map<ClanReference, CentralisedFuture<Optional<Clan>>> cache = new ConcurrentHashMap<>();
		for (ClanRegion region : regions.regions()) {

			var regionOf = new ProtectedRegionOf(container, region);
			ProtectedRegion pr = regionOf.region().orElseGet(() -> {
				ProtectedRegion protectedRegion = new RegionCreation(configs, region).create();
				regionOf.regionManager().ifPresent(manager -> manager.addRegion(protectedRegion));
				RegionBlock.set(region.location().getBlock(), region.id());
				return protectedRegion;
			});
			cache.computeIfAbsent(region.clan(), ClanReference::clan).thenAccept(o -> o.ifPresent(
					clan -> {
						pr.getMembers().clear();
						clan.memberMap().keySet().forEach(pr.getMembers()::addPlayer);
					}
			));
		}
	}

	private boolean checkIntersecting(ProtectedRegion region, Location location) {
		return container.get(BukkitAdapter.adapt(location.getWorld())).getApplicableRegions(region).size() != 0;
		/* Iterator<ClanRegion> iterator = global.listRegions().stream().flatMap(regions -> regions.regions().stream())
				.filter(region1 -> Objects.equals(region1.location().getWorld(), location.getWorld())).iterator();
		return region.getIntersectingRegions(new WgRegionSet(container, () -> iterator).protectedRegions()).stream()
				.findFirst().isPresent(); */
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

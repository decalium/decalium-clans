package org.gepron1x.clans.plugin.shield.region.wg;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import eu.decentsoftware.holograms.api.DHAPI;
import org.bukkit.Location;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.shield.ClanRegion;
import org.gepron1x.clans.api.shield.ClanRegions;
import org.gepron1x.clans.plugin.config.Configs;
import org.gepron1x.clans.plugin.wg.ProtectedRegionOf;
import org.gepron1x.clans.plugin.wg.WgExtension;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public final class WgClanRegions implements ClanRegions {

	private final ClanRegions regions;
	private final Clan clan;
	private final RegionContainer container;
	private final Configs configs;

	public WgClanRegions(ClanRegions regions, Clan clan, RegionContainer container, Configs configs) {

		this.regions = regions;
		this.clan = clan;
		this.container = container;
		this.configs = configs;
	}
	@Override
	public CentralisedFuture<Set<ClanRegion>> regions() {
		return regions.regions().thenApply(regions -> regions.stream().map(r -> new WgClanRegion(r, container, configs)).collect(Collectors.toSet()));
	}

	@Override
	public CentralisedFuture<Optional<ClanRegion>> region(int id) {
		return regions.region(id).thenApply(o -> o.map(r -> new WgClanRegion(r, container, configs)));
	}

	@Override
	public CentralisedFuture<?> remove(ClanRegion region) {
		return regions.remove(region).thenAccept($ -> {
			new ProtectedRegionOf(container, region).remove();
			DHAPI.removeHologram(WgExtension.regionName(region));
		});
	}

	@Override
	public CentralisedFuture<ClanRegion> create(Location location) {
		return regions.create(location).thenApplySync(r -> {
			new ProtectedRegionOf(container, r).region().orElseGet(() -> {
				ProtectedRegion region = new RegionCreation(configs, r).create();
				clan.memberMap().keySet().forEach(region.getMembers()::addPlayer);
				new RegionHologram(r, clan, configs).update();
				requireNonNull(container.get(BukkitAdapter.adapt(location.getWorld()))).addRegion(region);
				region.setFlag(WgExtension.CLAN, clan.tag());
				return region;
			});
			return new WgClanRegion(r, container, configs);
		});
	}
}

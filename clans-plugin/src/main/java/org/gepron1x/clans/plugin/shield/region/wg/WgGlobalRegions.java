package org.gepron1x.clans.plugin.shield.region.wg;

import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.block.Block;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.region.ClanRegion;
import org.gepron1x.clans.api.region.ClanRegions;
import org.gepron1x.clans.api.region.GlobalRegions;
import org.gepron1x.clans.plugin.config.Configs;
import org.gepron1x.clans.plugin.shield.region.RegionBlock;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class WgGlobalRegions implements GlobalRegions {

	private final GlobalRegions regions;
	private final RegionContainer container;
	private final Configs configs;

	public WgGlobalRegions(GlobalRegions regions, RegionContainer container, Configs configs) {

		this.regions = regions;
		this.container = container;
		this.configs = configs;
	}

	@Override
	public Set<ClanRegions> listRegions() {
		return wgClanRegions().collect(Collectors.toUnmodifiableSet());
	}

	private Stream<WgClanRegions> wgClanRegions() {
		return regions.listRegions().stream().map(r -> new WgClanRegions(r, regions, container, configs));
	}

	public void update() {
		wgClanRegions().forEach(WgClanRegions::updateRegions);
	}

	@Override
	public ClanRegions clanRegions(Clan clan) {
		return new WgClanRegions(regions.clanRegions(clan), regions, container, configs);
	}

	@Override
	public Optional<ClanRegion> region(int id) {
		return this.regions.region(id).map(region -> new WgClanRegion(region, container, configs));
	}

	@Override
	public void remove(int id) {
		for (ClanRegions regions : listRegions()) {
			regions.region(id).ifPresent(regions::remove);
		}
		regions.remove(id);
	}

	@Override
	public Optional<Integer> regionId(Block block) {
		return RegionBlock.get(block);
	}
}

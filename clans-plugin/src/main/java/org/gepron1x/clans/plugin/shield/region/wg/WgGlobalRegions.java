package org.gepron1x.clans.plugin.shield.region.wg;

import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.shield.ClanRegions;
import org.gepron1x.clans.api.shield.GlobalRegions;
import org.gepron1x.clans.plugin.config.Configs;

import java.util.Set;
import java.util.stream.Collectors;

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
		return regions.listRegions().stream().map(r -> new WgClanRegions(r, regions, container, configs))
				.collect(Collectors.toUnmodifiableSet());
	}
	@Override
	public ClanRegions clanRegions(Clan clan) {
		return new WgClanRegions(regions.clanRegions(clan), regions, container, configs);
	}

	@Override
	public void remove(int id) {
		for(ClanRegions regions : listRegions()) {
			regions.region(id).ifPresent(regions::remove);
		}
		regions.remove(id);
	}
}

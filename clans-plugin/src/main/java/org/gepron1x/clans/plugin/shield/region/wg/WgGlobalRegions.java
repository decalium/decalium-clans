package org.gepron1x.clans.plugin.shield.region.wg;

import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.shield.ClanRegion;
import org.gepron1x.clans.api.shield.ClanRegions;
import org.gepron1x.clans.api.shield.GlobalRegions;
import org.gepron1x.clans.plugin.config.Configs;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Collection;

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
	public CentralisedFuture<? extends Collection<ClanRegion>> listRegions() {
		return regions.listRegions().thenApply(regions -> regions.stream().
				<ClanRegion>map(r -> new WgClanRegion(r, container, configs)).toList());
	}



	@Override
	public ClanRegions clanRegions(Clan clan) {
		return new WgClanRegions(regions.clanRegions(clan), clan, container, configs);
	}

	@Override
	public CentralisedFuture<?> remove(int id) {
		return regions.remove(id).thenAccept($ -> {
			/* new ProtectedRegionOf(container, region).remove();
			DHAPI.removeHologram(WgExtension.regionName(region)); */
		});
	}
}

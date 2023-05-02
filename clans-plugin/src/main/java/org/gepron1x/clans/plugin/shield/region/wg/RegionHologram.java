package org.gepron1x.clans.plugin.shield.region.wg;

import org.gepron1x.clans.api.chat.ClanTagResolver;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.shield.ClanRegion;
import org.gepron1x.clans.plugin.config.Configs;
import org.gepron1x.clans.plugin.util.hologram.ClanHologram;
import org.gepron1x.clans.plugin.util.hologram.DecentClanHologram;
import org.gepron1x.clans.plugin.wg.WgExtension;

public final class RegionHologram {

	private final ClanRegion region;
	private final Clan clan;
	private final Configs configs;

	public RegionHologram(ClanRegion region, Clan clan, Configs configs) {

		this.region = region;
		this.clan = clan;
		this.configs = configs;
	}


	public ClanHologram hologram() {
		return DecentClanHologram.createIfAbsent(WgExtension.regionName(region), region.location().clone().add(0, 2, 0));
	}

	public void update() {
		hologram().lines(configs.config().region().hologramFormat().stream()
				.map(m -> m.with("clan", ClanTagResolver.clan(clan)).booleanState("shield_active", !region.shield().expired()).asComponent()).toList()
		);
	}

}

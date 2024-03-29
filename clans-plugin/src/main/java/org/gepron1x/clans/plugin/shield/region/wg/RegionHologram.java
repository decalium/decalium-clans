package org.gepron1x.clans.plugin.shield.region.wg;

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Location;
import org.gepron1x.clans.api.chat.BooleanStateResolver;
import org.gepron1x.clans.api.chat.ClanTagResolver;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.region.ClanRegion;
import org.gepron1x.clans.plugin.config.Configs;
import org.gepron1x.clans.plugin.util.hologram.ClanHologram;
import org.gepron1x.clans.plugin.util.hologram.DecentClanHologram;
import org.gepron1x.clans.plugin.wg.WgExtension;

import java.util.Objects;

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
		var config = configs.config().region().hologram();
		Location location = region.location().clone().add(0.5, 0, 0.5).add(config.offsetX(), config.offsetY(), config.offsetZ());
		return DecentClanHologram.createIfAbsent(WgExtension.regionName(region), location);
	}

	public void update() {
		hologram().lines(configs.config().region().hologram().format(), TagResolver.builder().resolver(ClanTagResolver.clan(clan))
				.resolver(new BooleanStateResolver("shield_active", region.shield().active())).build());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RegionHologram that = (RegionHologram) o;
		return Objects.equals(region, that.region) && Objects.equals(clan, that.clan);
	}

	@Override
	public int hashCode() {
		return Objects.hash(region, clan);
	}
}

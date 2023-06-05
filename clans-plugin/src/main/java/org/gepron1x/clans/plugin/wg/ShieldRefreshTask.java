/*
 * decalium-clans
 * Copyright © 2023 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.plugin.wg;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.scheduler.BukkitRunnable;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.shield.ClanRegion;
import org.gepron1x.clans.api.shield.ClanRegions;
import org.gepron1x.clans.api.shield.GlobalRegions;
import org.gepron1x.clans.plugin.config.Configs;
import org.gepron1x.clans.plugin.shield.region.wg.RegionHologram;

import java.time.Duration;
import java.util.Optional;

public final class ShieldRefreshTask extends BukkitRunnable {

	private final GlobalRegions regions;
	private final RegionContainer container;
	private final Configs configs;

	private final AsyncLoadingCache<ClanReference, Optional<Clan>> clanCache;



	public ShieldRefreshTask(GlobalRegions regions, RegionContainer container, Configs configs) {
		this.regions = regions;
		this.container = container;
		this.configs = configs;
		this.clanCache = Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(2)).buildAsync((c, e) -> c.clan());
	}
    @Override
    public void run() {
       for(ClanRegions regions : regions.listRegions()) for (ClanRegion region : regions) {
		   var protectedRegion = new ProtectedRegionOf(container, region);
		   if(region.shield().expired() &&
				   protectedRegion.region().map(r -> r.getFlag(WgExtension.SHIELD_ACTIVE)).orElse(false)) {
			   region.removeShield();
		   }
		   clanCache.get(region.clan()).thenAccept(o -> {
			   o.ifPresent(clan -> {
					new RegionHologram(region, clan, configs).update();
			   });
		   });
	   }
    }


}

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
import org.gepron1x.clans.api.repository.ClanRepository;
import org.gepron1x.clans.api.shield.ClanRegion;
import org.gepron1x.clans.api.shield.GlobalRegions;
import org.gepron1x.clans.plugin.config.Configs;
import org.gepron1x.clans.plugin.shield.region.wg.RegionHologram;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class ShieldRefreshTask extends BukkitRunnable {

	private final GlobalRegions regions;
	private final RegionContainer container;
	private final ClanRepository repository;
	private final Configs configs;

	private final AsyncLoadingCache<String, Optional<Clan>> clanCache;



	public ShieldRefreshTask(GlobalRegions regions, RegionContainer container, ClanRepository repository, Configs configs) {
		this.regions = regions;
		this.container = container;
		this.repository = repository;
		this.configs = configs;
		this.clanCache = Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(2)).buildAsync((s, e) -> repository.requestClan(s));
	}
    @Override
    public void run() {
       regions.listRegions().thenAccept(list -> {
		   for(ClanRegion region : list) {
			   var protectedRegion = new ProtectedRegionOf(container, region);
			   if(region.shield().expired() &&
					   protectedRegion.region().map(r -> r.getFlag(WgExtension.SHIELD_ACTIVE)).orElse(false)) {
				   region.removeShield();
			   }
			   new ProtectedRegionOf(container, region).clanTag().map(clanCache::get)
					   .orElseGet(() -> CompletableFuture.completedFuture(Optional.empty()))
					   .thenAccept(o -> o.ifPresent(clan -> {
						   new RegionHologram(region, clan, configs).update();
					   }));
		   }
	   }).join();
    }

}

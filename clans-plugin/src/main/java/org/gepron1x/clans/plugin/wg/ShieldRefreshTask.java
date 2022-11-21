/*
 * decalium-clans
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
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
import com.sk89q.worldguard.WorldGuard;
import org.bukkit.scheduler.BukkitRunnable;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.repository.ClanRepository;
import org.gepron1x.clans.api.shield.Shield;
import org.gepron1x.clans.api.shield.Shields;
import org.gepron1x.clans.plugin.config.settings.ClansConfig;
import org.slf4j.Logger;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class ShieldRefreshTask extends BukkitRunnable {

    private final ClanRepository repository;
    private final Shields shields;
    private final WorldGuard worldGuard;
    private final ClansConfig config;
    private final FactoryOfTheFuture futuresFactory;
    private final Logger logger;

    private final AsyncLoadingCache<String, Optional<Clan>> cache;

    public ShieldRefreshTask(ClanRepository repository, Shields shields, WorldGuard worldGuard, ClansConfig config, FactoryOfTheFuture futuresFactory, Logger logger) {

        this.repository = repository;
        this.shields = shields;
        this.worldGuard = worldGuard;
        this.config = config;
        this.futuresFactory = futuresFactory;
        this.logger = logger;
        this.cache = Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(60)).executor(futuresFactory::runAsync).buildAsync((s, executor) -> repository.requestClan(s));
    }
    @Override
    public void run() {
        shields.shields().thenCompose(map -> {
            System.out.println(map);
            List<CentralisedFuture<Optional<Clan>>> futures = map.keySet().stream().map(cache::get).map(c -> (CentralisedFuture<Optional<Clan>>) c).toList();
            return futuresFactory.allOf(futures).thenApply(ignored -> {
                return futures.stream().map(CompletableFuture::join).filter(Optional::isPresent)
                        .map(Optional::get).map(clan -> Map.entry(clan, map.get(clan.tag()))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            });
        }).thenAcceptSync(this::refreshShields).thenCompose(ignored -> shields.cleanExpired()).exceptionally(t -> {
            logger.error("Exception caught", t);
            return null;
        }).join();
    }


    private void refreshShields(Map<Clan, Shield> shields) {

        shields.forEach((key, value) -> {
            for(ClanHome home : key.homes()) {
                WgHome wgHome = new WgHome(worldGuard, key, home);
                wgHome.region().ifPresent(region -> {
                    boolean active = wgHome.shieldActive();
                   if(active && value.expired()) {
                       config.shields().shieldFlags().clear(region);
                       config.homes().worldGuardFlags().apply(region);
                       region.setFlag(WgExtension.SHIELD_ACTIVE, false);
                   } else if(!active) {
                       config.shields().shieldFlags().apply(region);
                       region.setFlag(WgExtension.SHIELD_ACTIVE, true);
                   }
                });
            }
        });
    }
}

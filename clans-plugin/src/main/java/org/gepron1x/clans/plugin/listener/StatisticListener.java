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
package org.gepron1x.clans.plugin.listener;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.statistic.StatisticType;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public final class StatisticListener implements Listener {

    private final CachingClanRepository repository;
    private final Plugin plugin;
    private final FactoryOfTheFuture futuresFactory;
    private final Table<String, StatisticType, Integer> statisticsTable = HashBasedTable.create();

    public StatisticListener(CachingClanRepository repository, Plugin plugin, FactoryOfTheFuture futuresFactory) {
        this.repository = repository;
        this.plugin = plugin;
        this.futuresFactory = futuresFactory;
    }


    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        incrementStatistic(event.getEntity().getUniqueId(), StatisticType.DEATHS);
        Player killer = event.getEntity().getKiller();
        if(killer == null) return;
        this.incrementStatistic(killer.getUniqueId(), StatisticType.KILLS);

    }



    private void incrementStatistic(UUID uuid, StatisticType type) {
        Optional<Clan> opt = repository.userClanIfCached(uuid);
        if(opt.isEmpty()) return;
        Clan clan = opt.get();

        Integer value = statisticsTable.get(clan.tag(), type);
        if(value == null) value = 0;
        value += 1;
        statisticsTable.put(clan.tag(), type, value);

    }


    public void start() {
        long ticks = 20 * 60;
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            Map<String, Map<StatisticType, Integer>> map = statisticsTable.rowMap();
            futuresFactory.allOf(map.entrySet().stream().map(entry -> {
                Map<StatisticType, Integer> copy = Map.copyOf(entry.getValue());
                return this.repository.requestClan(entry.getKey()).thenCompose(opt -> {
                    if(opt.isEmpty()) return futuresFactory.completedFuture(null);
                    Clan clan = opt.get();
                    return clan.edit(edition -> {
                        edition.addStatistics(copy);
                    });
                });

            }).collect(Collectors.toList())).thenAcceptSync(ignored -> {
                statisticsTable.clear();
            });
        }, ticks, ticks);
    }





}

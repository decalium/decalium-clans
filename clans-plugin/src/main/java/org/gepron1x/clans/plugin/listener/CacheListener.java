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

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.repository.ClanRepository;
import org.gepron1x.clans.plugin.cache.ClanCacheImpl;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CacheListener implements Listener {
    private final ClanCacheImpl cache;
    private final Server server;
    private final ClanRepository repository;

    public CacheListener(@NotNull ClanCacheImpl cache, @NotNull Server server, @NotNull ClanRepository repository) {

        this.cache = cache;
        this.server = server;
        this.repository = repository;
    }
    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        Clan clan = cache.getUserClan(uuid);
        if(clan != null) return;
        this.repository.requestUserClan(uuid).join().ifPresent(cache::cacheClan);

    }
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Clan clan = cache.getUserClan(player.getUniqueId());
        if(clan == null) return;
        if(areMembersOnline(clan)) return;
        cache.removeClan(clan.tag());

    }




    private boolean areMembersOnline(Clan clan) {
        for(UUID uuid : clan.memberMap().keySet()) {
            if(server.getPlayer(uuid) != null) return true;
        }
        return false;
    }

}

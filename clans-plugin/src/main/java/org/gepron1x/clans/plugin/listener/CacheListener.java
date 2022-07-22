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

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.gepron1x.clans.plugin.cache.UserCaching;
import org.jetbrains.annotations.NotNull;

public class CacheListener implements Listener {


    private final UserCaching userCaching;

    public CacheListener(@NotNull UserCaching userCaching) {

        this.userCaching = userCaching;
    }
    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        userCaching.cacheUser(event.getUniqueId());

    }
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        userCaching.remove(event.getPlayer().getUniqueId());
    }

}

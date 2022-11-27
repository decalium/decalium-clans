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
package org.gepron1x.clans.plugin.bootstrap;

import org.bukkit.plugin.Plugin;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.plugin.config.Configs;

public final class ChatCreation {

    private final Plugin plugin;
    private final Configs configs;
    private final CachingClanRepository repository;

    public ChatCreation(Plugin plugin, Configs configs, CachingClanRepository repository) {

        this.plugin = plugin;
        this.configs = configs;
        this.repository = repository;
    }

    public void create() {
        if(plugin.getServer().getPluginManager().isPluginEnabled("CarbonChat")) {
            // cnew CarbonChatHook(plugin.getServer(), configs.config(), configs.messages());
        }
    }
}

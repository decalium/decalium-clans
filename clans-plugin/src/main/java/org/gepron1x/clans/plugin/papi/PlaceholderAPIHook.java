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
package org.gepron1x.clans.plugin.papi;

import me.clip.placeholderapi.PlaceholderAPIPlugin;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Server;
import org.gepron1x.clans.plugin.cache.ClanCache;
import org.gepron1x.clans.plugin.config.settings.ClansConfig;

public record PlaceholderAPIHook(Server server, ClansConfig config,
								 ClanCache cache,
								 ClanTopCache topCache,
								 LegacyComponentSerializer legacy) {


	public void register() {
		new ClansExpansion(server, config, cache, legacy, topCache).register();
	}

	public static void unregister() {
		var expansionManager = PlaceholderAPIPlugin.getInstance().getLocalExpansionManager();
		expansionManager.findExpansionByIdentifier("clans").ifPresent(expansionManager::unregister);
	}

}

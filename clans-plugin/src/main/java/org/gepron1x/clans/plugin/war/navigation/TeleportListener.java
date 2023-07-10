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
package org.gepron1x.clans.plugin.war.navigation;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.gepron1x.clans.api.war.Wars;
import org.gepron1x.clans.plugin.config.Configs;
import org.gepron1x.clans.plugin.war.announce.WarAudience;

import java.util.Objects;

public final class TeleportListener implements Listener {

	private final Wars wars;
	private final Configs configs;

	public TeleportListener(Wars wars, Configs configs) {

		this.wars = wars;
		this.configs = configs;
	}

	@EventHandler
	public void on(PlayerTeleportEvent event) {
		World world = event.getTo().getWorld();
		if(Objects.equals(event.getFrom().getWorld(), world)) return;
		wars.currentWar(event.getPlayer()).ifPresent(war -> {
			configs.messages().war().navigationDifferentWorld()
					.with("target", event.getPlayer().displayName())
					.with("world", configs.config().wars().navigation().worldName(world))
					.send(new WarAudience(war));
		});
	}
}

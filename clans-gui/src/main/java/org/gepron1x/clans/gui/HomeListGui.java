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
package org.gepron1x.clans.gui;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.gepron1x.clans.api.chat.ClanHomeTagResolver;
import org.gepron1x.clans.api.chat.ClanTagResolver;
import org.gepron1x.clans.api.clan.Clan;

import java.util.stream.Stream;

import static org.gepron1x.clans.gui.DecaliumClansGui.message;

public final class HomeListGui implements GuiLike {

	private static final NamespacedKey HOME_NAME = new NamespacedKey(DecaliumClansGui.getPlugin(DecaliumClansGui.class), "home_name");

	private final Server server;
	private final Clan clan;

	public HomeListGui(Clan clan, Server server) {

		this.server = server;
		this.clan = clan;
	}

	@Override
	public Gui asGui() {
		ChestGui gui = new PaginatedGui<>(clan.homes(), clanHome -> {
			ItemStack item = clanHome.icon();
			item.editMeta(meta -> {
				ClanHomeTagResolver resolver = ClanHomeTagResolver.home(clanHome);
				meta.displayName(DecaliumClansGui.message("<home_display_name> (<gray><home_name></gray>)").with("home", resolver).asComponent());
				meta.getPersistentDataContainer().set(HOME_NAME, PersistentDataType.STRING, clanHome.name());
				meta.lore(Stream.of(
						message("<yellow>Уровень: <white><home_level>"),
						message("<yellow>Владелец: <white><home_owner_name>"),
						message("<green><bold>Нажмите, чтобы телепортироваться")
				).map(m -> m.with("home", resolver).asComponent()).toList());
			});
			return new GuiItem(item, event -> {
				if (event.isLeftClick()) event.getWhoClicked().teleportAsync(clanHome.location()).exceptionally(t -> {
					t.printStackTrace();
					return null;
				});
			});
		}).asGui();
		gui.setTitle(ComponentHolder.of(DecaliumClansGui.message("Базы клана <display_name>").with(ClanTagResolver.clan(clan)).asComponent()));
		return gui;
	}
}

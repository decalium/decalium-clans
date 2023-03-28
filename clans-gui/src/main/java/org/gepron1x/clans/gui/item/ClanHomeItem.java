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
package org.gepron1x.clans.gui.item;

import me.gepronix.decaliumcustomitems.BuildableItem;
import me.gepronix.decaliumcustomitems.event.ItemEventTrigger;
import me.gepronix.decaliumcustomitems.event.ItemTriggerContext;
import me.gepronix.decaliumcustomitems.item.Item;
import me.gepronix.decaliumcustomitems.item.SimpleItem;
import me.gepronix.decaliumcustomitems.item.modifier.ItemModifierImpl;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.gui.DecaliumClansGui;
import org.gepron1x.clans.gui.ItemBuilder;
import org.gepron1x.clans.plugin.DecaliumClansPlugin;

public final class ClanHomeItem implements BuildableItem {

	public static final NamespacedKey HOME_ITEM = new NamespacedKey(JavaPlugin.getPlugin(DecaliumClansPlugin.class), "home_item");

	private final DecaliumClansApi clans;

	public ClanHomeItem(DecaliumClansApi clans) {
		this.clans = clans;
	}

	@Override
	public Item build() {
		return SimpleItem.builder(Material.DIAMOND_BLOCK)
				.key(HOME_ITEM).modifier(ItemModifierImpl.builder().name(DecaliumClansGui.message("Клановый приват").asComponent()).build())
				.listener(ItemEventTrigger.BLOCK_PLACE, this::onPlace).build();
	}

	private void onPlace(BlockPlaceEvent event, ItemTriggerContext ctx) {
		Player player = event.getPlayer();
		clans.users().userFor(player).clan().ifPresent(clan -> {
			String name = name(player, clan);
			ClanHome home = clans.homeBuilder().name(name).displayName(Component.text(name))
					.location(event.getBlockPlaced().getLocation())
					.icon(ItemBuilder.skull(player.getPlayerProfile()).stack())
					.creator(player.getUniqueId()).build();
			clan.edit(e -> e.addHome(home));
		});
	}

	private String name(Player player, Clan clan) {
		String name = player.getName() + "-home";
		int i = 1;
		while(clan.home(name).isPresent()) {
			name = player.getName() + "-home-" + i;
			i++;
		}
		return name;
	}
}

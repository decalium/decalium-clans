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
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.region.ClanRegion;
import org.gepron1x.clans.api.region.RegionOverlapsException;
import org.gepron1x.clans.api.user.ClanUser;
import org.gepron1x.clans.gui.DecaliumClansGui;
import org.gepron1x.clans.gui.RegionGui;
import org.gepron1x.clans.plugin.DecaliumClansPlugin;
import org.gepron1x.clans.plugin.config.messages.MessagesConfig;

import java.util.Optional;

public final class ClanRegionItem implements BuildableItem.NoConfig, Listener {

	public static final NamespacedKey HOME_ITEM = new NamespacedKey(JavaPlugin.getPlugin(DecaliumClansPlugin.class), "clan_region");

	public static final NamespacedKey REGION_ID = new NamespacedKey(JavaPlugin.getPlugin(DecaliumClansPlugin.class), "region_id");

	private final Plugin plugin;
	private final DecaliumClansApi clans;
	private final MessagesConfig messages;

	public ClanRegionItem(Plugin plugin, DecaliumClansApi clans, MessagesConfig messages) {
		this.plugin = plugin;
		this.clans = clans;
		this.messages = messages;
	}

	@Override
	public Item build() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		Bukkit.getPluginManager().registerEvents(new BlockProtection(block -> clans.regions().regionId(block).isPresent()), plugin);
		return SimpleItem.builder(Material.LODESTONE)
				.key(HOME_ITEM).modifier(ItemModifierImpl.builder().name(DecaliumClansGui.message("Клановый приват").asComponent()).build())
				.listener(ItemEventTrigger.BLOCK_PLACE, this::onPlace).build();
	}

	private void onPlace(BlockPlaceEvent event, ItemTriggerContext ctx) {
		Player player = event.getPlayer();
		ClanUser user = clans.users().userFor(player);
		user.regions().ifPresentOrElse(regions -> {
			Block block = event.getBlockPlaced();
			try {
				ClanRegion region = regions.create(block.getLocation());
			} catch (RegionOverlapsException e) {
				messages.region().regionOverlaps().send(player);
				event.setCancelled(true);
			}

		}, () -> {
			event.setCancelled(true);
			messages.region().notInClan().send(player);
		});
	}


	@EventHandler
	public void on(PlayerInteractEvent event) {
		ClanUser user = clans.users().userFor(event.getPlayer());
		Optional.ofNullable(event.getClickedBlock()).flatMap(b -> clans.regions().regionId(b)).flatMap(id -> {
			return user.regions().flatMap(regions -> regions.region(id));
		}).ifPresent(region -> new RegionGui(clans, user, region, plugin).asGui().show(event.getPlayer()));
	}
}

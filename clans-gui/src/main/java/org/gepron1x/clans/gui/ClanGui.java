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
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.chat.ClanMemberTagResolver;
import org.gepron1x.clans.api.chat.ClanTagResolver;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.user.ClanUser;
import org.gepron1x.clans.api.util.player.UuidPlayerReference;

import java.util.*;
import java.util.function.Consumer;

import static org.gepron1x.clans.gui.DecaliumClansGui.message;

public final class ClanGui implements GuiLike {

    private final Server server;
	private final Clan clan;
	private final ClanUser viewer;
	private final DecaliumClansApi api;

	public ClanGui(Server server, Clan clan, ClanUser viewer, DecaliumClansApi api) {
        this.server = server;
		this.clan = clan;
		this.viewer = viewer;
		this.api = api;
	}

    @Override
    public Gui asGui() {
		int rows = 3;
		if(viewer.clan().map(c -> c.id() != clan.id()).orElse(false)) rows += 1;
        ChestGui gui = new ChestGui(rows, ComponentHolder.of(message("Клан <clan>").with("clan", clan.displayName()).asComponent()));
        StaticPane pane = new StaticPane(9, rows);
		pane.setOnClick(e -> e.setCancelled(true));
		ClanTagResolver resolver = ClanTagResolver.clan(clan);
		var clanInfo = ItemBuilder.create(Material.SKULL_BANNER_PATTERN).name("<gradient:#42C4FB:#63FFE8>Клан<reset> <display_name>")
				.lore("<white>Уровень: <#42C4FB><level>",
						" ",
						"<#63FFE8><dagger> <white>Убийств: <#42C4FB><statistic_kills>",
						"<#63FFE8><skull> <white>Смертей: <#42C4FB><statistic_deaths>",
						" ",
						"<#63FFE8><crossed_swords> <white>Побед в битвах: <#42C4FB><statistic_clan_war_wins>",
						"<#63FFE8><flag> <white>Поражений в битвах: <#42C4FB><statistic_clan_war_wins>")
				.with("dagger", "\uD83D\uDDE1").with("skull", "☠").with("crossed_swords", "⚔").with("flag", "⚐")
				.with(resolver)
				.edit(meta -> meta.addItemFlags(ItemFlag.values())).guiItem();

        var memberList = ItemBuilder.skull(new UuidPlayerReference(server, clan.owner().uniqueId()).profile())
				.name("<gradient:#92FF25:#DBFF4B>Участники клана")
				.amount(Math.min(64, clan.members().size()))
				.edit(meta -> {
					ArrayList<Component> lore = new ArrayList<>();
					lore.add(Component.empty());
					var members = new TreeSet<>(Comparator.comparing(ClanMember::role));
					members.addAll(clan.members());
					Iterator<? extends ClanMember> iterator = members.iterator();
					int size = 0;
					for(int i = 0; i < 5; i++) {
						if(!iterator.hasNext()) break;
						lore.add(message("<#7CD8D8><role> <#DBFDFF><name>").with(ClanMemberTagResolver.clanMember(iterator.next())).asComponent());
						size++;
					}
					if(size != clan.members().size()) {
						lore.add(message("<#DBFDFF>...И еще <size>").with("size", clan.members().size() - size).asComponent());
					}
					lore.add(message("<gray>⇄ Нажми для перемещения.").asComponent());
					meta.lore(lore);
				})
				.with(resolver).guiItem(event -> {
					event.setCancelled(true);
					event.getWhoClicked().closeInventory();
					new ClanMemberListGui(clan, server).asGui().show(event.getWhoClicked());
				});

		pane.addItem(clanInfo, 4, 1);
		pane.addItem(memberList, 2, 1);
		pane.addItem(clanWars(), 6, 1);

        gui.addPane(pane);
		gui.addPane(border(0, rows));
		gui.addPane(border(8, rows));
        return gui;
    }


	private GuiItem clanWars() {
		Material material = Material.IRON_SWORD;
		List<String> interaction;
		Consumer<InventoryClickEvent> consumer = e -> {};
		if(viewer.clan().isEmpty()) {
			interaction = List.of("<#fb2727> ⇄ Вы не можете вызывать на битвы", "<#fb2727>без клана!");
			material = Material.BARRIER;
		} else if(viewer.clan().map(c -> c.id() == clan.id()).orElse(false)) {
			interaction = List.of("<#7CD8D8> ⇄ Нажмите на этот предмет в меню другого клана", "<#7CD8D8>для вызова на битву!");
		} else if(viewer.member().map(member -> !member.hasPermission(ClanPermission.SEND_WAR_REQUEST)).orElse(false)) {
			interaction = List.of("<#fb2727> ⇄ Ваша роль не позволяет вызывать", "<#fb2727>кланы на битву.");
			material = Material.BARRIER;
		} else {
			interaction = List.of("<#42C4FB> ⇄ Нажмите, чтобы вызвать <display_name> на битву!");
			consumer = e -> {
				Bukkit.dispatchCommand(e.getWhoClicked(), "clan war request "+clan.tag()); // TODO: proper war request api
				e.getWhoClicked().closeInventory();
			};
		}

		List<String> lore = new ArrayList<>();
		lore.add(" ");
		lore.addAll(List.of(
				"<gray>├─<white> Устраивай незабываемые сражения",
				"<gray>├─<white> на любой территории. Ограничений нет - победу",
				"<gray>└─<white> определяет только мастерство и подготовка!"
				)
		);
		lore.add(" ");
		lore.addAll(interaction);

		return ItemBuilder.create(material).name("<gradient:#fb2727:#fd439c>Клановые битвы").lore(lore)
				.with(ClanTagResolver.clan(clan)).guiItem(consumer);
	}


	public static StaticPane border(int x, int size) {
		StaticPane pane = new StaticPane(x, 0, 1, size);
		pane.setOnClick(e -> e.setCancelled(true));
		ItemStack itemStack = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
		itemStack.editMeta(meta -> meta.displayName(Component.space()));
		ItemStack gray = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
		gray.editMeta(meta -> meta.displayName(Component.space()));

		pane.addItem(new GuiItem(gray), 0, 0);
		for (int i = 1; i > size - 1; i++) pane.addItem(new GuiItem(itemStack), 0, i);
		pane.addItem(new GuiItem(gray), 0, size - 1);
		return pane;
	}
}

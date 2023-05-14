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
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.chat.ClanMemberTagResolver;
import org.gepron1x.clans.api.chat.ClanTagResolver;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.util.player.UuidPlayerReference;
import org.gepron1x.clans.plugin.util.message.Message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

import static org.gepron1x.clans.gui.DecaliumClansGui.message;

public final class ClanGui implements GuiLike {

    private final Server server;
	private final Clan clan;
	private final DecaliumClansApi api;

	public ClanGui(Server server, Clan clan, DecaliumClansApi api) {
        this.server = server;
		this.clan = clan;
		this.api = api;
	}

    @Override
    public Gui asGui() {
        ChestGui gui = new ChestGui(4, ComponentHolder.of(message("Клан <clan>").with("clan", clan.displayName()).asComponent()));
        StaticPane pane = new StaticPane(9, 4);

        ItemStack clanInfo = new ItemStack(Material.RED_BANNER);
		ClanTagResolver resolver = ClanTagResolver.clan(clan);
        clanInfo.editMeta(meta -> {
            meta.displayName(message("Клан <clan_display_name> (<clan_tag>)").with("clan", resolver).asComponent());
            meta.lore(Stream.of(
                    message("<yellow>Уровень: <white><clan_level> "),
					message("<yellow>Убийств: <white><clan_statistic_kills>"),
					message("<yellow>Смертей: <white><clan_statistic_deaths>"),
					Message.EMPTY,
					message("<yellow>Побед в битвах: <white><clan_statistic_clan_war_wins>"),
					message("<yellow>Поражений в битвах: <white><clan_statistic_clan_war_loses>")
            ).map(m -> m.with("clan", resolver).asComponent()).toList());
        });

        ItemStack owner = new SkullOf(new UuidPlayerReference(server, clan.owner().uniqueId())).itemStack();
        owner.editMeta(SkullMeta.class, meta -> {
            meta.displayName(message("<yellow>Владелец: <owner_name>").with(resolver).asComponent());
        });

        ItemStack memberList = new ItemStack(Material.SKULL_BANNER_PATTERN);
		memberList.setAmount(Math.min(64, clan.members().size()));
        memberList.editMeta(meta -> {
            meta.displayName(message("<yellow>Участники: ").asComponent());
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(Component.empty());
            Iterator<? extends ClanMember> iterator = clan.members().iterator();
			int size = 0;
            for(int i = 0; i < 5; i++) {
                if(!iterator.hasNext()) break;
                lore.add(message("<member_role> <member_name>").with("member", ClanMemberTagResolver.clanMember(iterator.next())).asComponent());
				size++;
            }
			if(size != clan.members().size()) lore.add(message("<yellow>...И еще <size>").with("size", clan.members().size() - size).asComponent());
			meta.lore(lore);

        });

		ItemStack homeList = new ItemStack(Material.CAMPFIRE);
		homeList.setAmount(Math.min(64, clan.homes().size()));
		homeList.editMeta(meta -> {
			meta.displayName(message("<yellow>Клановые базы").asComponent());
		});

		StaticPane border = new StaticPane(0, 0, 1, 4);
		border.fillWith(new ItemStack(Material.CYAN_STAINED_GLASS_PANE));

        pane.addItem(new GuiItem(clanInfo, event -> event.setCancelled(true)), 4, 1);
        pane.addItem(new GuiItem(owner, event -> event.setCancelled(true)), 5, 1);
		pane.addItem(new GuiItem(homeList, event -> {
			event.getWhoClicked().closeInventory();
			new HomeListGui(clan, server).asGui().show(event.getWhoClicked());
		}), 6, 1);
        pane.addItem(new GuiItem(memberList, event -> {
            event.getWhoClicked().closeInventory();
            new ClanMemberListGui(clan, server).asGui().show(event.getWhoClicked());
        }), 2, 1);

        gui.addPane(pane);
		gui.addPane(border(0));
		gui.addPane(border(8));
        return gui;
    }


	public static StaticPane border(int x) {
		StaticPane pane = new StaticPane(x, 0, 1, 4);
		pane.setOnClick(e -> e.setCancelled(true));
		ItemStack itemStack = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
		itemStack.editMeta(meta -> meta.displayName(Component.space()));
		ItemStack gray = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
		gray.editMeta(meta -> meta.displayName(Component.space()));

		pane.addItem(new GuiItem(gray), 0, 0);
		pane.addItem(new GuiItem(itemStack), 0, 1);
		pane.addItem(new GuiItem(itemStack), 0, 2);
		pane.addItem(new GuiItem(gray), 0, 3);
		return pane;
	}
}

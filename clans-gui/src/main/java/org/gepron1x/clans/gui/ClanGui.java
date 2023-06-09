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
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.chat.ClanTagResolver;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.user.ClanUser;
import org.gepron1x.clans.api.util.player.UuidPlayerReference;
import org.gepron1x.clans.gui.builder.InteractionLoreApplicable;
import org.gepron1x.clans.gui.builder.ItemBuilder;

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

	private boolean ownsClan() {
		return viewer.clan().map(c -> c.id() == clan.id()).orElse(false);
	}

    @Override
    public Gui asGui() {
		int rows = 3;
		if(ownsClan()) rows += 1;
        ChestGui gui = new ChestGui(rows, ComponentHolder.of(message("Клан <clan>").with("clan", clan.displayName()).asComponent()));
        StaticPane pane = new StaticPane(9, rows);
		pane.setOnClick(e -> e.setCancelled(true));
		ClanTagResolver resolver = ClanTagResolver.clan(clan);

		pane.addItem(memberList(), 2, 1);
		pane.addItem(clanInfo(), 4, 1);
		pane.addItem(clanWars(), 6, 1);

		if(ownsClan()) {
			pane.addItem(clanUpgrade(), 2, 2);
			pane.addItem(regions(), 4, 2);
			pane.addItem(clanHomes(), 6, 2);
		}

        gui.addPane(pane);
		gui.addPane(border(0, rows));
		gui.addPane(border(8, rows));
		gui.setOnGlobalDrag(e -> e.setCancelled(true));
        return gui;
    }

	private GuiItem memberList() {
		return ItemBuilder.skull(new UuidPlayerReference(server, clan.owner().uniqueId()).profile())
				.name("<gradient:#92FF25:#DBFF4B>Участники клана")
				.amount(Math.min(64, clan.members().size()))
				.space()
				.description("Взгляни на свой состав или", "проследи за интересующим тебя кланом!")
				.space()
				.menuInteraction(TextColor.color(ownsClan() ? 0x92FF25 : 0xDBFDFF))
				.guiItem(event -> {
					new GoBackGui(new ClanMemberListGui(clan, viewer, api), Slot.fromXY(6, 5), this).asGui().show(event.getWhoClicked());
				});
	}


	private GuiItem clanWars() {
		Material material = Material.IRON_SWORD;
		String interaction;
		TextColor color = InteractionLoreApplicable.NEGATIVE;
		Consumer<InventoryClickEvent> consumer = e -> {};
		if(viewer.clan().isEmpty()) {
			interaction = "Вы не можете вызывать на битвы без клана!";
			material = Material.BARRIER;
		} else if(ownsClan()) {
			interaction = "Нажмите в меню другого клана для вызова на битву!";
			color = InteractionLoreApplicable.NEUTRAL;
		} else if(viewer.member().map(member -> !member.hasPermission(ClanPermission.SEND_WAR_REQUEST)).orElse(false)) {
			interaction = "Ваша роль не позволяет вызывать кланы на битву.";
			material = Material.BARRIER;
		} else {
			color = TextColor.color(0x42C4FB);
			interaction = "Нажмите, чтобы вызвать <reset><display_name></reset> на битву!";
			consumer = e -> {
				Bukkit.dispatchCommand(e.getWhoClicked(), "clan war request "+clan.tag()); // TODO: proper war request api
				e.getWhoClicked().closeInventory();
			};
		}

		return ItemBuilder.create(material).name("<gradient:#fb2727:#fd439c>Клановые битвы")
				.space()
				.description("Устраивай незабываемые сражения",
						"на любой территории. Ограничений нет - победу",
						"определяет только мастерство и подготовка!")
				.space()
				.interaction(color, interaction)
				.with(ClanTagResolver.clan(clan)).edit(meta -> meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)).guiItem(consumer);
	}

	private GuiItem clanInfo() {
		ClanTagResolver resolver = ClanTagResolver.clan(clan);

		var builder = ItemBuilder.create(Material.SKULL_BANNER_PATTERN)
				.name("<gradient:#42C4FB:#63FFE8>Информация о клане <reset><display_name>")
				.space()
				.description(
						"Уровень<gray>:<#42C4FB> <level> <#dbfdff><potion>",
						" ",
						"Убийства<gray>:<#42C4FB> <statistic_kills> <#dbfdff><dagger>",
						"Смерти<gray>:<#42C4FB> <statistic_deaths> <#dbfdff><skull>",
						" ",
						"Побед в войнах:<#42C4FB> <statistic_clan_war_wins> <#dbfdff><crossed_swords>",
						"Поражений в войнах<gray>:<#42C4FB> <statistic_clan_war_loses> <#dbfdff><flag>"
				)
				.with("dagger", "\uD83D\uDDE1").with("skull", "☠").with("crossed_swords", "⚔").with("flag", "⚐").with("potion", "\uD83E\uDDEA")
				.with(resolver)
				.edit(meta -> meta.addItemFlags(ItemFlag.values()));
		if(ownsClan()) {
			builder.space().interaction(InteractionLoreApplicable.POSITIVE, "Нажмите для кастомизации клана");
		}

		return builder.guiItem();
	}

	private GuiItem clanUpgrade() {
		return ItemBuilder.create(Material.TOTEM_OF_UNDYING).amount(Math.min(1, clan.level()))
				.name("<gradient:#FDA624:#FFD84A>Улучшение клана")
				.space()
				.description("Прокачивай уровень и", "открывай новые возможности", "для себя и своих соклановцев!")
				.space()
				.menuInteraction()
				.guiItem();
	}

	private GuiItem regions() {
		return ItemBuilder.create(Material.LODESTONE)
				.name("<gradient:#7CD8D8:#DBFDFF>Регионы клана")
				.space()
				.description("Управляй своими владениями", "с любой точки сервера").space()
				.menuInteraction()
				.guiItem();
	}

	private GuiItem clanHomes() {
		return ItemBuilder.create(Material.CHORUS_FRUIT)
				.name("<gradient:#c733fb:#fd439c>Клановые телепорты")
				.space()
				.description("Используйте общие точки", "телепорта со своим кланом!")
				.space().menuInteraction().guiItem();
	}


	public static StaticPane border(int x, int size) {
		StaticPane pane = new StaticPane(x, 0, 1, size);
		pane.setOnClick(e -> e.setCancelled(true));
		ItemStack itemStack = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
		itemStack.editMeta(meta -> meta.displayName(Component.space()));
		ItemStack gray = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
		gray.editMeta(meta -> meta.displayName(Component.space()));

		pane.addItem(new GuiItem(gray), 0, 0);
		for (int i = 1; i < size - 1; i++) pane.addItem(new GuiItem(itemStack), 0, i);
		pane.addItem(new GuiItem(gray), 0, size - 1);
		return pane;
	}
}

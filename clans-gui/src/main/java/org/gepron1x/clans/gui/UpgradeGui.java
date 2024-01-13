package org.gepron1x.clans.gui;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.economy.LevelsMeta;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.api.exception.DescribingException;
import org.gepron1x.clans.api.exception.ExceptionHandler;
import org.gepron1x.clans.api.user.ClanUser;
import org.gepron1x.clans.gui.builder.DescriptionLoreApplicable;
import org.gepron1x.clans.gui.builder.ItemBuilder;
import org.gepron1x.clans.gui.builder.LoreApplicable;

import java.util.ArrayList;
import java.util.List;

public final class UpgradeGui implements GuiLike {

	private final GuiLike parent;
	private final ClanUser viewer;
	private final DecaliumClansApi clans;

	public UpgradeGui(GuiLike parent, ClanUser viewer, DecaliumClansApi clans) {
		this.parent = parent;
		this.viewer = viewer;
		this.clans = clans;
	}


	@Override
	public Gui asGui() {
		ChestGui gui = new ChestGui(4, ComponentHolder.of(Component.text("Прокачка клана")));
		StaticPane levels = new StaticPane(2, 1, 5, 1);
		levels.setOnClick(e -> e.setCancelled(true));
		fillLevels(gui, levels);
		gui.addPane(levels);
		gui.addPane(ClanGui.border(0, 4));
		gui.addPane(ClanGui.border(8, 4));
		return new GoBackGui(gui, Slot.fromXY(6, 3), parent).asGui();
	}

	private void fillLevels(Gui gui, StaticPane pane) {
		LevelsMeta levels = clans.levels();
		int clanLevel = viewer.clan().orElseThrow().level();
		for (int i = 0; i < 5; i++) {
			int level = i + 1;

			Material material = Material.GRAY_DYE;
			if (level <= clanLevel) material = Material.LIME_DYE;
			else if (clanLevel + 1 == level) material = Material.LIGHT_BLUE_DYE;

			var builder = ItemBuilder.create(material).name("<#DBFDFF>Уровень <#63FFE8><level>").with("level", level);

			List<String> abilities = new ArrayList<>();
			if (levels.allowAt().wars() == level) abilities.add("Клановым войнам");
			if (levels.allowAt().regions() == level) abilities.add("Регионам");
			if (levels.allowAt().shields() == level) abilities.add("Щитам");
			if (levels.allowAt().regionEffects() == level) abilities.add("Клановым Эффектам");
			if (levels.allowAt().homes() == level) abilities.add("Клановым телепортам");
			if (levels.allowAt().colors() == level) abilities.add("Декорациям (Цвет)");
			if (levels.allowAt().gradients() == level) abilities.add("Декорациям (Градиенты)");
			if (levels.allowAt().symbols() == level) abilities.add("Декорациям (Титулы)"); // do better
			if (!abilities.isEmpty()) {
				builder.space().lore("<#7CD8D8>Откроет доступ к:")
						.lore(new DescriptionLoreApplicable(LoreApplicable.text(abilities).color(Colors.POSITIVE)));
			}

			List<String> limits = new ArrayList<>();
			LevelsMeta.PerLevel perLevel = levels.forLevel(level);
			limits.add("<#DBFDFF>Макс. Участников<gray>: <#63FFE8><members>");
			if (level >= levels.allowAt().regions()) limits.add("<#DBFDFF>Макс. Регионов<gray>: <#63FFE8><regions>");
			if (level >= levels.allowAt().homes())
				limits.add("<#DBFDFF>Макс. Точек телепортации<gray>: <#63FFE8><homes>");
			builder.space().description(limits).with("members", perLevel.slots())
					.with("regions", perLevel.regions())
					.with("homes", perLevel.homes());
			if (clanLevel + 1 == level) {
				double price = clans.prices().clanUpgrade(clanLevel + 1);
				builder.space().interaction(Colors.POSITIVE, "Нажмите, чтобы прокачать клан за <#FDA624><price>◎")
						.with("price", clans.prices().clanUpgrade(clanLevel + 1));
				builder.consumer(ConfirmAction.price(price, e -> {
					viewer.clan().orElseThrow().edit(ClanEdition::upgrade)
							.thenAccept(clan -> {
								pane.clear();
								fillLevels(gui, pane);
								gui.update();
							})
							.exceptionally(ExceptionHandler.catchException(DescribingException.class, ex -> handleDescribingException(ex, e)));
				}));
			}
			pane.addItem(builder.guiItem(), i, 0);
		}
	}

	private void handleDescribingException(DescribingException ex, InventoryClickEvent event) {
		new ErrorItem(event, ex).show();
	}
}

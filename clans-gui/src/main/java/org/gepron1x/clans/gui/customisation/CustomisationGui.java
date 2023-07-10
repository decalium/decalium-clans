package org.gepron1x.clans.gui.customisation;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.decoration.CombinedDecoration;
import org.gepron1x.clans.gui.ClanGui;
import org.gepron1x.clans.gui.GoBackGui;
import org.gepron1x.clans.gui.GuiLike;
import org.gepron1x.clans.gui.LevelRequiredBuilder;
import org.gepron1x.clans.gui.builder.ItemBuilder;
import org.gepron1x.clans.plugin.DecaliumClansPlugin;
import org.gepron1x.clans.plugin.config.settings.ClansConfig;
import org.gepron1x.clans.plugin.config.settings.Decorations;

import java.util.List;

public class CustomisationGui implements GuiLike {

	private final GuiLike parent;
	private final Clan clan;
	private final DecaliumClansApi clans;

	public CustomisationGui(GuiLike parent, Clan clan, DecaliumClansApi clans) {
		this.parent = parent;

		this.clan = clan;
		this.clans = clans;
	}

	@Override
	public Gui asGui() {

		ClansConfig config = JavaPlugin.getPlugin(DecaliumClansPlugin.class).config();

		ChestGui gui = new ChestGui(4, "Кастомизация клана");

		StaticPane decorations = new StaticPane(2, 1, 5, 2);
		decorations.setOnClick(e -> e.setCancelled(true));
		decorations.addItem(new LevelRequiredBuilder(clan, clans.levels().allowAt().colors(), ItemBuilder.create(Material.DRAGON_BREATH)
				.name("<#fd439c>Цвета")).ifAllowed(builder -> builder.menuInteraction().consumer(e -> {
			e.getWhoClicked().closeInventory();
			decorations(config.decorations().colors(), "Выбор цвета").show(e.getWhoClicked());
		})).guiItem(), 0, 0);
		decorations.addItem(new LevelRequiredBuilder(clan, clans.levels().allowAt().gradients(), ItemBuilder.create(Material.EXPERIENCE_BOTTLE)
						.name("<gradient:#42C4FB:#fd439c>Градиенты</gradient>")).ifAllowed(builder -> builder.menuInteraction().consumer(e -> {
					e.getWhoClicked().closeInventory();
					decorations(config.decorations().gradients(), "Выбор градиента").show(e.getWhoClicked());
				})).guiItem(), 2, 0
		);
		decorations.addItem(new LevelRequiredBuilder(clan, clans.levels().allowAt().symbols(), ItemBuilder.create(Material.NAME_TAG)
				.name("<#FFD84A>Титулы")).ifAllowed(builder -> builder.menuInteraction().consumer(e -> {
			e.getWhoClicked().closeInventory();
			decorations(config.decorations().symbols(), "Выбор титула").show(e.getWhoClicked());
		})).guiItem(), 4, 0);

		decorations.addItem(ItemBuilder.create(Material.BARRIER).name("<#fb2727>Сбросить декорации").consumer(e -> {
			clan.edit(edition -> edition.decoration(CombinedDecoration.EMPTY).rename(Component.text(clan.tag())));
		}).guiItem(), 2, 1);
		gui.addPane(decorations);
		gui.addPane(ClanGui.border(0, 4));
		gui.addPane(ClanGui.border(8, 4));
		return new GoBackGui(gui, Slot.fromXY(6, 3), parent).asGui();
	}


	private <T extends Decorations.BaseDecoration> Gui decorations(List<T> decorations, String title) {
		ChestGui gui = new DecorationSelectionGui<>(decorations, clan).asGui();
		gui.setTitle(title);
		return new GoBackGui(gui, Slot.fromXY(6, 3), this).asGui();
	}


}

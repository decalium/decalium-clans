package org.gepron1x.clans.gui.customisation;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.decoration.CombinedDecoration;
import org.gepron1x.clans.gui.Colors;
import org.gepron1x.clans.gui.GuiLike;
import org.gepron1x.clans.gui.PaginatedGui;
import org.gepron1x.clans.gui.builder.DescriptionLoreApplicable;
import org.gepron1x.clans.gui.builder.ItemBuilder;
import org.gepron1x.clans.gui.builder.LoreApplicable;
import org.gepron1x.clans.plugin.config.settings.Decorations;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DecorationSelectionGui<T extends Decorations.BaseDecoration> implements GuiLike {


	private final List<T> decorations;
	private final Clan clan;


	public DecorationSelectionGui(List<T> decorations, Clan clan) {
		this.decorations = decorations;
		this.clan = clan;
	}

	@Override
	public ChestGui asGui() {
		AtomicReference<ItemStack> selected = new AtomicReference<>(null);
		CombinedDecoration tagDecoration = clan.tagDecoration();

		var gui = new PaginatedGui<>(4, decorations, deco -> {
			boolean matches = deco.has(clan.tagDecoration());
			CombinedDecoration newDecoration = deco.apply(tagDecoration);
			var builder = ItemBuilder.create(deco.material()).edit(m -> {
				m.displayName(deco.name().asComponent().decoration(TextDecoration.ITALIC, false));
				m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				if (matches) {
					m.addEnchant(Enchantment.LUCK, 1, true);
				}
			});
			builder.lore(new DescriptionLoreApplicable(LoreApplicable.components(Component.text("Предварительный просмотр:"), newDecoration.apply(clan.tag()))));
			GuiItem item = builder.space().interaction(Colors.NEUTRAL, "Нажмите, чтобы применить").consumer(e -> {
				var decoration = deco.apply(clan.tagDecoration());
				clan.edit(edition -> edition.decoration(decoration).rename(decoration.apply(clan.tag())))
						.thenAcceptSync(result -> {
							e.getWhoClicked().playSound(Sound.sound(Key.key("minecraft:ui.cartography_table.take_result"), Sound.Source.MASTER, 1f, 1f));
							e.getCurrentItem().editMeta(m -> m.addEnchant(Enchantment.LUCK, 1, true));
							if (selected.get() != null) selected.get().editMeta(m -> m.removeEnchant(Enchantment.LUCK));
							selected.set(e.getCurrentItem());
							Gui.getGui(e.getInventory()).update();
						});

			}).guiItem();
			if (matches) selected.set(item.getItem());
			return item;
		}).asGui();
		gui.setOnGlobalClick(e -> e.setCancelled(true));
		gui.setOnGlobalDrag(e -> e.setCancelled(true));
		return gui;
	}
}

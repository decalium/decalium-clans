package org.gepron1x.clans.gui.region;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.type.HopperGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.api.region.ClanRegion;
import org.gepron1x.clans.api.region.effect.ActiveEffect;
import org.gepron1x.clans.api.user.ClanUser;
import org.gepron1x.clans.gui.Colors;
import org.gepron1x.clans.gui.ConfirmAction;
import org.gepron1x.clans.gui.GuiLike;
import org.gepron1x.clans.gui.Guis;
import org.gepron1x.clans.gui.builder.ItemBuilder;
import org.gepron1x.clans.gui.builder.LoreApplicable;
import org.gepron1x.clans.plugin.DecaliumClansPlugin;
import org.gepron1x.clans.plugin.config.format.TimeFormat;
import org.gepron1x.clans.plugin.config.settings.ClansConfig;
import org.gepron1x.clans.plugin.config.settings.EffectDescription;

import java.util.List;

public final class RegionEffectGui implements GuiLike {

	private final ClanUser viewer;
	private final ClanRegion region;

	private final TimeFormat timeFormat = DecaliumClansPlugin.getPlugin(DecaliumClansPlugin.class).config().timeFormat();

	public RegionEffectGui(ClanUser viewer, ClanRegion region) {

		this.viewer = viewer;
		this.region = region;
	}
	@Override
	public Gui asGui() {
		HopperGui gui = new HopperGui(ComponentHolder.of(Component.text("Клановые эффекты")));
		gui.setOnGlobalClick(e -> e.setCancelled(true));
		gui.setOnGlobalDrag(e -> e.setCancelled(true));
		ClansConfig config = JavaPlugin.getPlugin(DecaliumClansPlugin.class).config();
		List<EffectDescription> effects = config.region().effects();
		int size = Math.max(effects.size(), 1);
		int x = (5 - size) / 2;
		StaticPane pane = new StaticPane(x, 0, size, 1);
		updateEffects(pane, effects);
		gui.getSlotsComponent().addPane(pane);
		return gui;
	}


	private void updateEffects(StaticPane pane, List<EffectDescription> effects) {
		int i = 0;
		for (EffectDescription description : effects) {

			ItemBuilder builder = ItemBuilder.create(description.material());
			builder.edit(meta -> {
				meta.displayName(description.name().decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
			});
			builder.lore(LoreApplicable.components(description.lore()));
			builder.space();
			if(region.activeEffect().map(ActiveEffect::effect).map(description.effect()::equals).orElse(false)) {
				builder.edit(Guis::select);
				builder.description("<#92FF25>Эффект активен", "<#DBFDFF>До окончания: <#42C4FB><time_left>").with("time_left", timeFormat.format(region.activeEffect().get().left()));
			} else {
				builder.consumer(ConfirmAction.price(description.price(), event -> {
					region.applyEffect(description.effect(), description.duration());
					updateEffects(pane, effects);
					Guis.update(event.getClickedInventory());
				}));
				builder.interaction(Colors.POSITIVE, "Нажмите, чтобы приобрести за <price>")
						.with("price", description.price());
			}
			pane.addItem(builder.guiItem(), i, 0);
			i++;
		}
	}
}

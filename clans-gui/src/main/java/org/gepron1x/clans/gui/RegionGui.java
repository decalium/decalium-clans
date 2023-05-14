package org.gepron1x.clans.gui;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.shield.ClanRegion;
import org.gepron1x.clans.api.user.ClanUser;

import static org.gepron1x.clans.gui.DecaliumClansGui.message;

public class RegionGui implements GuiLike {


	private final DecaliumClansApi clans;
	private final ClanUser user;
	private ClanRegion region;

	public RegionGui(DecaliumClansApi clans, ClanUser user, ClanRegion region) {

		this.clans = clans;
		this.user = user;
		this.region = region;
	}
	@Override
	public Gui asGui() {
		Clan clan = user.clan().orElseThrow();
		ChestGui gui = new ChestGui(2, ComponentHolder.of(message("Регион клана <clan>").with("clan", clan.displayName()).asComponent()));
		StaticPane pane = new StaticPane(9, 1);

		pane.addItem(ItemBuilder.create(Material.EMERALD)
				.lore("<yellow>Уровень: <level>", "<green>Нажмите, чтобы прокачать за <price>")
				.with("level", region.level()).with("price", 100).guiItem(event	-> {
			region.upgrade().thenAcceptSync(region -> this.region = region);
		}), 1, 0);
		pane.addItem(ItemBuilder.create(Material.BARRIER).name("<red>Нажмите, чтобы удалить регион!").guiItem(e -> {
			user.regions().ifPresent(regions -> regions.remove(region).thenAcceptSync($ -> gui.getViewers().forEach(HumanEntity::closeInventory)));
		}), 2, 0);
		gui.addPane(pane);
		return gui;
	}
}

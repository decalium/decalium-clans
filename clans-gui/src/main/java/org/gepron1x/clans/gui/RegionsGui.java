package org.gepron1x.clans.gui;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import me.gepronix.decaliumcustomitems.DecaliumCustomItems;
import me.gepronix.decaliumcustomitems.item.StackOfItems;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.region.ClanRegion;
import org.gepron1x.clans.api.region.ClanRegions;
import org.gepron1x.clans.api.user.ClanUser;
import org.gepron1x.clans.gui.builder.ItemBuilder;
import org.gepron1x.clans.gui.item.ClanRegionItem;
import org.gepron1x.clans.plugin.DecaliumClansPlugin;
import org.gepron1x.clans.plugin.economy.VaultPlayer;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class RegionsGui implements GuiLike {


	private final GuiLike parent;
	private final ClanUser user;
	private final VaultPlayer player;
	private final DecaliumClansApi clans;

	public RegionsGui(GuiLike parent, ClanUser user, VaultPlayer player, DecaliumClansApi clans) {

		this.parent = parent;
		this.user = user;
		this.player = player;
		this.clans = clans;
	}

	@Override
	public Gui asGui() {
		ChestGui gui = new ChestGui(4, ComponentHolder.of(Component.text("Клановые регионы")));
		StaticPane pane = new StaticPane(3, 1, 3, 1);
		pane.setOnClick(e -> e.setCancelled(true));
		pane.addItem(ItemBuilder.create(Material.LODESTONE).name("<#92FF25>⛨ Купить блок региона")
				.description("Защитите постройки и базы вашего клана от нападений!").space()
				.interaction(Colors.POSITIVE, "Нажмите, чтобы приобрести за <#FDA624><price>◎").with("price", clans.prices().region())
				.consumer(e -> {
					if (!player.has(clans.prices().region())) {
						new ErrorItem(e, Component.text("Недостаточно средств!", Colors.NEGATIVE)).show();
						return;
					}
					player.withdraw(clans.prices().region());
					DecaliumCustomItems.get().getItemRegistry().of(ClanRegionItem.HOME_ITEM).ifPresent(i -> {
						e.getWhoClicked().getInventory().addItem(new StackOfItems(i).get());
					});
				}).guiItem(), 0, 0
		);
		pane.addItem(ItemBuilder.create(Material.CAMPFIRE).name("<#42C4FB>Список регионов")
				.description("Просмотри список клановых регионов")
				.menuInteraction().consumer(e -> {
					e.getWhoClicked().closeInventory();
					new GoBackGui(regionListGui(), Slot.fromXY(6, 5), this).asGui().show(e.getWhoClicked());
				}).guiItem(), 2, 0);

		gui.addPane(pane);
		gui.addPane(ClanGui.border(0, 4));
		gui.addPane(ClanGui.border(8, 4));
		return new GoBackGui(gui, Slot.fromXY(6, 3), parent).asGui();
	}


	private ChestGui regionListGui() {
		List<ClanRegion> regions = user.regions().map(ClanRegions::regions).orElse(List.of()).stream().sorted(Comparator.comparing(ClanRegion::id)).toList();
		Map<String, Component> worlds = JavaPlugin.getPlugin(DecaliumClansPlugin.class).config().wars().navigation().worldDisplayNames();
		ChestGui gui = new PaginatedGui<>(regions, region -> {
			Location location = region.location();
			Component worldName = worlds.get(location.getWorld().getName());
			if (worldName == null) worldName = Component.text(location.getWorld().getName());
			return ItemBuilder.create(Material.CAMPFIRE).name("<#FDA624>Клановый регион")
					.description("Щит<gray>: <shield_active:'<#92FF25>Активен':'<#fb2727>Не активен'>",
							"Мир<gray>: <#42C4FB><world>", "Координаты (X, Z)<gray>:<#42C4FB> <x> <z>").with("world", worldName)
					.with("x", location.getBlockX()).with("z", location.getBlockZ())
					.booleanState("shield_active", region.shield().active()).guiItem();
		}).asGui();
		gui.setOnGlobalDrag(e -> e.setCancelled(true));
		gui.setOnGlobalClick(e -> e.setCancelled(true));
		gui.setTitle(ComponentHolder.of(Component.text("Регионы клана")));
		return gui;
	}
}

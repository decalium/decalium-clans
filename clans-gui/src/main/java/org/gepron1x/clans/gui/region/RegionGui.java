package org.gepron1x.clans.gui.region;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.HopperGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.gepronix.decaliumcustomitems.DecaliumCustomItems;
import me.gepronix.decaliumcustomitems.item.StackOfItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.exception.NotEnoughMoneyException;
import org.gepron1x.clans.api.region.ClanRegion;
import org.gepron1x.clans.api.user.ClanUser;
import org.gepron1x.clans.gui.*;
import org.gepron1x.clans.gui.builder.InteractionLoreApplicable;
import org.gepron1x.clans.gui.builder.ItemBuilder;
import org.gepron1x.clans.gui.builder.LoreApplicable;
import org.gepron1x.clans.gui.item.ClanRegionItem;
import org.gepron1x.clans.plugin.DecaliumClansPlugin;
import org.gepron1x.clans.plugin.config.format.TimeFormat;
import org.gepron1x.clans.plugin.util.message.TextMessage;

import static org.gepron1x.clans.gui.DecaliumClansGui.message;

public class RegionGui implements GuiLike {


	private final DecaliumClansApi clans;
	private final ClanUser user;
	private final ClanRegion region;
	private final Plugin plugin;

	private transient BukkitTask refreshTask;

	private final TimeFormat timeFormat = DecaliumClansPlugin.getPlugin(DecaliumClansPlugin.class).config().timeFormat();

	public RegionGui(DecaliumClansApi clans, ClanUser user, ClanRegion region, Plugin plugin) {

		this.clans = clans;
		this.user = user;
		this.region = region;
		this.plugin = plugin;
	}

	@Override
	public Gui asGui() {
		Clan clan = user.clan().orElseThrow();
		HopperGui gui = new HopperGui(ComponentHolder.of(message("Регион клана <clan>").with("clan", clan.displayName()).asComponent()));
		StaticPane pane = new StaticPane(5, 1);
		pane.setOnClick(e -> e.setCancelled(true));
		pane.fillWith(new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE));

		pane.addItem(ItemBuilder.create(Material.BARRIER).name("<#fb2727>Нажмите, чтобы удалить регион!").guiItem(e -> {
			e.getWhoClicked().closeInventory();
			new ConfirmationGui(Component.text("Удалить регион"), event -> {
				event.getWhoClicked().closeInventory();
				user.regions().ifPresent(regions -> regions.remove(region));
				var block = region.location().getBlock();
				block.setType(Material.AIR);
				DecaliumCustomItems.get().getItemRegistry().of(ClanRegionItem.HOME_ITEM).ifPresent(i -> {
					block.getWorld().dropItem(block.getLocation(), new StackOfItems(i).get());
				});
			}, () -> {
				Guis.getGui(e.getInventory()).show(e.getWhoClicked());
			}).gui().show(e.getWhoClicked());
		}), 0, 0);

		pane.addItem(shield(gui), 2, 0);

		pane.addItem(effects(), 4, 0);
		gui.getSlotsComponent().addPane(pane);
		gui.setOnClose(e -> {
			if (refreshTask != null) refreshTask.cancel();
		});
		return gui;
	}

	private GuiItem shield(Gui gui) {
		ItemBuilder shield = ItemBuilder.create(Material.ITEM_FRAME)
				.edit(meta -> {
					editShieldMeta(meta);
					meta.addItemFlags(ItemFlag.values());
				});
		GuiItem item = shield.guiItem();
		item.setAction(e -> {
			if(!region.shield().expired()) return;
			if(!user.clan().map(clan -> clan.level() >= clans.levels().allowAt().shields()).orElse(false)) {
				new ErrorItem(e, TextMessage.message("<#fb2727>Прокачайте клан, чтобы открыть доступ к щиту!")).show();
				return;
			}
			ConfirmAction.price(clans.prices().shield(), event -> {
				try {
					region.addShield(clans.levels().forLevel(user.clan().orElseThrow()).shieldDuration());
					item.getItem().editMeta(this::editShieldMeta);
					startRefresh(item.getItem(), gui);
				} catch (NotEnoughMoneyException ex) {
					new ErrorItem(event, ex).show();
				}

			}).accept(e);
		});
		if (region.shield().active()) startRefresh(item.getItem(), gui);
		return item;
	}

	private GuiItem effects() {
		return ItemBuilder.create(Material.POTION).name("<#FDA624>Клановые эффекты")
				.consumer(event -> new RegionEffectGui(user, region).gui().show(event.getWhoClicked()))
				.guiItem();
	}

	private void editShieldMeta(ItemMeta meta) {
		meta.displayName(DecaliumClansGui.message("<#DBFDFF>\uD83D\uDEE1 Щит: <active:'<#92FF25>Активен':'<#fb2727>Не активен'>")
				.booleanState("active", region.shield().active()).asComponent());
		if (region.shield().active()) {
			updateTimer(meta);
			meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
		} else {
			meta.removeEnchant(Enchantment.PROTECTION_ENVIRONMENTAL);
			new InteractionLoreApplicable(LoreApplicable.text("Нажмите, чтобы приобрести щит за <price>!"), Colors.POSITIVE)
					.apply(meta, Formatter.number("price", clans.prices().shield()));
		}
	}

	private void updateTimer(ItemMeta meta) {
		String text = "<#DBFDFF>До окончания: <#42C4FB>" + timeFormat.format(region.shield().left()) + "<#dbfdff>⌚";
		LoreApplicable.text(text).apply(meta);
	}

	private void startRefresh(ItemStack item, Gui gui) {
		refreshTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
			if (region.shield().active()) {
				item.editMeta(this::updateTimer);
			} else {
				item.editMeta(this::editShieldMeta);
				refreshTask.cancel();
			}
			gui.update();
		}, 0, 20);
	}
}

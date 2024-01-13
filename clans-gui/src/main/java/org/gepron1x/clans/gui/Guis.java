package org.gepron1x.clans.gui;

import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class Guis {

	private Guis() {}

	public static Gui getGui(Inventory inventory) {
		if(inventory.getHolder() instanceof Gui gui) return gui;
		return Gui.getGui(inventory);
	}

	public static void update(Inventory inventory) {
		Gui gui = getGui(inventory);
		if(gui != null) gui.update();
	}

	public static void select(ItemStack itemStack) {
		itemStack.editMeta(Guis::select);
	}

	public static void select(ItemMeta meta) {
		meta.addEnchant(Enchantment.LUCK, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
	}

	public static void update(InventoryEvent event) {
		update(event.getInventory());
	}
}

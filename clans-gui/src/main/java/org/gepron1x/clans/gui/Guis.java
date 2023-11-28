package org.gepron1x.clans.gui;

import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import org.bukkit.inventory.Inventory;

public final class Guis {

	private Guis() {}

	public static Gui getGui(Inventory inventory) {
		if(inventory.getHolder() instanceof Gui gui) return gui;
		return Gui.getGui(inventory);
	}
}

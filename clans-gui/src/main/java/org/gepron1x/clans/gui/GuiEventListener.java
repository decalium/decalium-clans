package org.gepron1x.clans.gui;

import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;

public final class GuiEventListener implements Listener {


	private void onGuiClick(InventoryInteractEvent event) {
		Gui gui = Guis.getGui(event.getInventory());
		if(gui != null) event.setResult(Event.Result.DENY);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(InventoryClickEvent event) {
		onGuiClick(event);
	}


	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(InventoryDragEvent event) {
		onGuiClick(event);
	}

}

package org.gepron1x.clans.gui;

import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.gepron1x.clans.plugin.util.message.TextMessage;

import java.util.function.Consumer;

public final class ConfirmAction implements Consumer<InventoryClickEvent> {

	private final Consumer<InventoryClickEvent> action;
	private final ComponentLike title;

	public ConfirmAction(Consumer<InventoryClickEvent> action, ComponentLike title) {

		this.action = action;
		this.title = title;
	}
	@Override
	public void accept(InventoryClickEvent event) {
		Inventory inv = event.getInventory();
		Gui gui = Guis.getGui(inv);
		new ConfirmationGui(title, e -> {
			e.getWhoClicked().closeInventory();
			gui.show(e.getWhoClicked());
			gui.update();
			action.accept(event);
		}, () -> {
			event.getWhoClicked().closeInventory();
			gui.show(event.getWhoClicked());
		}).asGui().show(event.getWhoClicked());
	}


	public static ConfirmAction price(double price, Consumer<InventoryClickEvent> action) {
		return new ConfirmAction(action, TextMessage.message("Купить за <price>◎?").with("price", price));
	}
}

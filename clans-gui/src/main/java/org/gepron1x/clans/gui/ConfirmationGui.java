package org.gepron1x.clans.gui;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.type.HopperGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.gepron1x.clans.gui.builder.ItemBuilder;

import java.util.function.Consumer;

public final class ConfirmationGui implements GuiLike {

	private static final ItemBuilder CONFIRM = ItemBuilder.skullFromId("a92e31ffb59c90ab08fc9dc1fe26802035a3a47c42fee63423bcdb4262ecb9b6")
			.name("<#92FF25>Подтвердить");

	private static final ItemBuilder CANCEL = ItemBuilder.skullFromId("beb588b21a6f98ad1ff4e085c552dcb050efc9cab427f46048f18fc803475f7")
			.name("<#92FF25>Отменить");

	private final Component title;
	private final Consumer<InventoryClickEvent> onConfirm;
	private final Runnable onFail;

	public ConfirmationGui(Component title, Consumer<InventoryClickEvent> onConfirm, Runnable onFail) {

		this.title = title;
		this.onConfirm = onConfirm;
		this.onFail = onFail;
	}
	@Override
	public Gui asGui() {
		HopperGui gui = new HopperGui(ComponentHolder.of(title));
		gui.setOnClose(e -> onFail.run());
		StaticPane pane = new StaticPane(5, 1);
		pane.setOnClick(e -> e.setCancelled(true));
		pane.addItem(CONFIRM.guiItem(onConfirm), 1, 0);
		pane.addItem(CANCEL.guiItem(e -> {
			onFail.run();
			e.getWhoClicked().closeInventory();
		}), 3, 0);
		gui.getSlotsComponent().addPane(pane);
		return gui;
	}
}

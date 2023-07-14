package org.gepron1x.clans.gui;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import org.gepron1x.clans.gui.builder.ItemBuilder;

public final class GoBackGui implements GuiLike {

	private static final ItemBuilder GO_BACK = ItemBuilder.skull(Heads.GO_BACK).name("<#fb2727>Обратно в меню");
	private final ChestGui gui;
	private final Slot slot;
	private final GuiLike toGo;

	public GoBackGui(ChestGui gui, Slot slot, GuiLike toGo) {

		this.gui = gui;
		this.slot = slot;
		this.toGo = toGo;
	}

	@Override
	public Gui asGui() {
		StaticPane pane = new StaticPane(slot, 1, 1);
		pane.addItem(GO_BACK.guiItem(e -> {
			e.setCancelled(true);
			e.getWhoClicked().closeInventory();
			toGo.asGui().show(e.getWhoClicked());
		}), 0, 0);
		gui.addPane(pane);
		return gui;
	}
}

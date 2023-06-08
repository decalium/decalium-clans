package org.gepron1x.clans.gui;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import org.gepron1x.clans.gui.builder.ItemBuilder;

public final class GoBackGui implements GuiLike {

	private static final String TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzQxMzNmNmFjM2JlMmUyNDk5YTc4NGVmYWRjZmZmZWI5YWNlMDI1YzM2NDZhZGE2N2YzNDE0ZTVlZjMzOTQifX19";
	private static final ItemBuilder GO_BACK = ItemBuilder.skull(TEXTURE).name("<#fb2727>Обратно в меню");
	private final GuiLike guiLike;
	private final Slot slot;
	private final GuiLike toGo;

	public GoBackGui(GuiLike guiLike, Slot slot, GuiLike toGo) {

		this.guiLike = guiLike;
		this.slot = slot;
		this.toGo = toGo;
	}
	@Override
	public Gui asGui() {
		ChestGui gui = (ChestGui) guiLike.asGui();
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

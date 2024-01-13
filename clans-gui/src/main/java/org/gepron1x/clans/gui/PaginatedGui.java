/*
 * decalium-clans
 * Copyright © 2023 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.gepron1x.clans.gui.builder.ItemBuilder;

import java.util.Collection;
import java.util.function.Function;

public final class PaginatedGui<E> implements GuiLike {
	private final int rows;
	private final Collection<? extends E> elements;
	private final Function<? super E, GuiItem> mapper;

	public PaginatedGui(int rows, Collection<? extends E> elements, Function<? super E, GuiItem> mapper) {
		this.rows = rows;
		this.elements = elements;
		this.mapper = mapper;
	}

	public PaginatedGui(Collection<? extends E> elements, Function<? super E, GuiItem> mapper) {
		this(6, elements, mapper);
	}


	@Override
	public ChestGui asGui() {
		ChestGui gui = new ChestGui(rows, "Paginated Gui");
		gui.addPane(ClanGui.border(0, rows));
		gui.addPane(ClanGui.border(8, rows));
		PaginatedPane pages = new PaginatedPane(2, 1, 5, rows - 2);
		pages.populateWithGuiItems(elements.stream().map(mapper).toList());
		gui.addPane(pages);
		if (pages.getPages() < 2) return gui;

		StaticPane navigation = new StaticPane(2, rows - 2, 5, 1);
		navigation.setOnClick(event -> event.setCancelled(true));

		navigation.addItem(ItemBuilder.skull(Heads.NEXT).name("<#DBFDFF>Предыдущая").guiItem(event -> {
			if (pages.getPage() > 0) {
				pages.setPage(pages.getPage() - 1);
				gui.update();
			}
		}), 0, 0);

		navigation.addItem(ItemBuilder.skull(Heads.NEXT).name("<#DBFDFF>Следующая").guiItem(event -> {
			if (pages.getPage() < pages.getPages() - 1) {
				pages.setPage(pages.getPage() + 1);
				gui.update();
			}
		}), 4, 0);

		gui.addPane(navigation);

		return gui;
	}
}

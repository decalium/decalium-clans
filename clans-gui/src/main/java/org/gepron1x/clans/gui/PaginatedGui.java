/*
 * decalium-clans
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
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
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

public final class PaginatedGui<E> implements GuiLike {
    private final Collection<? extends E> elements;
    private final Function<? super E, ItemStack> mapper;

    public PaginatedGui(Collection<? extends E> elements, Function<? super E, ItemStack> mapper) {

        this.elements = elements;
        this.mapper = mapper;
    }
    @Override
    public ChestGui asGui() {
        ChestGui gui = new ChestGui(6, "Paginated Gui");
        PaginatedPane pages = new PaginatedPane(9, 5);
        ArrayList<ItemStack> items = new ArrayList<>(elements.size());
        for(E e : elements) {
            items.add(mapper.apply(e));
        }
        pages.populateWithItemStacks(items);
        pages.setOnClick(event -> event.setCancelled(true));
        gui.addPane(pages);
        StaticPane navigation = new StaticPane(0, 5, 9, 1);
        navigation.setOnClick(event -> event.setCancelled(true));
        navigation.addItem(new GuiItem(new ItemStack(Material.RED_WOOL), event -> {
            if (pages.getPage() > 0) {
                pages.setPage(pages.getPage() - 1);

                gui.update();
            }
        }), 0, 0);

        navigation.addItem(new GuiItem(new ItemStack(Material.GREEN_WOOL), event -> {
            if (pages.getPage() < pages.getPages() - 1) {
                pages.setPage(pages.getPage() + 1);

                gui.update();
            }
        }), 8, 0);

        gui.addPane(navigation);

        return gui;
    }
}

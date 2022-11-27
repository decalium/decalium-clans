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

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.util.player.UuidPlayerReference;

public final class ClanMemberListGui implements GuiLike {
    private final Clan clan;
    private final Server server;

    public ClanMemberListGui(Clan clan, Server server) {

        this.clan = clan;
        this.server = server;
    }
    @Override
    public Gui asGui() {
        ChestGui gui = new PaginatedGui<>(clan.members(), clanMember -> {
            ItemStack item = new SkullOf(new UuidPlayerReference(server, clanMember.uniqueId())).itemStack();
            item.editMeta(meta -> {
                meta.displayName(clanMember.renderName(server));
            });
            return item;
        }).asGui();
        gui.setTitle(ComponentHolder.of(Component.textOfChildren(clan.displayName(), Component.text(" members"))));
        return gui;
    }
}

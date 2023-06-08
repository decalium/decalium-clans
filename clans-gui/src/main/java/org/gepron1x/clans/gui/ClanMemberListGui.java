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

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import org.bukkit.Server;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.gepron1x.clans.api.chat.ClanMemberTagResolver;
import org.gepron1x.clans.api.chat.ClanTagResolver;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.user.ClanUser;
import org.gepron1x.clans.api.util.player.UuidPlayerReference;
import org.gepron1x.clans.gui.builder.InteractionLoreApplicable;
import org.gepron1x.clans.gui.builder.ItemBuilder;

import java.util.function.Consumer;

public final class ClanMemberListGui implements GuiLike {
    private final Clan clan;
	private final ClanUser viewer;
	private final Server server;

    public ClanMemberListGui(Clan clan, ClanUser viewer, Server server) {

        this.clan = clan;
		this.viewer = viewer;
		this.server = server;
    }
    @Override
    public Gui asGui() {
        ChestGui gui = new PaginatedGui<>(clan.members(), clanMember -> {
			var builder = ItemBuilder.skull(new UuidPlayerReference(server, clanMember.uniqueId()).profile()).name("<role> <name>")
					.with(ClanMemberTagResolver.clanMember(clanMember));
			if(!viewer.isIn(clan)) return builder.guiItem();
			ClanMember member = viewer.member().orElseThrow();
			if(member.equals(clanMember) || member.compareTo(clanMember) <= 0) return builder.guiItem();
			Consumer<InventoryClickEvent> event = e -> {};
            if(member.hasPermission(ClanPermission.SET_ROLE)) {
				builder.space().interaction(InteractionLoreApplicable.POSITIVE, "Нажмите чтобы повысить.");
				event = event.andThen(e -> {
					if(e.getClick() == ClickType.LEFT) return; // TODO
				});
			}
			if(member.hasPermission(ClanPermission.KICK)) {
				builder.space().interaction(InteractionLoreApplicable.NEGATIVE, "Нажмите Shift+ЛКМ чтобы кикнуть.");
				event = event.andThen(e -> {
					if(e.getClick() == ClickType.SHIFT_LEFT) return; // TODO
				});
			}
			return builder.guiItem(event);
        }).asGui();
        gui.setTitle(ComponentHolder.of(DecaliumClansGui.message("Участники клана <display_name>").with(ClanTagResolver.clan(clan)).asComponent()));
        return gui;
    }
}

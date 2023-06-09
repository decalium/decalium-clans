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
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.chat.ClanMemberTagResolver;
import org.gepron1x.clans.api.chat.ClanTagResolver;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.user.ClanUser;
import org.gepron1x.clans.api.util.player.UuidPlayerReference;
import org.gepron1x.clans.gui.builder.InteractionLoreApplicable;
import org.gepron1x.clans.gui.builder.ItemBuilder;

import java.util.Comparator;
import java.util.function.Consumer;

public final class ClanMemberListGui implements GuiLike {
    private final Clan clan;
	private final ClanUser viewer;
	private final DecaliumClansApi clansApi;

	public ClanMemberListGui(Clan clan, ClanUser viewer, DecaliumClansApi clansApi) {
        this.clan = clan;
		this.viewer = viewer;
		this.clansApi = clansApi;
	}
    @Override
    public Gui asGui() {

        ChestGui gui = new PaginatedGui<>(clan.members().stream().sorted(Comparator.reverseOrder()).toList(), clanMember -> {
			var resolver = ClanMemberTagResolver.clanMember(clanMember);
			var builder = ItemBuilder.skull(new UuidPlayerReference(Bukkit.getServer(), clanMember.uniqueId()).profile()).name("<role> <name>")
					.with(resolver);
			if(!viewer.isIn(clan)) return builder.guiItem();
			ClanMember member = viewer.member().orElseThrow();
			if(member.equals(clanMember) || member.compareTo(clanMember) <= 0) return builder.guiItem();
			Consumer<InventoryClickEvent> event = e -> {};
            if(member.hasPermission(ClanPermission.SET_ROLE)) {
				builder.space().interaction(InteractionLoreApplicable.POSITIVE, "Нажмите чтобы повысить.");
				event = event.andThen(e -> {
					if(e.getClick() == ClickType.LEFT) {
						e.getWhoClicked().closeInventory();
						new RoleSelectionGui(this, viewer, clanMember, clansApi).asGui().show(e.getWhoClicked());
					}
				});
			}
			if(member.hasPermission(ClanPermission.KICK)) {
				builder.space().interaction(InteractionLoreApplicable.NEGATIVE, "Нажмите Shift+ЛКМ чтобы кикнуть.");
				event = event.andThen(e -> {
					if(e.getClick() == ClickType.SHIFT_LEFT) {
						new ConfirmationGui(DecaliumClansGui.message("Исключить <name>?").with(resolver), confirmEvent -> {
							clan.edit(edition -> edition.removeMember(clanMember)).thenAcceptSync(newClan -> {
								confirmEvent.getWhoClicked().closeInventory();
								this.asGui().show(confirmEvent.getWhoClicked());
							});
						}, () -> e.getWhoClicked().openInventory(e.getInventory())).asGui().show(e.getWhoClicked());
					}
				});
			}
			return builder.guiItem(event);
        }).asGui();
        gui.setTitle(ComponentHolder.of(DecaliumClansGui.message("Участники клана <display_name>").with(ClanTagResolver.clan(clan)).asComponent()));
		gui.update();;
        return gui;
    }
}

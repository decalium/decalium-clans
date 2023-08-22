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
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.chat.ClanHomeTagResolver;
import org.gepron1x.clans.api.chat.ClanTagResolver;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.user.ClanUser;
import org.gepron1x.clans.gui.builder.ItemBuilder;



public final class HomeListGui implements GuiLike {


	private final Clan clan;
	private final ClanUser viewer;
	private final GuiLike parent;

	public HomeListGui(GuiLike parent, ClanUser viewer) {
		this.parent = parent;
		this.clan = viewer.clan().orElseThrow();
		this.viewer = viewer;
	}

	@Override
	public Gui asGui() {
		ChestGui gui = new PaginatedGui<>(clan.homes(), clanHome -> {
			ClanHomeTagResolver resolver = ClanHomeTagResolver.home(clanHome);
			boolean canDelete = viewer.member().map(ClanMember::uniqueId)
					.map(uuid -> clanHome.creator().equals(uuid)).orElse(false) || viewer.hasPermission(ClanPermission.EDIT_OTHERS_HOMES);
			ItemBuilder builder = ItemBuilder.create(clanHome.icon()).name("<home_display_name>")
					.description("Владелец: <home_owner_name>", "Координаты (X, Z): <x> <z>")
					.space()
					.interaction(Colors.NEUTRAL, "Нажмите, чтобы телепортироваться")
					.consumer(event -> {
						if(event.isShiftClick() && canDelete) {
								ConfirmationGui.confirmAndReturn(Component.text("Вы уверены?"), e -> clan.edit(edition -> edition.removeHome(clanHome)), event).asGui()
										.show(event.getWhoClicked());

						} else if(event.isLeftClick()) {
							event.getWhoClicked().teleportAsync(clanHome.location());
						}
					})
					.with("home", resolver).with("x", clanHome.location().getBlockX())
					.with("z", clanHome.location().getBlockZ());
			if(canDelete) builder.interaction(Colors.NEGATIVE, "Shift+ЛКМ, чтобы удалить!");
			return builder.guiItem();
		}).asGui();
		gui.setTitle(ComponentHolder.of(DecaliumClansGui.message("Телепорты клана <display_name>").with(ClanTagResolver.clan(clan)).asComponent()));
		return new GoBackGui(gui, Slot.fromXY(6, 5), parent).asGui();
	}
}

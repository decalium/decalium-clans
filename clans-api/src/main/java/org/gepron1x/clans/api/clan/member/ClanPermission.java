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
package org.gepron1x.clans.api.clan.member;

import net.kyori.adventure.util.Index;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record ClanPermission(@NotNull String value) {

	public static final ClanPermission INVITE = new ClanPermission("invite");
	public static final ClanPermission KICK = new ClanPermission("kick");
	public static final ClanPermission SET_ROLE = new ClanPermission("set_role");

	public static final ClanPermission ADD_HOME = new ClanPermission("add_home");
	public static final ClanPermission REMOVE_HOME = new ClanPermission("remove_home");
	public static final ClanPermission EDIT_OTHERS_HOMES = new ClanPermission("edit_others_homes");

	public static final ClanPermission SET_DISPLAY_NAME = new ClanPermission("set_display_name");
	public static final ClanPermission PROMOTE_OWNER = new ClanPermission("promote_owner");
	public static final ClanPermission DISBAND = new ClanPermission("disband");


	public static final ClanPermission SEND_WAR_REQUEST = new ClanPermission("send_war_request");
	public static final ClanPermission ACCEPT_WAR = new ClanPermission("accept_war");

	public static final ClanPermission DECORATE = new ClanPermission("decorate");

	public static final ClanPermission SET_CLAN_REGION = new ClanPermission("set_clan_region");
	public static final ClanPermission CLAN_REGION_MENU = new ClanPermission("clan_region_menu");

	private static final Index<String, ClanPermission> NAMES =
			Index.create(ClanPermission::value,
					INVITE, KICK, SET_ROLE,
					ADD_HOME, REMOVE_HOME, EDIT_OTHERS_HOMES,
					SET_DISPLAY_NAME, PROMOTE_OWNER, DISBAND, SEND_WAR_REQUEST, ACCEPT_WAR, DECORATE,
					SET_CLAN_REGION, CLAN_REGION_MENU);

	public static Index<String, ClanPermission> registry() {
		return NAMES;
	}

	public static Set<ClanPermission> all() {
		return NAMES.values();
	}

	public static @NotNull ClanPermission permission(String value) {
		ClanPermission perm = NAMES.value(value);
		return perm == null ? new ClanPermission(value) : perm;
	}

	@Override
	public String toString() {
		return value;
	}
}

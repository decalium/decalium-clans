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
package org.gepron1x.clans.api.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.gepron1x.clans.api.audience.RenderedPlayerName;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ClanHomeTagResolver(@NotNull ClanHome clanHome) implements TagResolver.WithoutArguments {

    private static final String NAME = "name";
    private static final String DISPLAY_NAME = "display_name";
    private static final String OWNER_UUID = "owner_uuid";
    private static final String OWNER_NAME = "owner_name";
    private static final String LOCATION_X = "location_x";
    private static final String LOCATION_Y = "location_y";
    private static final String LOCATION_Z = "location_z";
    private static final String LOCATION_WORLD = "location_world";

    private static final String ICON = "icon";

    public static ClanHomeTagResolver home(@NotNull ClanHome home) {
        return new ClanHomeTagResolver(home);
    }


    @Override
    public @Nullable Tag resolve(@NotNull String name) {
        Component component = switch(name) {
            case NAME -> Component.text(clanHome.name());
            case DISPLAY_NAME -> clanHome.displayName();
            case OWNER_UUID -> Component.text(clanHome.creator().toString());
            case OWNER_NAME -> new RenderedPlayerName(this.clanHome.creator(), Bukkit.getServer()).asComponent();
            case LOCATION_X -> Component.text(clanHome.location().getBlockX());
            case LOCATION_Y -> Component.text(clanHome.location().getBlockY());
            case LOCATION_Z -> Component.text(clanHome.location().getBlockZ());
            case LOCATION_WORLD -> Component.text(clanHome.location().getWorld().getName());
            case ICON -> clanHome.icon().displayName();
            default -> null;
        };
        return component == null ? null : Tag.selfClosingInserting(component);
    }
}

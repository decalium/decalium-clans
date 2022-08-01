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
package org.gepron1x.clans.api.audience;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class RenderedPlayerName implements ComponentLike {

    private final UUID uuid;
    private final Server server;

    public RenderedPlayerName(UUID uuid, Server server) {

        this.uuid = uuid;
        this.server = server;
    }
    @Override
    public @NotNull Component asComponent() {
        OfflinePlayer player = server.getOfflinePlayer(uuid);
        Player onlinePlayer = player.getPlayer();
        if(onlinePlayer != null) return onlinePlayer.displayName();

        String name = player.getName();
        if(name != null) return Component.text(name, NamedTextColor.GRAY);

        String uuidString = uuid.toString();
        Component uniqueId = Component.text().content("("+uuidString+")").color(NamedTextColor.GRAY)
                .clickEvent(ClickEvent.copyToClipboard(uuidString)).build();

        return Component.text().append(
                Component.text("Unknown player "),
                uniqueId
        ).build();

    }
}

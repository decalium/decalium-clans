/*
 * decalium-clans-rewrite
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans-rewrite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans-rewrite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans-rewrite. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.plugin.chat.paper;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.gepron1x.clans.plugin.chat.common.Channel;

import java.util.Set;

public final class ChatListener implements Listener {

    private final Set<? extends Channel> channels;

    public ChatListener(Set<? extends Channel> channels) {

        this.channels = channels;
    }

    @EventHandler
    public void on(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Component original = event.originalMessage();
        String originalString = PlainTextComponentSerializer.plainText().serialize(original);
        for(Channel channel : channels) {
            if(!channel.usePermitted(player)) continue;
            if(!originalString.startsWith(channel.prefix())) continue;
            event.viewers().clear();
            event.viewers().addAll(channel.recipients(player));
            event.renderer((sender, message, ogMessage, audience) -> channel.render(sender, audience, message, ogMessage));
            break;
        }
    }
}

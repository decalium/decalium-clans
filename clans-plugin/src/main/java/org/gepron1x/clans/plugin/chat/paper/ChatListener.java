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
package org.gepron1x.clans.plugin.chat.paper;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.gepron1x.clans.api.registry.Registry;
import org.gepron1x.clans.plugin.chat.common.Channel;

public final class ChatListener implements Listener {

    private final Registry<Key, ? extends Channel> channels;

    public ChatListener(Registry<Key, ? extends Channel> channels) {
        this.channels = channels;
    }

    @EventHandler
    public void on(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Component original = event.originalMessage();
        if(new PdcChatUser(player).currentChannelKey().flatMap(this.channels::value).map(channel -> {
            this.useChannel(channel, event);
            return true;
        }).orElse(false)) {
            return;
        }
        String originalString = PlainTextComponentSerializer.plainText().serialize(original);
        for(Channel channel : channels) {
            if(!originalString.startsWith(channel.prefix())) continue;
            useChannel(channel, event);
            break;
        }
    }

    private void useChannel(Channel channel, AsyncChatEvent event) {
        Player player = event.getPlayer();
        if(!channel.usePermitted(player)) return;
        event.viewers().clear();
        event.viewers().addAll(channel.recipients(player));
        event.renderer((sender, message, ogMessage, audience) -> channel.render(sender, audience, message, ogMessage));
    }
}

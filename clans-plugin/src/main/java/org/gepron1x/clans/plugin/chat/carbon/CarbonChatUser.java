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
package org.gepron1x.clans.plugin.chat.carbon;

import net.draycia.carbon.api.channels.ChannelRegistry;
import net.draycia.carbon.api.channels.ChatChannel;
import net.draycia.carbon.api.users.CarbonPlayer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.gepron1x.clans.plugin.chat.common.ChatUser;

import java.util.Optional;
import java.util.UUID;

public final class CarbonChatUser implements ChatUser {

    private final CarbonPlayer player;
    private final ChannelRegistry registry;

    public CarbonChatUser(CarbonPlayer player, ChannelRegistry registry) {

        this.player = player;
        this.registry = registry;
    }
    @Override
    public UUID uuid() {
        return player.uuid();
    }

    @Override
    public String name() {
        return player.username();
    }

    @Override
    public Component renderName() {
        return CarbonPlayer.renderName(player);
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }

    @Override
    public Optional<Key> currentChannel() {
        return Optional.ofNullable(player.selectedChannel()).map(ChatChannel::key);
    }

    @Override
    public void currentChannel(Key key) {
        player.selectedChannel(registry.get(key));
    }
}

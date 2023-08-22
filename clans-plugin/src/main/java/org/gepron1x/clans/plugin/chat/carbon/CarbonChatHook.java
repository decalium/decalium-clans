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
package org.gepron1x.clans.plugin.chat.carbon;

import net.draycia.carbon.api.CarbonChat;
import net.draycia.carbon.api.CarbonChatProvider;
import net.draycia.carbon.api.channels.ChannelRegistry;
import net.draycia.carbon.api.channels.ChatChannel;
import org.bukkit.Server;
import org.gepron1x.clans.plugin.cache.ClanCache;
import org.gepron1x.clans.plugin.config.Configs;
import org.jetbrains.annotations.NotNull;

public record CarbonChatHook(@NotNull Server server, @NotNull ClanCache cache, @NotNull Configs configs) {

	public void register() {
		CarbonChat carbon = CarbonChatProvider.carbonChat();
		ClanChatChannel chatChannel = new ClanChatChannel(server, cache, configs);
		ChannelRegistry registry = carbon.channelRegistry();
		ChatChannel channel = registry.channel(chatChannel.key());
		if(channel instanceof MutableChannel mutable) {
			mutable.setDelegate(chatChannel);
			return;
		}
		registry.register(new MutableChannel(chatChannel));
	}
}

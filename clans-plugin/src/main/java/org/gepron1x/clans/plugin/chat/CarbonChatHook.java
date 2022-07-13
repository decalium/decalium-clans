package org.gepron1x.clans.plugin.chat;

import net.draycia.carbon.api.CarbonChat;
import net.draycia.carbon.api.CarbonChatProvider;
import net.draycia.carbon.api.channels.ChannelRegistry;
import org.bukkit.Server;
import org.gepron1x.clans.plugin.cache.ClanCacheImpl;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.jetbrains.annotations.NotNull;

public record CarbonChatHook(@NotNull Server server, @NotNull ClanCacheImpl cache, @NotNull MessagesConfig messages, @NotNull ClansConfig clansConfig) {

    public void register() {
        CarbonChat carbon = CarbonChatProvider.carbonChat();
        ClanChatChannel chatChannel = new ClanChatChannel(server, cache, messages, clansConfig);
        ChannelRegistry registry = carbon.channelRegistry();
        boolean exists = registry.get(chatChannel.key()) != null;
        carbon.channelRegistry().register(chatChannel.key(), chatChannel);
        if(!exists) carbon.channelRegistry().registerChannelCommands(chatChannel);
    }
}

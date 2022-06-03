package org.gepron1x.clans.plugin.chat;

import net.draycia.carbon.api.CarbonChat;
import net.draycia.carbon.api.CarbonChatProvider;
import org.bukkit.Server;
import org.gepron1x.clans.plugin.cache.ClanCacheImpl;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.jetbrains.annotations.NotNull;

public record CarbonChatHook(@NotNull Server server, @NotNull ClanCacheImpl cache, @NotNull MessagesConfig messages, @NotNull ClansConfig clansConfig) {

    public void register() {
        CarbonChat carbon = CarbonChatProvider.carbonChat();
        ClanChatChannel chatChannel = new ClanChatChannel(server, cache, messages, clansConfig);
        carbon.channelRegistry().register(chatChannel.key(), chatChannel);
        carbon.channelRegistry().registerChannelCommands(chatChannel);
    }
}

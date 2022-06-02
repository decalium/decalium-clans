package org.gepron1x.clans.plugin.papi;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Server;
import org.gepron1x.clans.plugin.cache.ClanCacheImpl;
import org.gepron1x.clans.plugin.config.ClansConfig;

public record PlaceholderAPIHook(Server server, ClansConfig config,
                                 ClanCacheImpl cache,
                                 LegacyComponentSerializer legacy) {


    public void register() {
        new ClansExpansion(server, config, cache, legacy).register();
    }

}

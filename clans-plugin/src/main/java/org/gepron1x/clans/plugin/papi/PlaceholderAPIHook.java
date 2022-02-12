package org.gepron1x.clans.plugin.papi;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Server;
import org.gepron1x.clans.api.ClanCache;

public final class PlaceholderAPIHook {
    private final Server server;
    private final ClanCache cache;
    private final LegacyComponentSerializer legacy;

    public PlaceholderAPIHook(Server server, ClanCache cache, LegacyComponentSerializer legacy) {

        this.server = server;
        this.cache = cache;
        this.legacy = legacy;
    }


    public void register() {
        new ClansExpansion(server, cache, legacy).register();
    }

}

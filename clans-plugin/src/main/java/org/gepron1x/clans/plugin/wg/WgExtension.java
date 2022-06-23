package org.gepron1x.clans.plugin.wg;

import com.sk89q.worldguard.WorldGuard;
import org.bukkit.Server;
import org.gepron1x.clans.api.repository.ClanRepository;

public final class WgExtension {
    private final Server server;
    private final ClanRepository repository;

    public WgExtension(Server server, ClanRepository repository) {
        this.server = server;
        this.repository = repository;
    }


    public ClanRepository make() {
        if(server.getPluginManager().isPluginEnabled("WorldGuard")) {
            return new WgRepositoryImpl(this.repository, WorldGuard.getInstance(), this.server);
        }
        return this.repository;
    }
}

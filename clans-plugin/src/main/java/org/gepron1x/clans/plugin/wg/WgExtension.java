package org.gepron1x.clans.plugin.wg;

import com.sk89q.worldguard.WorldGuard;
import org.bukkit.Server;
import org.gepron1x.clans.api.repository.ClanRepository;
import org.gepron1x.clans.plugin.config.ClansConfig;

public final class WgExtension {
    private final Server server;
    private final ClansConfig clansConfig;
    private final ClanRepository repository;

    public WgExtension(Server server, ClansConfig clansConfig, ClanRepository repository) {
        this.server = server;
        this.clansConfig = clansConfig;
        this.repository = repository;
    }


    public ClanRepository make() {
        return new WgRepositoryImpl(this.repository, this.clansConfig, WorldGuard.getInstance(), this.server);
    }
}

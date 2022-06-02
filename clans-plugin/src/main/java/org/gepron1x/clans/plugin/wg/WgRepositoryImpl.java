package org.gepron1x.clans.plugin.wg;

import com.sk89q.worldguard.WorldGuard;
import org.bukkit.Server;
import org.gepron1x.clans.api.repository.ClanRepository;
import org.gepron1x.clans.plugin.AdaptingClanRepository;

public class WgRepositoryImpl extends AdaptingClanRepository {

    public WgRepositoryImpl(ClanRepository repository, WorldGuard worldGuard, Server server) {
        super(repository, clan -> new WgClan(clan, worldGuard, server));
    }


}

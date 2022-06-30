package org.gepron1x.clans.plugin.wg;

import com.sk89q.worldguard.WorldGuard;
import org.bukkit.Server;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.repository.ClanRepository;
import org.gepron1x.clans.plugin.AdaptingClanRepository;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.edition.PostClanEdition;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

public class WgRepositoryImpl extends AdaptingClanRepository {

    private final ClansConfig clansConfig;
    private final WorldGuard worldGuard;
    private final Server server;

    public WgRepositoryImpl(ClanRepository repository, ClansConfig clansConfig, WorldGuard worldGuard, Server server) {
        super(repository, clan -> new WgClan(clan, clansConfig, worldGuard, server));
        this.clansConfig = clansConfig;
        this.worldGuard = worldGuard;
        this.server = server;
    }

    @Override
    public @NotNull CentralisedFuture<Boolean> removeClan(@NotNull Clan clan) {
        return super.removeClan(clan).thenApplySync(bool -> {
            if(!bool) return false;
            PostClanEdition postClanEdition = new PostClanEdition(clan, this.clansConfig, this.worldGuard.getPlatform().getRegionContainer());
            clan.homes().forEach(postClanEdition::removeHome);
            return true;
        });
    }
}

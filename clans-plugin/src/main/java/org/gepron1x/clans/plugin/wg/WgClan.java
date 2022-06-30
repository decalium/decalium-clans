package org.gepron1x.clans.plugin.wg;

import com.sk89q.worldguard.WorldGuard;
import org.bukkit.Server;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.plugin.clan.DelegatingClan;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.edition.PostClanEdition;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Objects;
import java.util.function.Consumer;

public class WgClan implements DelegatingClan, Clan {
    private final Clan delegate;
    private final ClansConfig clansConfig;
    private final WorldGuard worldGuard;
    private final Server server;

    public WgClan(Clan delegate, ClansConfig clansConfig, WorldGuard worldGuard, Server server) {
        this.delegate = delegate;
        this.clansConfig = clansConfig;
        this.worldGuard = worldGuard;
        this.server = server;
    }


    @Override
    public DraftClan delegate() {
        return this.delegate;
    }


    @Override
    public @NotNull CentralisedFuture<Clan> edit(Consumer<ClanEdition> consumer) {
        return this.delegate.edit(consumer).thenApplySync(clan -> {
            consumer.accept(new PostClanEdition(clan, this.clansConfig, worldGuard.getPlatform().getRegionContainer()));
            return new WgClan(clan, this.clansConfig, this.worldGuard, this.server);
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WgClan wgClan = (WgClan) o;
        return delegate.equals(wgClan.delegate) && clansConfig.equals(wgClan.clansConfig) && worldGuard.equals(wgClan.worldGuard) && server.equals(wgClan.server);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate, clansConfig, worldGuard, server);
    }

    @Override
    public int id() {
        return this.delegate.id();
    }
}

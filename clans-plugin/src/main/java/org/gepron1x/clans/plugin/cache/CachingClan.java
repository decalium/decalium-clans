package org.gepron1x.clans.plugin.cache;

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.plugin.clan.DelegatingClan;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Objects;
import java.util.function.Consumer;

public final class CachingClan implements Clan, DelegatingClan {

    private final Clan delegate;
    private final ClanCacheImpl cache;

    public CachingClan(Clan delegate, ClanCacheImpl cache) {
        this.delegate = delegate;
        this.cache = cache;
    }

    @Override
    public @NotNull CentralisedFuture<Clan> edit(Consumer<ClanEdition> consumer) {
        return this.delegate.edit(consumer).thenApply(clan -> {
            if(!cache.isCached(clan.tag())) {
                cache.removeClan(clan.tag());
                cache.cacheClan(clan);
            }
            return new CachingClan(clan, cache);
        });
    }

    @Override
    public int id() {
        return delegate.id();
    }

    @Override
    public DraftClan delegate() {
        return this.delegate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CachingClan that = (CachingClan) o;
        return delegate.equals(that.delegate) && cache.equals(that.cache);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate, cache);
    }
}

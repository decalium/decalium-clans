package org.gepron1x.clans.plugin.cache;

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.repository.ClanCreationResult;
import org.gepron1x.clans.api.repository.ClanRepository;
import org.gepron1x.clans.plugin.AdaptingClanRepository;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Optional;
import java.util.UUID;

public final class CachingClanRepositoryImpl extends AdaptingClanRepository implements CachingClanRepository {


    private final FactoryOfTheFuture futuresFactory;
    private final ClanRepository repository;
    private final ClanCacheImpl cache;

    public CachingClanRepositoryImpl(ClanRepository repository, FactoryOfTheFuture futuresFactory, ClanCacheImpl cache) {
        super(repository, clan -> new CachingClan(clan, cache));
        this.futuresFactory = futuresFactory;
        this.repository = repository;
        this.cache = cache;
    }



    @Override
    public @NotNull CentralisedFuture<ClanCreationResult> createClan(@NotNull DraftClan draftClan) {
        if(cache.isCached(draftClan.tag())) return futuresFactory.completedFuture(ClanCreationResult.alreadyExists());
        return super.createClan(draftClan);
    }

    @Override
    public @NotNull CentralisedFuture<Boolean> removeClan(@NotNull Clan clan) {
        return this.repository.removeClan(clan).thenApply(bool -> {
            if(bool) cache.removeClan(clan.tag());
            return bool;
        });
    }

    @Override
    public @NotNull CentralisedFuture<Optional<Clan>> requestClan(@NotNull String tag) {
        Clan clan = cache.getClan(tag);
        if(clan != null) {
            return this.futuresFactory.completedFuture(Optional.of(new CachingClan(clan, cache)));
        }
        return super.requestClan(tag);
    }

    @Override
    public @NotNull CentralisedFuture<Optional<Clan>> requestUserClan(@NotNull UUID uuid) {
        Clan clan = cache.getUserClan(uuid);
        if(clan != null) return this.futuresFactory.completedFuture(Optional.of(new CachingClan(clan, cache)));
        return super.requestUserClan(uuid);
    }


    @Override
    public Optional<Clan> userClanIfCached(@NotNull UUID uuid) {
        return Optional.ofNullable(cache.getUserClan(uuid)).map(clan -> new CachingClan(clan, cache));
    }

    @Override
    public Optional<Clan> clanIfCached(@NotNull String tag) {
        return Optional.ofNullable(cache.getClan(tag)).map(clan -> new CachingClan(clan, cache));
    }


}

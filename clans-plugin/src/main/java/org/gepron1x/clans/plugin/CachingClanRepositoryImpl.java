package org.gepron1x.clans.plugin;

import org.gepron1x.clans.api.CachingClanRepository;
import org.gepron1x.clans.api.ClanCreationResult;
import org.gepron1x.clans.api.ClanRepository;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.plugin.clan.ClanImpl;
import org.gepron1x.clans.plugin.storage.ClanStorage;
import org.gepron1x.clans.plugin.storage.IdentifiedDraftClanImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class CachingClanRepositoryImpl implements CachingClanRepository {

    private final ClanRepository clanRepository;
    private final ClanStorage storage;
    private final FactoryOfTheFuture futuresFactory;
    private final ClanCacheImpl cache;

    public CachingClanRepositoryImpl(@NotNull ClanRepository clanRepository, @NotNull ClanStorage storage, @NotNull FactoryOfTheFuture futuresFactory, @NotNull ClanCacheImpl cache) {

        this.clanRepository = clanRepository;
        this.storage = storage;
        this.futuresFactory = futuresFactory;
        this.cache = cache;

    }
    @Override
    public @Nullable DraftClan getUserClanIfPresent(@NotNull UUID uuid) {
        return cache.getUserClan(uuid).clan();
    }

    @Override
    public @Nullable DraftClan getClanIfPresent(@NotNull String tag) {
        return cache.getClan(tag).clan();
    }


    @Override
    public @NotNull CentralisedFuture<ClanCreationResult> createClan(@NotNull DraftClan draftClan) {
        if(cache.isCached(draftClan.tag())) return futuresFactory.completedFuture(ClanCreationResult.alreadyExists());
        return this.clanRepository.createClan(draftClan);
    }

    @Override
    public @NotNull CentralisedFuture<Boolean> removeClan(@NotNull Clan clan) {
        return this.clanRepository.removeClan(clan).thenApply(success -> {
            if(success) this.cache.removeClan(clan.id());
            return success;
        });
    }

    @Override
    public CentralisedFuture<Optional<CachingClan>> requestClan(@NotNull String tag) {
        IdentifiedDraftClanImpl clan = cache.getClan(tag);
        if(clan == null) return this.clanRepository.requestClan(tag);
        return futuresFactory.completedFuture(
                Optional.of(
                        new CachingClan(
                                new ClanImpl(clan.id(), this.storage, this.futuresFactory),
                                this.futuresFactory, this.cache)
                )
        );
    }

    @Override
    public CentralisedFuture<Optional<CachingClan>> requestUserClan(@NotNull UUID uuid) {
        IdentifiedDraftClanImpl clan = cache.getUserClan(uuid);
        if(clan == null) return this.clanRepository.requestUserClan(uuid);
        return futuresFactory.completedFuture(Optional.of(
                new CachingClan(
                        new ClanImpl(clan.id(), this.storage, this.futuresFactory),
                        this.futuresFactory, this.cache
                )
        ));
    }

    @Override
    public @NotNull CentralisedFuture<Set<? extends Clan>> clans() {
        return this.clanRepository.clans();
    }
}

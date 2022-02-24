package org.gepron1x.clans.plugin;

import org.gepron1x.clans.api.CachingClanRepository;
import org.gepron1x.clans.api.ClanCreationResult;
import org.gepron1x.clans.api.ClanRepository;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.Clan2;
import org.gepron1x.clans.api.clan.DraftClan;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class CachingClanRepositoryImpl implements CachingClanRepository {

    private final ClanRepository clanRepository;
    private final FactoryOfTheFuture futuresFactory;
    private final ClanCacheImpl cache;

    public CachingClanRepositoryImpl(@NotNull ClanRepository clanRepository, @NotNull FactoryOfTheFuture futuresFactory, @NotNull ClanCacheImpl cache) {

        this.clanRepository = clanRepository;
        this.futuresFactory = futuresFactory;
        this.cache = cache;

    }
    @Override
    public @Nullable Clan getUserClanIfPresent(@NotNull UUID uuid) {
        return cache.getUserClan(uuid);
    }

    @Override
    public @Nullable Clan getClanIfPresent(@NotNull String tag) {
        return cache.getClan(tag);
    }


    @Override
    public @NotNull CentralisedFuture<ClanCreationResult> createClan(@NotNull DraftClan draftClan) {
        if(cache.isCached(draftClan.getTag())) return futuresFactory.completedFuture(ClanCreationResult.alreadyExists());
        return this.clanRepository.createClan(draftClan);
    }

    @Override
    public @NotNull CentralisedFuture<Boolean> removeClan(@NotNull Clan2 clan) {
        return this.clanRepository.removeClan(clan);
    }

    @Override
    public @NotNull CentralisedFuture<Optional<Clan2>> getClan(@NotNull String tag) {
        return null;
    }

    @Override
    public @NotNull CentralisedFuture<Optional<Clan2>> getUserClan(@NotNull UUID uuid) {
        return null;
    }

    @Override
    public @NotNull CentralisedFuture<Set<? extends Clan2>> getClans() {
        return null;
    }
}

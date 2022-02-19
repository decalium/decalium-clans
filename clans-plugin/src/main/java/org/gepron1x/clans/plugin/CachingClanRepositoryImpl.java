package org.gepron1x.clans.plugin;

import org.gepron1x.clans.api.CachingClanRepository;
import org.gepron1x.clans.api.ClanCache;
import org.gepron1x.clans.api.ClanCreationResult;
import org.gepron1x.clans.api.ClanRepository;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.editor.ClanEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public final class CachingClanRepositoryImpl implements CachingClanRepository {

    private final ClanRepository clanRepository;
    private final FactoryOfTheFuture futuresFactory;
    private final ClanCache cache;

    public CachingClanRepositoryImpl(@NotNull ClanRepository clanRepository, @NotNull FactoryOfTheFuture futuresFactory, @NotNull ClanCache cache) {

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
        return clanRepository.createClan(draftClan);
    }

    @Override
    public @NotNull CentralisedFuture<Boolean> removeClan(@NotNull Clan clan) {
        return clanRepository.removeClan(clan);
    }

    @Override
    public @NotNull CentralisedFuture<Clan> editClan(@NotNull Clan clan, @NotNull Consumer<ClanEditor> consumer) {
        return clanRepository.editClan(clan, consumer);
    }

    @Override
    public @NotNull CentralisedFuture<@Nullable Clan> getClan(@NotNull String tag) {
        Clan clan = cache.getClan(tag);
        if(clan != null) return futuresFactory.completedFuture(clan);
        return clanRepository.getClan(tag);
    }

    @Override
    public @NotNull CentralisedFuture<@Nullable Clan> getUserClan(@NotNull UUID uuid) {
        Clan clan = cache.getUserClan(uuid);
        if(clan != null) return futuresFactory.completedFuture(clan);
        return clanRepository.getUserClan(uuid);
    }

    @Override
    public @NotNull CentralisedFuture<Set<? extends Clan>> getClans() {
        return clanRepository.getClans();
    }
}

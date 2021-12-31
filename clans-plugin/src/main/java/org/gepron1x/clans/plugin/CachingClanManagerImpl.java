package org.gepron1x.clans.plugin;

import org.gepron1x.clans.api.CachingClanManager;
import org.gepron1x.clans.api.ClanCache;
import org.gepron1x.clans.api.ClanCreationResult;
import org.gepron1x.clans.api.ClanManager;
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

public class CachingClanManagerImpl implements CachingClanManager {

    private final ClanManager clanManager;
    private final FactoryOfTheFuture futuresFactory;
    private final ClanCache cache;

    public CachingClanManagerImpl(@NotNull ClanManager clanManager, @NotNull FactoryOfTheFuture futuresFactory, @NotNull ClanCache cache) {

        this.clanManager = clanManager;
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
        return clanManager.createClan(draftClan);
    }

    @Override
    public @NotNull CentralisedFuture<Boolean> removeClan(@NotNull Clan clan) {
        return clanManager.removeClan(clan);
    }

    @Override
    public @NotNull CentralisedFuture<Clan> editClan(@NotNull Clan clan, @NotNull Consumer<ClanEditor> consumer) {
        return clanManager.editClan(clan, consumer);
    }

    @Override
    public @NotNull CentralisedFuture<@Nullable Clan> getClan(@NotNull String tag) {
        Clan clan = cache.getClan(tag);
        if(clan != null) return futuresFactory.completedFuture(clan);
        return clanManager.getClan(tag);
    }

    @Override
    public @NotNull CentralisedFuture<@Nullable Clan> getUserClan(@NotNull UUID uuid) {
        Clan clan = cache.getUserClan(uuid);
        if(clan != null) return futuresFactory.completedFuture(clan);
        return clanManager.getUserClan(uuid);
    }

    @Override
    public @NotNull CentralisedFuture<Set<Clan>> getClans() {
        return clanManager.getClans();
    }
}

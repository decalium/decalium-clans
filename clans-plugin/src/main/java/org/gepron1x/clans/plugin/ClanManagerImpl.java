package org.gepron1x.clans.plugin;

import org.gepron1x.clans.api.ClanCreationResult;
import org.gepron1x.clans.api.ClanManager;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.editor.ClanEditor;
import org.gepron1x.clans.plugin.async.FuturesFactory;
import org.gepron1x.clans.plugin.clan.ClanBuilder;
import org.gepron1x.clans.plugin.editor.ClanEditorImpl;
import org.gepron1x.clans.plugin.storage.ClanStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ClanManagerImpl implements ClanManager {
    private final ClanStorage storage;
    private final FuturesFactory futuresFactory;
    private final ClanCacheImpl cache;

    public ClanManagerImpl(@NotNull ClanStorage storage, @NotNull ClanCacheImpl cache, FuturesFactory futuresFactory) {
        this.storage = storage;
        this.cache = cache;
        this.futuresFactory = futuresFactory;
    }
    @Override
    public @NotNull CompletableFuture<ClanCreationResult> createClan(@NotNull DraftClan draftClan) {
        if(cache.isCached(draftClan.getTag())) return CompletableFuture.completedFuture(ClanCreationResult.alreadyExists());
        return futuresFactory.supplyAsync(() -> storage.saveClan(draftClan))
                .thenApply(result -> {
                   if(result.isSuccess()) cache.cacheClan(result.clan());
                   return result;
                });
    }

    @Override
    public @NotNull CompletableFuture<Boolean> removeClan(@NotNull Clan clan) {

        return futuresFactory.runAsync(() -> storage.removeClan(clan))
                .thenApply(v -> {
            cache.removeClan(clan);
            return true;
        });
    }

    @Override
    public @NotNull CompletableFuture<Clan> editClan(@NotNull Clan clan, @NotNull Consumer<ClanEditor> consumer) {
        ClanBuilder builder = ClanBuilder.asBuilder(clan);
        ClanEditor editor = new ClanEditorImpl(clan, builder);
        consumer.accept(editor);
        Clan newClan = builder.build();

        return futuresFactory.runAsync(() -> storage.applyEditor(newClan, consumer))
                .thenApply(v -> {
                    if(cache.isCached(clan)) {
                        cache.removeClan(clan);
                        cache.cacheClan(newClan);
                    }
                    return newClan;
        });

    }

    @Override
    public @NotNull CompletableFuture<@Nullable Clan> getClan(@NotNull String tag) {
        Clan clan = cache.getClan(tag);
        if(clan != null) return futuresFactory.completedFuture(clan);
        return futuresFactory.supplyAsync(() -> storage.loadClan(tag));
    }

    @Override
    public @NotNull CompletableFuture<@Nullable Clan> getUserClan(@NotNull UUID uuid) {
        Clan clan = cache.getUserClan(uuid);
        if(clan != null) return futuresFactory.completedFuture(clan);
        return futuresFactory.supplyAsync(() -> storage.loadUserClan(uuid));
    }

    @Override
    public @NotNull CompletableFuture<Set<Clan>> getClans() {
        return futuresFactory.supplyAsync(storage::loadClans);
    }
}

package org.gepron1x.clans.plugin;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.gepron1x.clans.api.ClanManager;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.editor.ClanEditor;
import org.gepron1x.clans.plugin.editor.ClanEditorImpl;
import org.gepron1x.clans.plugin.storage.ClanStorage;
import org.gepron1x.clans.plugin.storage.StorageService;
import org.jdbi.v3.core.Jdbi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class ClanManagerImpl implements ClanManager {
    private final ClanCache cache;
    private final ClanStorage storage;
    private final Logger logger;

    public ClanManagerImpl(@NotNull ClanStorage storage, @NotNull ClanCache cache, @NotNull Logger logger) {

        this.storage = storage;
        this.cache = cache;
        this.logger = logger;
    }
    @Override
    public CompletableFuture<CreationResult> addClan(@NotNull Clan clan) {


        return CompletableFuture.supplyAsync(() -> {
            storage.saveClan(clan);
            return true;
        }).thenApply(bool -> {
            cache.cacheClan(clan);
            return CreationResult.SUCCESS;
        }).exceptionally(ex -> {
            logger.severe("Error happened while adding clan: ");
            ex.printStackTrace();
            return null;
        });

    }

    @Override
    public CompletableFuture<Boolean> removeClan(@NotNull Clan clan) {

        return CompletableFuture.runAsync(() -> storage.removeClan(clan)).thenApply(v -> {
            cache.removeClan(clan);
            return true;
        });
    }

    @Override
    public CompletableFuture<Clan> editClan(@NotNull Clan clan, @NotNull Consumer<ClanEditor> consumer) {
        Clan.Builder builder = clan.toBuilder();
        ClanEditor editor = new ClanEditorImpl(clan, builder);
        consumer.accept(editor);
        return CompletableFuture.runAsync(() -> {
            storage.editClan(clan, consumer);
        }).thenApply(v -> {
            Clan newClan = builder.build();
            cache.removeClan(clan);
            cache.cacheClan(newClan);
            return newClan;
        });
    }

    @Override
    public CompletableFuture<@Nullable Clan> getClan(@NotNull String tag) {
        Clan clan = cache.getClan(tag);
        if(clan != null) return CompletableFuture.completedFuture(clan);
        return CompletableFuture.supplyAsync(() -> storage.loadClan(tag)).thenApply(c -> {
            if(c != null) cache.cacheClan(c);
            return c;
        });
    }

    @Override
    public CompletableFuture<@Nullable Clan> getUserClan(@NotNull UUID uuid) {
        Clan clan = cache.getUserClan(uuid);
        if(clan != null) return CompletableFuture.completedFuture(clan);
        return CompletableFuture.supplyAsync(() -> storage.loadUserClan(uuid)).thenApply(c -> {
            if(c != null) cache.cacheClan(c);
            return c;
        });
    }
}

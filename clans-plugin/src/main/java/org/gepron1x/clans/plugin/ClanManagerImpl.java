package org.gepron1x.clans.plugin;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.gepron1x.clans.api.ClanManager;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.editor.ClanEditor;
import org.gepron1x.clans.plugin.async.FuturesFactory;
import org.gepron1x.clans.plugin.editor.ClanEditorImpl;
import org.gepron1x.clans.plugin.storage.ClanStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class ClanManagerImpl implements ClanManager {
    private final ClanStorage storage;
    private final FuturesFactory futuresFactory;
    private final ClanCache cache;
    private final Logger logger;

    public ClanManagerImpl(@NotNull ClanStorage storage, @NotNull ClanCache cache, FuturesFactory futuresFactory, @NotNull Logger logger) {
        this.storage = storage;
        this.cache = cache;
        this.futuresFactory = futuresFactory;
        this.logger = logger;
    }
    @Override
    public CompletableFuture<CreationResult> addClan(@NotNull Clan clan) {
        return futuresFactory.runAsync(() -> storage.saveClan(clan))
                .thenApply(v -> {
                    cache.cacheClan(clan);
                    return CreationResult.SUCCESS;
                });
    }

    @Override
    public CompletableFuture<Boolean> removeClan(@NotNull Clan clan) {

        return futuresFactory.runAsync(() -> storage.removeClan(clan)).thenApply(v -> {
            cache.removeClan(clan);
            return true;
        });
    }

    @Override
    public CompletableFuture<Clan> editClan(@NotNull Clan clan, @NotNull Consumer<ClanEditor> consumer) {
        Clan.Builder builder = clan.toBuilder();
        ClanEditor editor = new ClanEditorImpl(clan, builder);
        consumer.accept(editor);
        Clan newClan = builder.build();

        return futuresFactory.runAsync(() -> storage.editClan(clan, consumer))
                .thenApply(v -> {
                    if(cache.isCached(clan.getTag())) {
                        cache.removeClan(clan);
                        cache.cacheClan(newClan);
                    }
                    return newClan;
        });
    }

    @Override
    public CompletableFuture<@Nullable Clan> getClan(@NotNull String tag) {
        Clan clan = cache.getClan(tag);
        if(clan != null) return futuresFactory.completedFuture(clan);
        return futuresFactory.supplyAsync(() -> storage.loadClan(tag));
    }

    @Override
    public CompletableFuture<@Nullable Clan> getUserClan(@NotNull UUID uuid) {
        Clan clan = cache.getUserClan(uuid);
        if(clan != null) return futuresFactory.completedFuture(clan);
        return futuresFactory.supplyAsync(() -> storage.loadUserClan(uuid));
    }
}

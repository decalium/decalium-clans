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
    private final AsyncLoadingCache<String, Clan> clanCache;
    private final AsyncLoadingCache<UUID, Clan> userClanCache;
    private final ClanStorage storage;
    private final FuturesFactory futuresFactory;
    private final Logger logger;

    public ClanManagerImpl(@NotNull ClanStorage storage, FuturesFactory futuresFactory, @NotNull Logger logger) {

        this.storage = storage;
        this.futuresFactory = futuresFactory;
        this.clanCache = Caffeine.newBuilder().expireAfterAccess(Duration.ofMinutes(15)).buildAsync(storage::loadClan);
        this.userClanCache = Caffeine.newBuilder().buildAsync(storage::loadUserClan);

        this.logger = logger;
    }
    @Override
    public CompletableFuture<CreationResult> addClan(@NotNull Clan clan) {


        return futuresFactory.runAsync(() -> storage.saveClan(clan))
                .thenApplyAsync(v -> {
            clanCache.put(clan.getTag(), CompletableFuture.completedFuture(clan));
            LoadingCache<UUID, Clan> syncUserCache = userClanCache.synchronous();
            for(UUID uuid : clan.memberMap().keySet()) {
                syncUserCache.put(uuid, clan);
            }

            return CreationResult.SUCCESS;
        });

    }

    @Override
    public CompletableFuture<Boolean> removeClan(@NotNull Clan clan) {

        return futuresFactory.runAsync(() -> storage.removeClan(clan)).thenApplyAsync(v -> {
            clanCache.synchronous().invalidate(clan.getTag());
            userClanCache.synchronous().invalidateAll(clan.memberMap().keySet());
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
                .thenApplyAsync(v -> {
                    clanCache.put(newClan.getTag(), futuresFactory.completedFuture(newClan));

                    LoadingCache<UUID, Clan> syncCache = userClanCache.synchronous();

                    Set<UUID> uuids = clan.memberMap().keySet();

                    syncCache.invalidateAll(uuids);
                    for(UUID uuid : uuids) {
                        syncCache.put(uuid, newClan);
                    }

                    return newClan;
        });
    }

    @Override
    public CompletableFuture<@Nullable Clan> getClan(@NotNull String tag) {
        return clanCache.get(tag);
    }

    @Override
    public CompletableFuture<@Nullable Clan> getUserClan(@NotNull UUID uuid) {
        return userClanCache.get(uuid);
    }
}

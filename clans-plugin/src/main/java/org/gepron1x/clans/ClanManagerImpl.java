package org.gepron1x.clans;

import org.bukkit.plugin.PluginManager;
import org.gepron1x.clans.api.ClanCreationResult;
import org.gepron1x.clans.api.ClanManager;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.editor.ClanEditor;
import org.gepron1x.clans.api.event.ClanCreatedEvent;
import org.gepron1x.clans.api.event.ClanDeletedEvent;
import org.gepron1x.clans.api.event.ClanEditedEvent;
import org.gepron1x.clans.async.FuturesFactory;
import org.gepron1x.clans.clan.ClanBuilder;
import org.gepron1x.clans.editor.ClanEditorImpl;
import org.gepron1x.clans.storage.ClanStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ClanManagerImpl implements ClanManager {
    private final ClanStorage storage;
    private final FuturesFactory futuresFactory;
    private final PluginManager pluginManager;

    public ClanManagerImpl(@NotNull ClanStorage storage, FuturesFactory futuresFactory, PluginManager pluginManager) {
        this.storage = storage;
        this.futuresFactory = futuresFactory;
        this.pluginManager = pluginManager;
    }
    @Override
    public @NotNull CompletableFuture<ClanCreationResult> createClan(@NotNull DraftClan draftClan) {
        return futuresFactory.supplyAsync(() -> storage.saveClan(draftClan)).thenApply(result -> {
            result.ifSuccess(clan -> pluginManager.callEvent(new ClanCreatedEvent(clan)));
            return result;
        });
    }

    @Override
    public @NotNull CompletableFuture<Boolean> removeClan(@NotNull Clan clan) {
        return futuresFactory.supplyAsync(() -> storage.removeClan(clan))
                .thenApply(b -> {
                    if(b) pluginManager.callEvent(new ClanDeletedEvent(clan));
                    return b;
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
                    pluginManager.callEvent(new ClanEditedEvent(clan, newClan));
                    return newClan;
                });
    }

    @Override
    public @NotNull CompletableFuture<@Nullable Clan> getClan(@NotNull String tag) {
        return futuresFactory.supplyAsync(() -> storage.loadClan(tag));
    }

    @Override
    public @NotNull CompletableFuture<@Nullable Clan> getUserClan(@NotNull UUID uuid) {
        return futuresFactory.supplyAsync(() -> storage.loadUserClan(uuid));
    }

    @Override
    public @NotNull CompletableFuture<Set<Clan>> getClans() {
        return futuresFactory.supplyAsync(storage::loadClans);
    }
}

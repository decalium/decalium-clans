package org.gepron1x.clans.plugin;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.gepron1x.clans.api.ClanCreationResult;
import org.gepron1x.clans.api.ClanManager;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.editor.ClanEditor;
import org.gepron1x.clans.api.event.ClanCreatedEvent;
import org.gepron1x.clans.api.event.ClanDeletedEvent;
import org.gepron1x.clans.api.event.ClanEditedEvent;
import org.gepron1x.clans.plugin.clan.ClanBuilder;
import org.gepron1x.clans.plugin.editor.ClanEditorImpl;
import org.gepron1x.clans.plugin.storage.ClanStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public final class ClanManagerImpl implements ClanManager {
    private final ClanStorage storage;
    private final FactoryOfTheFuture futuresFactory;
    private final PluginManager pluginManager;

    public ClanManagerImpl(@NotNull ClanStorage storage, FactoryOfTheFuture futuresFactory, PluginManager pluginManager) {
        this.storage = storage;
        this.futuresFactory = futuresFactory;
        this.pluginManager = pluginManager;
    }
    @Override
    public @NotNull CentralisedFuture<ClanCreationResult> createClan(@NotNull DraftClan draftClan) {
        return futuresFactory.supplyAsync(() -> storage.saveClan(draftClan)).thenApplySync(result -> {
            result.ifSuccess(clan -> pluginManager.callEvent(new ClanCreatedEvent(clan)));
            return result;
        }).exceptionally(t -> {
            t.printStackTrace();
            return null;
        });
    }

    @Override
    public @NotNull CentralisedFuture<Boolean> removeClan(@NotNull Clan clan) {
        return futuresFactory.supplyAsync(() -> storage.removeClan(clan))
                .thenApplySync(b -> {
                    if(b) pluginManager.callEvent(new ClanDeletedEvent(clan));
                    return b;
                });
    }

    @Override
    public @NotNull CentralisedFuture<Clan> editClan(@NotNull Clan clan, @NotNull Consumer<ClanEditor> consumer) {
        ClanBuilder builder = ClanBuilder.asBuilder(clan);
        ClanEditor editor = new ClanEditorImpl(clan, builder);
        consumer.accept(editor);
        Clan newClan = builder.build();
        return futuresFactory.runAsync(() -> storage.applyEditor(newClan, consumer))
                .thenApplySync(v -> {
                    pluginManager.callEvent(new ClanEditedEvent(clan, newClan));
                    return newClan;
                });
    }

    @Override
    public @NotNull CentralisedFuture<@Nullable Clan> getClan(@NotNull String tag) {
        return futuresFactory.supplyAsync(() -> storage.loadClan(tag));
    }

    @Override
    public @NotNull CentralisedFuture<@Nullable Clan> getUserClan(@NotNull UUID uuid) {
        return futuresFactory.supplyAsync(() -> storage.loadUserClan(uuid));
    }

    @Override
    public @NotNull CentralisedFuture<Set<? extends Clan>> getClans() {
        return futuresFactory.supplyAsync(storage::loadClans);
    }

    private static <E extends Event & Cancellable> void test(E event) {
        event.setCancelled(true);

    }
}

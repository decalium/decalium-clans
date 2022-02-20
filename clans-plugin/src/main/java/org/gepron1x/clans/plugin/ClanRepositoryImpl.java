package org.gepron1x.clans.plugin;

import com.sk89q.worldguard.WorldGuard;
import org.bukkit.Server;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.gepron1x.clans.api.ClanCreationResult;
import org.gepron1x.clans.api.ClanRepository;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.editor.ClanEdition;
import org.gepron1x.clans.api.event.ClanCreatedEvent;
import org.gepron1x.clans.api.event.ClanDeletedEvent;
import org.gepron1x.clans.api.event.ClanEditedEvent;
import org.gepron1x.clans.plugin.clan.ClanBuilder;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.gepron1x.clans.plugin.editor.AnnouncingClanEdition;
import org.gepron1x.clans.plugin.editor.ClanEditionImpl;
import org.gepron1x.clans.plugin.editor.PostClanEdition;
import org.gepron1x.clans.plugin.storage.ClanStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public final class ClanRepositoryImpl implements ClanRepository {
    private final ClanStorage storage;
    private final FactoryOfTheFuture futuresFactory;
    private final PluginManager pluginManager;
    private final Server server;
    private final MessagesConfig messages;
    private final WorldGuard worldGuard;

    public ClanRepositoryImpl(@NotNull ClanStorage storage, @NotNull FactoryOfTheFuture futuresFactory, @NotNull Server server, @NotNull MessagesConfig messages, @NotNull WorldGuard worldGuard) {
        this.storage = storage;
        this.futuresFactory = futuresFactory;
        this.pluginManager = server.getPluginManager();
        this.server = server;
        this.messages = messages;
        this.worldGuard = worldGuard;
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
                    PostClanEdition postEditor = new PostClanEdition(clan, worldGuard.getPlatform().getRegionContainer());
                    clan.getHomes().forEach(postEditor::removeHome);
                    if(b) pluginManager.callEvent(new ClanDeletedEvent(clan));
                    return b;
                });
    }

    @Override
    public @NotNull CentralisedFuture<Clan> editClan(@NotNull Clan clan, @NotNull Consumer<ClanEdition> consumer) {
        ClanBuilder builder = ClanBuilder.asBuilder(clan);
        ClanEdition editor = new ClanEditionImpl(clan, builder);
        consumer.accept(editor);
        Clan newClan = builder.build();
        return futuresFactory.runAsync(() -> storage.applyEditor(newClan, consumer))
                .thenApplySync(v -> {
                    consumer.accept(new AnnouncingClanEdition(clan, server, new PostClanEdition(newClan, worldGuard.getPlatform().getRegionContainer()), messages));
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

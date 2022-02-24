package org.gepron1x.clans.plugin.clan;

import org.gepron1x.clans.api.clan.Clan2;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.plugin.storage.ClanStorage;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class Clan2Impl implements Clan2 {

    private final int id;
    private final ClanStorage storage;
    private final FactoryOfTheFuture futuresFactory;

    public Clan2Impl(int id, ClanStorage storage, FactoryOfTheFuture futuresFactory) {

        this.id = id;
        this.storage = storage;
        this.futuresFactory = futuresFactory;
    }
    @Override
    public int id() {
        return id;
    }

    @Override
    public @NotNull CompletableFuture<DraftClan> getDraft() {
        return futuresFactory.supplyAsync(() -> Objects.requireNonNull(this.storage.loadClan(this.id), "Clan deleted").clan());
    }

    @Override
    public @NotNull CompletableFuture<?> edit(Consumer<ClanEdition> consumer) {
        return futuresFactory.runAsync(() -> this.storage.applyEdition(this.id, consumer));
    }
}

package org.gepron1x.clans.plugin;

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.IdentifiedDraftClan;
import org.gepron1x.clans.api.repository.ClanCreationResult;
import org.gepron1x.clans.api.repository.ClanRepository;
import org.gepron1x.clans.plugin.clan.ClanImpl;
import org.gepron1x.clans.plugin.storage.ClanStorage;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class ClanRepositoryImpl implements ClanRepository {
    private final ClanStorage storage;
    private final FactoryOfTheFuture futuresFactory;

    public ClanRepositoryImpl(@NotNull ClanStorage storage, @NotNull FactoryOfTheFuture futuresFactory) {
        this.storage = storage;
        this.futuresFactory = futuresFactory;
    }

    @Override
    public @NotNull CentralisedFuture<ClanCreationResult> createClan(@NotNull DraftClan draftClan) {
        return futuresFactory.supplyAsync(() -> storage.saveClan(draftClan)).thenApplySync(saveResult -> {
            if(saveResult.status() != ClanCreationResult.Status.SUCCESS) {
                return ClanCreationResult.failure(saveResult.status());
            }
            int id = saveResult.id();
            return ClanCreationResult.success(new ClanImpl(id, draftClan, this.storage, this.futuresFactory));
        });
    }

    @Override
    public @NotNull CentralisedFuture<Boolean> removeClan(@NotNull Clan clan) {
        return futuresFactory.supplyAsync(() -> this.storage.removeClan(clan.id()));
    }

    @Override
    public @NotNull CentralisedFuture<Optional<Clan>> requestClan(@NotNull String tag) {
        return futuresFactory.supplyAsync(() -> Optional.ofNullable(this.storage.loadClan(tag)).map(this::adapt));
    }

    @Override
    public @NotNull CentralisedFuture<Optional<Clan>> requestUserClan(@NotNull UUID uuid) {
        return futuresFactory.supplyAsync(() -> Optional.ofNullable(this.storage.loadUserClan(uuid)).map(this::adapt));
    }

    @Override
    public @NotNull CentralisedFuture<Set<? extends Clan>> clans() {
        return futuresFactory.supplyAsync(() -> this.storage.loadClans().stream().map(this::adapt).collect(Collectors.toUnmodifiableSet()));
    }

    Clan adapt(IdentifiedDraftClan draftClan) {
        return new ClanImpl(draftClan.id(), draftClan, this.storage, this.futuresFactory);
    }
}

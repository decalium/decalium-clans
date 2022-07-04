package org.gepron1x.clans.api.reference;

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Optional;
import java.util.UUID;

public final class UuidClanReference implements ClanReference {
    private final CachingClanRepository repository;
    private final UUID uniqueId;


    public UuidClanReference(CachingClanRepository repository, UUID uniqueId) {
        this.repository = repository;
        this.uniqueId = uniqueId;
    }

    @Override
    public @NotNull CentralisedFuture<Optional<Clan>> clan() {

        return this.repository.requestUserClan(uniqueId);
    }

    @Override
    public Optional<Clan> cached() {
        return this.repository.userClanIfCached(uniqueId);
    }

}

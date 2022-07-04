package org.gepron1x.clans.api.reference;

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Optional;

public final class TagClanReference implements ClanReference {
    private final CachingClanRepository repository;
    private final String tag;

    public TagClanReference(CachingClanRepository repository, String tag) {

        this.repository = repository;
        this.tag = tag;
    }
    @Override
    public @NotNull CentralisedFuture<Optional<Clan>> clan() {
        return this.repository.requestClan(this.tag);
    }

    @Override
    public Optional<Clan> cached() {
        return this.repository.clanIfCached(tag);
    }

}

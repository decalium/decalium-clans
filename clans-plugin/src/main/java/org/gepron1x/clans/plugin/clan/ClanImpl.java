package org.gepron1x.clans.plugin.clan;

import com.google.common.base.MoreObjects;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.plugin.storage.ClanStorage;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class ClanImpl implements Clan, DelegatingClan {

    private final int id;
    private final DraftClan draftClan;
    private transient final ClanStorage storage;
    private transient final FactoryOfTheFuture futuresFactory;

    public ClanImpl(int id, DraftClan draftClan, ClanStorage storage, FactoryOfTheFuture futuresFactory) {

        this.id = id;
        this.draftClan = draftClan;
        this.storage = storage;
        this.futuresFactory = futuresFactory;
    }
    @Override
    public int id() {
        return id;
    }

    @Override
    public @NotNull CompletableFuture<Clan> edit(Consumer<ClanEdition> consumer) {
        DraftClan.Builder builder = draftClan.toBuilder();
        builder.applyEdition(consumer);
        return futuresFactory.runAsync(() -> this.storage.applyEdition(this.id, consumer))
                .thenApply(ignored -> new ClanImpl(this.id, builder.build(), this.storage, this.futuresFactory));
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClanImpl clan2 = (ClanImpl) o;
        return id == clan2.id && draftClan.equals(clan2.draftClan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, draftClan);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("draftClan", draftClan)
                .add("storage", storage)
                .add("futuresFactory", futuresFactory)
                .toString();
    }

    @Override
    public DraftClan delegate() {
        return this.draftClan;
    }
}

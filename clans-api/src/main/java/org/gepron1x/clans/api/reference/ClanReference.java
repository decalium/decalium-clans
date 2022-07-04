package org.gepron1x.clans.api.reference;

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Optional;
import java.util.function.Consumer;

public interface ClanReference {

    default CentralisedFuture<Optional<Clan>> edit(Consumer<ClanEdition> edition) {
        CentralisedFuture<Optional<Clan>> clanFuture = clan();
        return clanFuture.thenCompose(opt -> opt.map(clan -> clan.edit(edition).thenApply(Optional::of)).orElse(clanFuture));
    }

    @NotNull CentralisedFuture<Optional<Clan>> clan();

    Optional<Clan> cached();




}

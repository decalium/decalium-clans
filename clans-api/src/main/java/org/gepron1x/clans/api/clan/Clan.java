package org.gepron1x.clans.api.clan;

import org.gepron1x.clans.api.edition.ClanEdition;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.function.Consumer;

public interface Clan extends IdentifiedDraftClan {

    @NotNull CentralisedFuture<Clan> edit(Consumer<ClanEdition> consumer);
}

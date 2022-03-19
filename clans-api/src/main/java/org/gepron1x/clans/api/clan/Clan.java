package org.gepron1x.clans.api.clan;

import org.gepron1x.clans.api.edition.ClanEdition;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface Clan extends IdentifiedDraftClan {

    @NotNull CompletableFuture<Clan> edit(Consumer<ClanEdition> consumer);
}

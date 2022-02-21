package org.gepron1x.clans.api.clan;

import org.gepron1x.clans.api.edition.ClanEdition;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface Clan2 {

    int id();

    @NotNull CompletableFuture<DraftClan> getDraft();

    @NotNull CompletableFuture<DraftClan> edit(Consumer<ClanEdition> consumer);
}

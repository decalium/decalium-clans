package org.gepron1x.clans.api;

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.editor.ClanEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface ClanManager {
    @NotNull CompletableFuture<ClanCreationResult> createClan(@NotNull DraftClan draftClan);

    @NotNull CompletableFuture<Boolean> removeClan(@NotNull Clan clan);

    @NotNull CompletableFuture<Clan> editClan(@NotNull Clan clan, @NotNull Consumer<ClanEditor> consumer);

    @NotNull CompletableFuture<@Nullable Clan> getClan(@NotNull String tag);
    @NotNull CompletableFuture<@Nullable Clan> getUserClan(@NotNull UUID uuid);

    @NotNull CompletableFuture<Set<Clan>> getClans();

}

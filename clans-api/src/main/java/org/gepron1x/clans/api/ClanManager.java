package org.gepron1x.clans.api;

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.editor.ClanEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface ClanManager {
    CompletableFuture<CreationResult> addClan(@NotNull Clan clan);

    CompletableFuture<Boolean> removeClan(@NotNull Clan clan);

    CompletableFuture<Clan> editClan(@NotNull Clan clan, @NotNull Consumer<ClanEditor> consumer);

    CompletableFuture<@Nullable Clan> getClan(@NotNull String tag);
    CompletableFuture<@Nullable Clan> getUserClan(@NotNull UUID uuid);

    enum CreationResult {
        SUCCESS,
        ALREADY_EXISTS,
        MEMBERS_ALREADY_IN_CLAN
    }

}

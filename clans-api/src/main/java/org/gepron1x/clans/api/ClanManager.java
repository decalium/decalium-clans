package org.gepron1x.clans.api;

import org.bukkit.OfflinePlayer;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.editor.ClanEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public interface ClanManager {
    @NotNull CentralisedFuture<ClanCreationResult> createClan(@NotNull DraftClan draftClan);

    @NotNull CentralisedFuture<Boolean> removeClan(@NotNull Clan clan);

    @NotNull CentralisedFuture<Clan> editClan(@NotNull Clan clan, @NotNull Consumer<ClanEditor> consumer);

    @NotNull CentralisedFuture<@Nullable Clan> getClan(@NotNull String tag);
    @NotNull CentralisedFuture<@Nullable Clan> getUserClan(@NotNull UUID uuid);

    @NotNull
    default CentralisedFuture<@Nullable Clan> getUserClan(@NotNull OfflinePlayer player) {
        return getUserClan(player.getUniqueId());
    }

    @NotNull CentralisedFuture<Set<Clan>> getClans();

}

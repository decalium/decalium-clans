package org.gepron1x.clans.api;

import org.bukkit.OfflinePlayer;
import org.gepron1x.clans.api.clan.Clan2;
import org.gepron1x.clans.api.clan.DraftClan;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ClanRepository {
    @NotNull CentralisedFuture<ClanCreationResult> createClan(@NotNull DraftClan draftClan);

    @NotNull CentralisedFuture<Boolean> removeClan(@NotNull Clan2 clan);


    @NotNull CentralisedFuture<Optional<Clan2>> getClan(@NotNull String tag);
    @NotNull CentralisedFuture<Optional<Clan2>> getUserClan(@NotNull UUID uuid);

    @NotNull
    default CentralisedFuture<Optional<Clan2>> getUserClan(@NotNull OfflinePlayer player) {
        return getUserClan(player.getUniqueId());
    }

    @NotNull CentralisedFuture<Set<? extends Clan2>> getClans();

}

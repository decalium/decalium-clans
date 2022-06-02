package org.gepron1x.clans.api.repository;

import org.bukkit.OfflinePlayer;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ClanRepository {
    @NotNull CentralisedFuture<ClanCreationResult> createClan(@NotNull DraftClan draftClan);

    @NotNull CentralisedFuture<Boolean> removeClan(@NotNull Clan clan);


    @NotNull CentralisedFuture<Optional<Clan>> requestClan(@NotNull String tag);
    @NotNull CentralisedFuture<Optional<Clan>> requestUserClan(@NotNull UUID uuid);

    default CentralisedFuture<Optional<Clan>> requestUserClan(@NotNull OfflinePlayer player) {
        return requestUserClan(player.getUniqueId());
    }

    @NotNull CentralisedFuture<Set<? extends Clan>> clans();

}

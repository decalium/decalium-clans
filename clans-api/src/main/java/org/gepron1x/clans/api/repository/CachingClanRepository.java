package org.gepron1x.clans.api.repository;

import org.bukkit.OfflinePlayer;
import org.gepron1x.clans.api.clan.Clan;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public interface CachingClanRepository extends ClanRepository {


    Optional<Clan> userClanIfCached(@NotNull UUID uuid);

    default Optional<Clan> userClanIfCached(@NotNull OfflinePlayer player) {
        return userClanIfCached(player.getUniqueId());
    }

    Optional<Clan> clanIfCached(@NotNull String tag);




}

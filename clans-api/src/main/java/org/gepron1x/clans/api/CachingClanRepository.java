package org.gepron1x.clans.api;

import org.gepron1x.clans.api.clan.Clan;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public interface CachingClanRepository extends ClanRepository {


    Optional<Clan> userClanIfCached(@NotNull UUID uuid);

    Optional<Clan> clanIfCached(@NotNull String tag);




}

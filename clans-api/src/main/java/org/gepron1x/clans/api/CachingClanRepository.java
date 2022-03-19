package org.gepron1x.clans.api;

import org.gepron1x.clans.api.clan.DraftClan;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface CachingClanRepository extends ClanRepository {


    @Nullable DraftClan getUserClanIfPresent(@NotNull UUID uuid);

    @Nullable DraftClan getClanIfPresent(@NotNull String tag);




}

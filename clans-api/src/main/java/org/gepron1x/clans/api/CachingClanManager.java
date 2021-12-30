package org.gepron1x.clans.api;

import org.gepron1x.clans.api.clan.Clan;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface CachingClanManager extends ClanManager {


    @Nullable Clan getUserClanIfPresent(@NotNull UUID uuid);

    @Nullable Clan getClanIfPresent(@NotNull String tag);




}

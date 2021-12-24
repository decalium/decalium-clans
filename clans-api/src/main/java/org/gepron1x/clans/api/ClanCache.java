package org.gepron1x.clans.api;

import org.gepron1x.clans.api.clan.Clan;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.UUID;

public interface ClanCache {
    @Nullable
    Clan getClan(@NotNull String tag);

    @Nullable
    Clan getUserClan(@NotNull UUID uuid);

    @NotNull
    @UnmodifiableView
    Collection<Clan> getClans();

    boolean isCached(@NotNull Clan clan);

    boolean isCached(@NotNull String tag);
}

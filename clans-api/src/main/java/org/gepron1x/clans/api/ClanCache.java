package org.gepron1x.clans.api;

import org.gepron1x.clans.api.clan.DraftClan;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.UUID;

public interface ClanCache {
    @Nullable
    DraftClan getClan(@NotNull String tag);

    @Nullable
    DraftClan getUserClan(@NotNull UUID uuid);

    @NotNull
    @UnmodifiableView
    Collection<? extends DraftClan> getClans();

    boolean isCached(@NotNull DraftClan clan);

    boolean isCached(@NotNull String tag);

}

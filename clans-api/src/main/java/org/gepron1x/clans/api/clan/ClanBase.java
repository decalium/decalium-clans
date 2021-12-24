package org.gepron1x.clans.api.clan;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.Buildable;
import org.bukkit.OfflinePlayer;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.statistic.StatisticHolder;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface ClanBase extends StatisticHolder {
    @NotNull String getTag();
    @NotNull Component getDisplayName();

    @NotNull UUID getOwner();

    @NotNull @Unmodifiable Collection<ClanMember> getMembers();

    @NotNull @Unmodifiable Map<UUID, ClanMember> memberMap();

    @Nullable ClanMember getMember(@NotNull UUID uuid);

    @Nullable
    default ClanMember getMember(@NotNull OfflinePlayer player) {
        return getMember(player.getUniqueId());
    }

    @NotNull @Unmodifiable Collection<ClanHome> getHomes();

    @NotNull @Unmodifiable Map<String, ClanHome> homeMap();

    interface Builder<B extends ClanBase.Builder<B, C>, C extends ClanBase> extends Buildable.Builder<C> {


        @Contract("_ -> this")
        @NotNull B tag(@NotNull String tag);

        @Contract("_ -> this")
        @NotNull B owner(@NotNull UUID owner);

        @Contract("_ -> this")
        @NotNull B displayName(@NotNull Component displayName);

        @Contract("_ -> this")
        @NotNull B addMember(@NotNull ClanMember member);

        @Contract("_ -> this")
        @NotNull B removeMember(@NotNull ClanMember member);



        @Contract("_ -> this")
        @NotNull B addHome(@NotNull ClanHome home);

        @Contract("_ -> this")
        @NotNull B removeHome(@NotNull ClanHome home);

        @Contract("_ -> this")
        @NotNull B homes(@NotNull Collection<ClanHome> homes);

        @Contract("_ -> this")
        @NotNull B members(@NotNull Collection<ClanMember> members);

        @Contract("_, _ -> this")
        @NotNull B statistic(@NotNull StatisticType type, int value);

        @Contract("_ -> this")
        @NotNull B statistics(@NotNull Map<StatisticType, Integer> statistics);

        @NotNull B emptyStatistics();
        @NotNull B emptyMembers();
        @NotNull B emptyHomes();

        B self();


    }
}

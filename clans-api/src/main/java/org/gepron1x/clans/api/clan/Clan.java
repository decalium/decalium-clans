package org.gepron1x.clans.api.clan;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
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

public interface Clan extends Buildable<Clan, Clan.Builder>, StatisticHolder {

    @NotNull String getTag();
    @NotNull Component getDisplayName();

    @NotNull UUID getOwner();

    @NotNull @Unmodifiable Collection<ClanMember> getMembers();

    @Nullable ClanMember getMember(@NotNull UUID uuid);

    @Nullable
    default ClanMember getMember(@NotNull OfflinePlayer player) {
        return getMember(player.getUniqueId());
    }

    @NotNull @Unmodifiable Collection<ClanHome> getHomes();
    
    interface Builder extends Buildable.Builder<Clan> {

        @Contract("_ -> this")
        @NotNull Builder tag(@NotNull String tag);

        @Contract("_ -> this")
        @NotNull Builder owner(@NotNull UUID owner);

        @Contract("_ -> this")
        @NotNull Builder displayName(@NotNull Component displayName);

        @Contract("_ -> this")
        @NotNull Builder addMember(@NotNull ClanMember member);

        @Contract("_ -> this")
        @NotNull Builder removeMember(@NotNull ClanMember member);



        @Contract("_ -> this")
        @NotNull Builder addHome(@NotNull ClanHome home);

        @Contract("_ -> this")
        @NotNull Builder removeHome(@NotNull ClanHome home);

        @Contract("_ -> this")
        @NotNull Builder homes(@NotNull Collection<ClanHome> homes);

        @Contract("_ -> this")
        @NotNull Builder members(@NotNull Collection<ClanMember> members);

        @Contract("_, _ -> this")
        @NotNull Builder statistic(@NotNull StatisticType type, int value);

        @Contract("_ -> this")
        @NotNull Builder statistics(@NotNull Map<StatisticType, Integer> statistics);

        @NotNull Builder emptyStatistics();
        @NotNull Builder emptyMembers();
        @NotNull Builder emptyHomes();



        
    }


}

package org.gepron1x.clans.api.clan;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.pointer.Pointers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.util.Buildable;
import org.bukkit.Bukkit;
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
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public interface ClanBase extends StatisticHolder, ComponentLike, ForwardingAudience {
    @Override
    @NotNull
    default Component asComponent() { return getDisplayName(); }

    @NotNull String getTag();
    @NotNull Component getDisplayName();

    @NotNull ClanMember getOwner();

    @NotNull @Unmodifiable Collection<? extends ClanMember> getMembers();

    @NotNull @Unmodifiable Map<UUID, ? extends ClanMember> memberMap();

    @Nullable ClanMember getMember(@NotNull UUID uuid);

    @Nullable
    default ClanMember getMember(@NotNull OfflinePlayer player) {
        return getMember(player.getUniqueId());
    }

    @Override
    @NotNull default Iterable<? extends Audience> audiences() {
        return getMembers().stream()
                .map(m -> m.asPlayer(Bukkit.getServer()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    default @NotNull Pointers pointers() {
        return Pointers.builder().withDynamic(Identity.DISPLAY_NAME, this::getDisplayName).build();
    }

    @NotNull @Unmodifiable Collection<? extends ClanHome> getHomes();

    @Nullable ClanHome getHome(@NotNull String name);


    @NotNull @Unmodifiable Map<String, ? extends ClanHome> homeMap();

    interface Builder<B extends ClanBase.Builder<B, C>, C extends ClanBase> extends Buildable.Builder<C> {


        @Contract("_ -> this")
        @NotNull B tag(@NotNull String tag);

        @Contract("_ -> this")
        @NotNull B owner(ClanMember owner);

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
        @NotNull B homes(@NotNull Collection<? extends ClanHome> homes);

        @Contract("_ -> this")
        @NotNull B members(@NotNull Collection<? extends ClanMember> members);

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

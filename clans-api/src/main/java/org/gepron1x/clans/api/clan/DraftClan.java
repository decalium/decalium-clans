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
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.api.edition.EditionApplicable;
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

public interface DraftClan extends StatisticHolder, ComponentLike, Buildable<DraftClan, DraftClan.Builder>, ForwardingAudience {

    @Override
    @NotNull
    default Component asComponent() { return displayName(); }

    @NotNull String tag();
    @NotNull Component displayName();

    @NotNull ClanMember owner();

    @NotNull @Unmodifiable
    default Collection<? extends ClanMember> members() {
        return memberMap().values();
    }

    @NotNull @Unmodifiable Map<UUID, ? extends ClanMember> memberMap();

    @Nullable default ClanMember member(@NotNull UUID uuid) {
        return memberMap().get(uuid);
    }

    @Nullable
    default ClanMember member(@NotNull OfflinePlayer player) {
        return member(player.getUniqueId());
    }

    @Override
    @NotNull default Iterable<? extends Audience> audiences() {
        return members().stream()
                .map(m -> m.asPlayer(Bukkit.getServer()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    default @NotNull Pointers pointers() {
        return Pointers.builder().withDynamic(Identity.DISPLAY_NAME, this::displayName).build();
    }

    @NotNull @Unmodifiable default Collection<? extends ClanHome> homes() {
        return homeMap().values();
    }

    @Nullable default ClanHome home(@NotNull String name) {
        return homeMap().get(name);
    }


    @NotNull @Unmodifiable Map<String, ? extends ClanHome> homeMap();

    
    interface Builder extends Buildable.Builder<DraftClan>, EditionApplicable<DraftClan, ClanEdition> {
        
        @Contract("_ -> this")
        @NotNull Builder tag(@NotNull String tag);

        @Contract("_ -> this")
        @NotNull Builder owner(ClanMember owner);

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
        @NotNull Builder homes(@NotNull Collection<? extends ClanHome> homes);

        @Contract("_ -> this")
        @NotNull Builder members(@NotNull Collection<? extends ClanMember> members);

        @Contract("_, _ -> this")
        @NotNull Builder statistic(@NotNull StatisticType type, int value);

        @Contract("_ -> this")
        @NotNull Builder statistics(@NotNull Map<StatisticType, Integer> statistics);

        @NotNull Builder emptyStatistics();
        @NotNull Builder emptyMembers();
        @NotNull Builder emptyHomes();

        Builder self();
    }

}

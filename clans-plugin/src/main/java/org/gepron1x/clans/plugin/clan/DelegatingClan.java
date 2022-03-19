package org.gepron1x.clans.plugin.clan;

import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.OptionalInt;
import java.util.UUID;

public interface DelegatingClan extends DraftClan {


    DraftClan delegate();

    @Override
    @NotNull
    default String tag() {
        return delegate().tag();
    }

    @Override
    default @NotNull Component displayName() {
        return delegate().displayName();
    }

    @Override
    default @NotNull ClanMember owner() {
        return delegate().owner();
    }

    @Override
    default @NotNull @Unmodifiable Map<UUID, ? extends ClanMember> memberMap() {
        return delegate().memberMap();
    }

    @Override
    default @NotNull @Unmodifiable Map<String, ? extends ClanHome> homeMap() {
        return delegate().homeMap();
    }

    @Override
    @NotNull
    default DraftClan.Builder toBuilder() {
        return delegate().toBuilder();
    }

    @Override
    default OptionalInt statistic(@NotNull StatisticType type) {
        return delegate().statistic(type);
    }

    @Override
    @NotNull @Unmodifiable
    default Map<StatisticType, Integer> statistics() {
        return delegate().statistics();
    }

}

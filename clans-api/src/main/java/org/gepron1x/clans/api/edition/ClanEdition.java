package org.gepron1x.clans.api.edition;

import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public interface ClanEdition extends Edition<DraftClan> {

    @Override
    @NotNull
    default Class<DraftClan> getTarget() { return DraftClan.class; }

    ClanEdition setDisplayName(@NotNull Component displayName);

    ClanEdition setStatistic(@NotNull StatisticType type, int value);

    default ClanEdition setStatistics(@NotNull Map<StatisticType, Integer> statistics) {
        statistics.forEach(this::setStatistic);
        return this;
    }
    ClanEdition incrementStatistic(@NotNull StatisticType type);
    ClanEdition removeStatistic(@NotNull StatisticType type);

    ClanEdition addMember(@NotNull ClanMember member);

    default ClanEdition addMembers(@NotNull Collection<ClanMember> members) {
        members.forEach(this::addMember);
        return this;
    }
    ClanEdition removeMember(@NotNull ClanMember member);
    ClanEdition editMember(@NotNull UUID uuid, @NotNull Consumer<MemberEdition> consumer);

    ClanEdition addHome(@NotNull ClanHome home);

    default ClanEdition addHomes(@NotNull Collection<ClanHome> homes) {
        homes.forEach(this::addHome);
        return this;
    }
    ClanEdition removeHome(@NotNull ClanHome home);
    ClanEdition editHome(@NotNull String name, @NotNull Consumer<HomeEdition> consumer);





}

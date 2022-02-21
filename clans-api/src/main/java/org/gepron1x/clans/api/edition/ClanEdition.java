package org.gepron1x.clans.api.edition;

import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

public interface ClanEdition extends Edition<Clan> {

    @Override
    @NotNull
    default Class<Clan> getTarget() { return Clan.class; }

    ClanEdition setDisplayName(@NotNull Component displayName);

    ClanEdition setStatistic(@NotNull StatisticType type, int value);
    ClanEdition incrementStatistic(@NotNull StatisticType type);
    ClanEdition removeStatistic(@NotNull StatisticType type);

    ClanEdition addMember(@NotNull ClanMember member);
    ClanEdition removeMember(@NotNull ClanMember member);
    ClanEdition editMember(@NotNull UUID uuid, @NotNull Consumer<MemberEdition> consumer);

    ClanEdition addHome(@NotNull ClanHome home);
    ClanEdition removeHome(@NotNull ClanHome home);
    ClanEdition editHome(@NotNull String name, @NotNull Consumer<HomeEdition> consumer);





}

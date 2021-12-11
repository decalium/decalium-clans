package org.gepron1x.clans.api.editor;

import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface ClanEditor extends Editor<Clan> {

    ClanEditor setDisplayName(@NotNull Component displayName);

    ClanEditor setStatistic(@NotNull StatisticType type, int value);
    ClanEditor incrementStatistic(@NotNull StatisticType type);
    ClanEditor removeStatistic(@NotNull StatisticType type);

    ClanEditor addMember(@NotNull ClanMember member);
    ClanEditor removeMember(@NotNull ClanMember member);
    ClanEditor editMember(@NotNull ClanMember member, @NotNull Consumer<MemberEditor> consumer);

    ClanEditor addHome(@NotNull ClanHome home);
    ClanEditor removeHome(@NotNull ClanHome home);
    ClanEditor editHome(@NotNull ClanHome home, @NotNull Consumer<HomeEditor> consumer);





}

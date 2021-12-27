package org.gepron1x.clans.api;

import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.jetbrains.annotations.NotNull;

public interface ClanBuilderFactory {

    @NotNull DraftClan.Builder draftClanBuilder();

    @NotNull ClanMember.Builder memberBuilder();

    @NotNull ClanHome.Builder homeBuilder();

    @NotNull ClanRole.Builder roleBuilder();
}

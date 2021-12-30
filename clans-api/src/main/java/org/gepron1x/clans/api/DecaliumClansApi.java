package org.gepron1x.clans.api;

import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.jetbrains.annotations.NotNull;

public interface DecaliumClansApi extends ClanBuilderFactory {

    @NotNull ClanManager getClanManager();

    @NotNull ClanCache getClanCache();

    @NotNull ClanBuilderFactory getBuilderFactory();

    @NotNull RoleRegistry getRoleRegistry();

    @Override
    @NotNull
    default DraftClan.Builder draftClanBuilder() {
        return getBuilderFactory().draftClanBuilder();
    }

    @Override
    @NotNull
    default ClanMember.Builder memberBuilder() {
        return getBuilderFactory().memberBuilder();
    }

    @Override
    @NotNull
    default ClanHome.Builder homeBuilder() {
        return getBuilderFactory().homeBuilder();
    }

    @Override
    @NotNull
    default ClanRole.Builder roleBuilder() {
        return getBuilderFactory().roleBuilder();
    }
}

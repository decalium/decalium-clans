package org.gepron1x.clans.plugin;

import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.plugin.clan.ClanHomeImpl;
import org.gepron1x.clans.plugin.clan.DraftClanImpl;
import org.gepron1x.clans.plugin.clan.member.ClanMemberImpl;
import org.gepron1x.clans.plugin.clan.member.ClanRoleImpl;
import org.jetbrains.annotations.NotNull;

public final class ClanBuilderFactoryImpl implements ClanBuilderFactory {
    @Override
    public @NotNull DraftClan.Builder draftClanBuilder() {
        return DraftClanImpl.builder();
    }

    @Override
    public @NotNull ClanMember.Builder memberBuilder() {
        return ClanMemberImpl.builder();
    }

    @Override
    public @NotNull ClanHome.Builder homeBuilder() {
        return ClanHomeImpl.builder();
    }

    @Override
    public @NotNull ClanRole.Builder roleBuilder() {
        return ClanRoleImpl.builder();
    }
}

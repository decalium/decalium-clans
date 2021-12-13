package org.gepron1x.clans.api;

import net.kyori.adventure.util.Index;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.jetbrains.annotations.NotNull;

public interface DecaliumClansApi {

    @NotNull ClanManager getClanManager();

    @NotNull ClanCache getClanCache();

    @NotNull Clan.Builder clanBuilder();

    @NotNull ClanMember.Builder memberBuilder();

    @NotNull ClanHome.Builder homeBuilder();

    @NotNull ClanRole.Builder roleBuilder();

    @NotNull RoleRegistry getRoleRegistry();

    @NotNull Index<String, ClanRole> getRoles();






}

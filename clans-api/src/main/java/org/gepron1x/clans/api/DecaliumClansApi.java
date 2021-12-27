package org.gepron1x.clans.api;

import org.jetbrains.annotations.NotNull;

public interface DecaliumClansApi {

    @NotNull ClanManager getClanManager();

    @NotNull ClanCache getClanCache();

    @NotNull ClanBuilderFactory getBuilderFactory();

    @NotNull RoleRegistry getRoleRegistry();







}

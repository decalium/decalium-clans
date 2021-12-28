package org.gepron1x.clans;

import org.gepron1x.clans.api.*;
import org.jetbrains.annotations.NotNull;

public final class DecaliumClansApiImpl implements DecaliumClansApi {

    private final ClanManager clanManager;
    private final ClanCache cache;
    private final RoleRegistry roleRegistry;
    private final ClanBuilderFactory builderFactory;

    public DecaliumClansApiImpl(@NotNull ClanManager clanManager, @NotNull ClanCache cache, @NotNull RoleRegistry roleRegistry, @NotNull ClanBuilderFactory builderFactory) {

        this.clanManager = clanManager;
        this.cache = cache;
        this.roleRegistry = roleRegistry;
        this.builderFactory = builderFactory;
    }
    @Override
    public @NotNull ClanManager getClanManager() {
        return clanManager;
    }

    @Override
    public @NotNull ClanCache getClanCache() {
        return cache;
    }

    @Override
    public @NotNull ClanBuilderFactory getBuilderFactory() {
        return builderFactory;
    }


    @Override
    public @NotNull RoleRegistry getRoleRegistry() {
        return roleRegistry;
    }

}

package org.gepron1x.clans.plugin;

import org.gepron1x.clans.api.*;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

public final class DecaliumClansApiImpl implements DecaliumClansApi {

    private final ClanManager clanManager;
    private final ClanCache cache;
    private final RoleRegistry roleRegistry;
    private final ClanBuilderFactory builderFactory;
    private final FactoryOfTheFuture futuresFactory;

    public DecaliumClansApiImpl(@NotNull ClanManager clanManager,
                                @NotNull ClanCache cache,
                                @NotNull RoleRegistry roleRegistry,
                                @NotNull ClanBuilderFactory builderFactory,
                                FactoryOfTheFuture futuresFactory) {

        this.clanManager = clanManager;
        this.cache = cache;
        this.roleRegistry = roleRegistry;
        this.builderFactory = builderFactory;
        this.futuresFactory = futuresFactory;
    }
    @Override
    public @NotNull ClanManager getClanManager() {
        return clanManager;
    }

    @Override
    public @NotNull FactoryOfTheFuture getFuturesFactory() {
        return futuresFactory;
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

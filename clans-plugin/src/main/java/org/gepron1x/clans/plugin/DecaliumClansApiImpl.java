package org.gepron1x.clans.plugin;

import org.gepron1x.clans.api.CachingClanRepository;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.RoleRegistry;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

public final class DecaliumClansApiImpl implements DecaliumClansApi {

    private final CachingClanRepository clanRepository;
    private final RoleRegistry roleRegistry;
    private final ClanBuilderFactory builderFactory;
    private final FactoryOfTheFuture futuresFactory;

    public DecaliumClansApiImpl(@NotNull CachingClanRepository clanRepository,
                                @NotNull RoleRegistry roleRegistry,
                                @NotNull ClanBuilderFactory builderFactory,
                                FactoryOfTheFuture futuresFactory) {

        this.clanRepository = clanRepository;
        this.roleRegistry = roleRegistry;
        this.builderFactory = builderFactory;
        this.futuresFactory = futuresFactory;
    }
    @Override
    public @NotNull CachingClanRepository repository() {
        return clanRepository;
    }

    @Override
    public @NotNull FactoryOfTheFuture futuresFactory() {
        return futuresFactory;
    }

    @Override
    public @NotNull ClanBuilderFactory builderFactory() {
        return builderFactory;
    }


    @Override
    public @NotNull RoleRegistry roleRegistry() {
        return roleRegistry;
    }

}

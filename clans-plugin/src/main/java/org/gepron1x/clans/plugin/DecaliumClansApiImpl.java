package org.gepron1x.clans.plugin;

import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.war.Wars;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

public final class DecaliumClansApiImpl implements DecaliumClansApi {

    private final CachingClanRepository clanRepository;
    private final RoleRegistry roleRegistry;
    private final ClanBuilderFactory builderFactory;
    private final FactoryOfTheFuture futuresFactory;
    private final Wars wars;

    public DecaliumClansApiImpl(@NotNull CachingClanRepository clanRepository,
                                @NotNull RoleRegistry roleRegistry,
                                @NotNull ClanBuilderFactory builderFactory,
                                @NotNull FactoryOfTheFuture futuresFactory,
                                @NotNull Wars wars) {

        this.clanRepository = clanRepository;
        this.roleRegistry = roleRegistry;
        this.builderFactory = builderFactory;
        this.futuresFactory = futuresFactory;
        this.wars = wars;
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

    @Override
    public @NotNull Wars wars() {
        return wars;
    }

}

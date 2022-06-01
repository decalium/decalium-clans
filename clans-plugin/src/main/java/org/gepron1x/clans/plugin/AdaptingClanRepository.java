package org.gepron1x.clans.plugin;

import org.gepron1x.clans.api.ClanCreationResult;
import org.gepron1x.clans.api.ClanRepository;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AdaptingClanRepository implements ClanRepository {

    private final ClanRepository repository;
    private final Function<Clan, ? extends Clan> mappingFunction;

    public AdaptingClanRepository(ClanRepository repository, Function<Clan, ? extends Clan> mappingFunction) {
        this.repository = repository;
        this.mappingFunction = mappingFunction;
    }


    @Override
    public @NotNull CentralisedFuture<ClanCreationResult> createClan(@NotNull DraftClan draftClan) {
        return this.repository.createClan(draftClan)
                .thenApply(result ->
                        new ClanCreationResult(result.isSuccess() ? mappingFunction.apply(result.orElseThrow()) : null, result.status())
                );
    }

    @Override
    public @NotNull CentralisedFuture<Boolean> removeClan(@NotNull Clan clan) {
        return this.repository.removeClan(clan);
    }

    @Override
    public @NotNull CentralisedFuture<Optional<Clan>> requestClan(@NotNull String tag) {
        return this.repository.requestClan(tag).thenApply(optional -> optional.map(mappingFunction));
    }

    @Override
    public @NotNull CentralisedFuture<Optional<Clan>> requestUserClan(@NotNull UUID uuid) {
        return this.repository.requestUserClan(uuid).thenApply(optional -> optional.map(mappingFunction));
    }

    @Override
    public @NotNull CentralisedFuture<Set<? extends Clan>> clans() {
        return this.repository.clans().thenApply(clans -> clans.stream().map(mappingFunction).collect(Collectors.toUnmodifiableSet()));
    }

}

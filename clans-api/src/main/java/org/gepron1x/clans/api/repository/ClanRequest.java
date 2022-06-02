package org.gepron1x.clans.api.repository;

import org.gepron1x.clans.api.clan.Clan;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ClanRequest extends Request<Optional<Clan>> {

    CompletableFuture<Boolean> delete();
}

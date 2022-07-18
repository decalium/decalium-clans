package org.gepron1x.clans.plugin.command.parser;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.repository.CachingClanRepository;

import java.util.Optional;

public final class ClanOfSender {

    private final CachingClanRepository repository;
    private final CommandSender sender;

    public ClanOfSender(CachingClanRepository repository, CommandSender sender) {

        this.repository = repository;
        this.sender = sender;
    }

    public Optional<Clan> clan() {
        return Optional.of(sender).filter(Player.class::isInstance)
                .map(Player.class::cast).flatMap(repository::userClanIfCached);
    }
}

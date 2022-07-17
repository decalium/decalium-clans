package org.gepron1x.clans.plugin.command.parser;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.ParserException;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.repository.CachingClanRepository;

import java.util.*;

public final class MemberParser implements ArgumentParser<CommandSender, ClanMember> {

    public static final Caption UNKNOWN_MEMBER = Caption.of("unknown.member");

    private final CachingClanRepository repository;
    private final Server server;

    public MemberParser(CachingClanRepository repository, Server server) {

        this.repository = repository;
        this.server = server;
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull ClanMember> parse(@NonNull CommandContext<@NonNull CommandSender> commandContext, @NonNull Queue<@NonNull String> inputQueue) {
        String name = Objects.requireNonNull(inputQueue.peek());
        OfflinePlayer player = server.getOfflinePlayerIfCached(name);
        if(player == null) return ArgumentParseResult.failure(new NoMemberFoundException(commandContext, name));
        return clan(commandContext.getSender()).flatMap(clan -> clan.member(player)).map(member -> {
            inputQueue.remove();
            return ArgumentParseResult.success(member);
                }).orElseGet(() -> ArgumentParseResult.failure(new NoMemberFoundException(commandContext, name)));
    }

    @Override
    public @NonNull List<@NonNull String> suggestions(@NonNull CommandContext<CommandSender> commandContext, @NonNull String input) {
        return clan(commandContext.getSender()).map(clan ->
                clan.members().stream()
                .map(m -> m.asOffline(server))
                .map(OfflinePlayer::getName)
                .filter(Objects::nonNull).toList()
        ).orElse(Collections.emptyList());
    }

    public Optional<Clan> clan(CommandSender sender) {
        return Optional.of(sender).filter(Player.class::isInstance)
                .map(Player.class::cast).flatMap(player -> repository.userClanIfCached(player.getUniqueId()));
    }

    @Override
    public int getRequestedArgumentCount() {
        return 1;
    }

    public static class NoMemberFoundException extends ParserException {

        private final String name;

        protected NoMemberFoundException(@NonNull CommandContext<?> context, String name) {
            super(ClanRoleParser.class, context, UNKNOWN_MEMBER);
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}

package org.gepron1x.clans.plugin.command.parser;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.ParserException;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.repository.CachingClanRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

public final class HomeParser implements ArgumentParser<CommandSender, ClanHome> {

    private final CachingClanRepository repository;

    public HomeParser(CachingClanRepository repository) {

        this.repository = repository;
    }
    @Override
    public @NonNull ArgumentParseResult<@NonNull ClanHome> parse(@NonNull CommandContext<@NonNull CommandSender> commandContext, @NonNull Queue<@NonNull String> inputQueue) {
        String name = Objects.requireNonNull(inputQueue.peek());
        return new ClanOfSender(this.repository, commandContext.getSender()).clan()
                .flatMap(clan -> clan.home(name)).map(ArgumentParseResult::success)
                .orElseGet(() -> ArgumentParseResult.failure(new HomeNotFoundException(commandContext, name)));
    }

    @Override
    public @NonNull List<@NonNull String> suggestions(@NonNull CommandContext<CommandSender> commandContext, @NonNull String input) {
        return new ClanOfSender(this.repository, commandContext.getSender()).clan()
                .map(clan -> clan.homes().stream().map(ClanHome::name).toList())
                .orElse(Collections.emptyList());
    }

    @Override
    public int getRequestedArgumentCount() {
        return 1;
    }

    public static final class HomeNotFoundException extends ParserException {

        private HomeNotFoundException(@NonNull CommandContext<?> context, String name) {
            super(HomeParser.class, context, Caption.of("home.not.found"), CaptionVariable.of("name", name));
        }
    }
}

package org.gepron1x.clans.plugin.command.parser;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.api.clan.member.ClanRole;

import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;


public final class ClanRoleParser<C> implements ArgumentParser<C, ClanRole> {

    public static final Caption UNKNOWN_ROLE = Caption.of("unknown.role");


    private final RoleRegistry roleRegistry;

    public ClanRoleParser(@NonNull RoleRegistry roleRegistry) {

        this.roleRegistry = roleRegistry;
    }


    @Override
    public @NonNull ArgumentParseResult<@NonNull ClanRole> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull Queue<@NonNull String> inputQueue) {

        String name = inputQueue.peek();
        if(name == null) return ArgumentParseResult.failure(new NoInputProvidedException(ClanRoleParser.class, commandContext));
        inputQueue.remove();

        return roleRegistry.value(name).map(ArgumentParseResult::success).orElseGet(() -> ArgumentParseResult.failure(new UnknownRoleException(commandContext)));

    }

    @Override
    public @NonNull List<@NonNull String> suggestions(@NonNull CommandContext<C> commandContext, @NonNull String input) {

        return roleRegistry.values().stream()
                .map(ClanRole::name)
                .collect(Collectors.toList());
    }


    public static class UnknownRoleException extends ParserException {

        protected UnknownRoleException(@NonNull CommandContext<?> context) {
            super(ClanRoleParser.class, context, UNKNOWN_ROLE);
        }
    }
}

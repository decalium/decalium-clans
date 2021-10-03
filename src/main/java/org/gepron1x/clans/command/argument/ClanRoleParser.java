package org.gepron1x.clans.command.argument;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.gepron1x.clans.clan.member.role.ClanRole;
import org.gepron1x.clans.exception.NoSuchClanRoleException;
import org.gepron1x.clans.util.registry.ClanRoleRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

public class ClanRoleParser<C> implements ArgumentParser<C, ClanRole> {
    private final ClanRoleRegistry roles;

    public ClanRoleParser(ClanRoleRegistry roles) {
        this.roles = roles;
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull ClanRole> parse(@NonNull CommandContext<@NonNull C> commandContext,
                                                                 @NonNull Queue<@NonNull String> inputQueue) {
        String name = inputQueue.peek();
        if(name == null) {
            return ArgumentParseResult.failure(new NoInputProvidedException(getClass(), commandContext));
        }
        ClanRole role = roles.get(name.toLowerCase(Locale.ROOT));
        if(role == null) {
            return ArgumentParseResult.failure(new NoSuchClanRoleException(getClass(), commandContext, name));
        }
        inputQueue.remove();

        return ArgumentParseResult.success(role);
    }

    @Override
    public @NonNull List<@NonNull String> suggestions(@NonNull CommandContext<C> commandContext, @NonNull String input) {

        List<String> suggestions = new ArrayList<>();
        input = input.toLowerCase(Locale.ROOT);
        for(ClanRole role : roles.values()) {
            String name = role.getName();
            if(role.getName().startsWith(input)) {
                suggestions.add(role.getName());
            }
        }
        return suggestions;
    }
}

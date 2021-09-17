package org.gepron1x.clans.command.argument;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import net.kyori.adventure.util.Index;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.gepron1x.clans.clan.member.role.ClanRole;
import org.gepron1x.clans.exception.NoSuchClanRoleException;

import java.util.Locale;
import java.util.Queue;

public class ClanRoleParser<C> implements ArgumentParser<C, ClanRole> {
    private final Index<String, ClanRole> roles;

    public ClanRoleParser(Index<String, ClanRole> roles) {
        this.roles = roles;
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull ClanRole> parse(@NonNull CommandContext<@NonNull C> commandContext,
                                                                 @NonNull Queue<@NonNull String> inputQueue) {
        String name = inputQueue.peek();
        if(name == null) {
            return ArgumentParseResult.failure(new NoInputProvidedException(getClass(), commandContext));
        }
        ClanRole role = roles.value(name.toLowerCase(Locale.ROOT));
        if(role == null) {
            return ArgumentParseResult.failure(new NoSuchClanRoleException(getClass(), commandContext, name));
        }

        return ArgumentParseResult.success(role);
    }
}

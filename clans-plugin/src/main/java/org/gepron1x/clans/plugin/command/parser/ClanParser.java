/*
 * decalium-clans-rewrite
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans-rewrite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans-rewrite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans-rewrite. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.plugin.command.parser;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.plugin.cache.ClanCache;

import java.util.Queue;

public final class ClanParser implements ArgumentParser<CommandSender, Clan> {

    private final ClanCache cache;

    public ClanParser(ClanCache cache) {

        this.cache = cache;
    }
    @Override
    public @NonNull ArgumentParseResult<@NonNull Clan> parse(@NonNull CommandContext<@NonNull CommandSender> commandContext, @NonNull Queue<@NonNull String> inputQueue) {
        String tag = inputQueue.peek();
        if(tag == null) return ArgumentParseResult.failure(new NoInputProvidedException(ClanParser.class, commandContext));
        Clan clan = cache.getClan(tag);
        if(clan == null) return ArgumentParseResult.failure(new ClanNotFoundException(tag));
        return ArgumentParseResult.success(clan);
    }

    public static final class ClanNotFoundException extends DescribingException {

        public ClanNotFoundException(String tag) {
            super(Placeholder.parsed("tag", "tag"));
        }
    }
}

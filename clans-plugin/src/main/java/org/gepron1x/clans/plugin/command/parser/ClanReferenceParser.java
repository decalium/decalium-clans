/*
 * decalium-clans
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.plugin.command.parser;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.reference.TagClanReference;
import org.gepron1x.clans.api.repository.CachingClanRepository;

import java.util.List;
import java.util.Queue;

public final class ClanReferenceParser implements ArgumentParser<CommandSender, ClanReference> {


	private final CachingClanRepository cachingClanRepository;

	public ClanReferenceParser(CachingClanRepository cachingClanRepository) {

		this.cachingClanRepository = cachingClanRepository;
	}

	@Override
	public @NonNull ArgumentParseResult<@NonNull ClanReference> parse(@NonNull CommandContext<@NonNull CommandSender> commandContext, @NonNull Queue<@NonNull String> inputQueue) {
		String value = inputQueue.peek();
		if (value == null)
			return ArgumentParseResult.failure(new NoInputProvidedException(ClanReferenceParser.class, commandContext));
		inputQueue.remove();
		return cachingClanRepository.clanIfCached(value).<ClanReference>map(clan -> new TagClanReference(cachingClanRepository, value))
				.map(ArgumentParseResult::success)
				.orElseGet(() -> ArgumentParseResult.failure(new UnknownClanException(commandContext, value)));
	}

	@Override
	public int getRequestedArgumentCount() {
		return 1;
	}

	@Override
	public @NonNull List<@NonNull String> suggestions(@NonNull CommandContext<CommandSender> commandContext, @NonNull String input) {
		return cachingClanRepository.cachedClans().stream().map(Clan::tag).toList();
	}

	public static class UnknownClanException extends ParserException {

		protected UnknownClanException(@NonNull CommandContext<?> context, String tag) {
			super(ClanReferenceParser.class, context, Caption.of("clans.unknown.clan"), CaptionVariable.of("tag", tag));
		}
	}


}

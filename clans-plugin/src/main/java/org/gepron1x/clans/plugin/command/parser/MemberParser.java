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
import cloud.commandframework.exceptions.parsing.ParserException;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
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
		if (player == null) return ArgumentParseResult.failure(new NoMemberFoundException(commandContext, name));
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
		return new ClanOfSender(this.repository, sender).clan();
	}

	@Override
	public int getRequestedArgumentCount() {
		return 1;
	}

	public static class NoMemberFoundException extends ParserException {

		private final String name;

		protected NoMemberFoundException(@NonNull CommandContext<?> context, String name) {
			super(ClanRoleParser.class, context, UNKNOWN_MEMBER, CaptionVariable.of("name", name));
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
}

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
package org.gepron1x.clans.plugin.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.permission.Permission;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.user.Users;
import org.gepron1x.clans.plugin.config.Configs;
import org.gepron1x.clans.plugin.config.messages.HelpCommandConfig;
import org.gepron1x.clans.plugin.config.settings.Levels;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.*;

public class InviteCommand extends AbstractClanCommand {

	private final ClanBuilderFactory builderFactory;
	private final RoleRegistry roleRegistry;

	private record Invitation(@NotNull UUID sender, @NotNull UUID receiver) {
	}

	private final Table<UUID, String, Invitation> invitations = HashBasedTable.create();


	public InviteCommand(@NotNull Logger logger, @NotNull CachingClanRepository clanRepository, Users users,
						 @NotNull Configs configs,
						 @NotNull FactoryOfTheFuture futuresFactory,
						 @NotNull ClanBuilderFactory builderFactory,
						 @NotNull RoleRegistry roleRegistry) {
		super(logger, clanRepository, users, configs, futuresFactory);
		this.builderFactory = builderFactory;
		this.roleRegistry = roleRegistry;
	}

	@Override
	public void register(CommandManager<CommandSender> manager) {
		Command.Builder<CommandSender> builder = manager.commandBuilder("clan").senderType(Player.class);

		HelpCommandConfig.Messages.Description descriptions = messages.help().messages().descriptions();


		manager.command(builder.literal("invite").meta(CommandMeta.DESCRIPTION, descriptions.invite())
				.permission(Permission.of("clans.invite"))
				.argument(PlayerArgument.of("receiver"))
				.handler(
						clanExecutionHandler(
								new PermissiveClanExecutionHandler(this::invite, ClanPermission.INVITE, this.messages)
						)
				)
		);

		manager.command(builder.literal("accept").meta(CommandMeta.DESCRIPTION, descriptions.accept())
				.permission(Permission.of("clans.invite.accept"))
				.argument(StringArgument.<CommandSender>builder("sender_name")
						.withSuggestionsProvider(this::invitationCompletion))
				.handler(this::acceptInvite)
		);

		manager.command(builder.literal("decline").meta(CommandMeta.DESCRIPTION, descriptions.decline())
				.permission(Permission.of("clans.invite.decline"))
				.argument(StringArgument.<CommandSender>builder("sender_name").withSuggestionsProvider(this::invitationCompletion))
				.handler(this::declineInvite)
		);

	}


	private void invite(CommandContext<CommandSender> context) {
		Player player = (Player) context.getSender();
		Player receiver = context.get("receiver");

		Clan clan = context.get(ClanExecutionHandler.CLAN);
		Levels.PerLevel perLevel = clansConfig.levels().forLevel(clan.level());
		if (clan.members().size() >= perLevel.slots()) {
			this.messages.level().tooManyMembers().with("slots", perLevel.slots()).send(player);
			return;
		}

		this.clanRepository.requestUserClan(receiver.getUniqueId()).thenAcceptSync(opt -> {
			if (opt.isPresent()) {
				messages.playerIsAlreadyInClan().with("player", receiver.displayName()).send(player);
				return;
			}
			invitations.put(receiver.getUniqueId(), player.getName(),
					new Invitation(player.getUniqueId(), receiver.getUniqueId()));
			messages.commands().invitation().invitationSent().with("receiver", receiver.displayName()).send(player);

			messages.commands().invitation().invitationMessage()
					.withMiniMessage("sender", player.getName())
					.with("clan_display_name", clan.displayName()).send(receiver);

		}).exceptionally(exceptionHandler(player));

	}

	private void acceptInvite(CommandContext<CommandSender> context) {
		Player player = (Player) context.getSender();
		String name = context.get("sender_name");
		Invitation invitation = checkInvitation(player, name);
		if (invitation == null) return;
		invitations.row(player.getUniqueId()).clear();
		ClanMember member = builderFactory.memberBuilder().uuid(invitation.receiver()).role(roleRegistry.defaultRole()).build();
		Player senderPlayer = player.getServer().getPlayer(invitation.sender());
		this.clanRepository.requestUserClan(invitation.sender())
				.thenComposeSync(clan -> {
					if (clan.isEmpty()) {
						messages.commands().invitation().clanGotDeleted().send(player);
						return futuresFactory.completedFuture(false);
					}
					return clan.get().edit(edition -> edition.addMember(member)).thenApply(c -> true);
				}).thenAcceptSync(bool -> {
					if (!bool) return;
					if (senderPlayer != null) {
						messages.commands().invitation().playerAccepted().with("receiver", player.displayName()).send(senderPlayer);
					}
				}).exceptionally(exceptionHandler(player));
	}

	private void declineInvite(CommandContext<CommandSender> context) {
		Player player = (Player) context.getSender();
		String name = context.get("sender_name");
		Invitation invitation = checkInvitation(player, name);
		if (invitation == null) return;

		Player senderPlayer = player.getServer().getPlayer(invitation.sender());

		messages.commands().invitation().declined().send(player);
		if (senderPlayer != null) {
			messages.commands().invitation().playerDeclined().with("receiver", player.displayName()).send(senderPlayer);
		}

		invitations.remove(player.getUniqueId(), name);

	}

	@Nullable
	private Invitation checkInvitation(Player player, String name) {
		Invitation invitation = invitations.get(player.getUniqueId(), name);
		if (invitation == null) {
			messages.commands().invitation().noInvitations().with("player", name).send(player);
		}
		return invitation;
	}

	private List<String> invitationCompletion(CommandContext<CommandSender> ctx, String s) {
		return Optional.of(ctx.getSender())
				.filter(Player.class::isInstance)
				.map(Player.class::cast)
				.map(Player::getUniqueId)
				.map(invitations::row).map(Map::keySet)
				.map(List::copyOf).orElse(Collections.emptyList());

	}

}

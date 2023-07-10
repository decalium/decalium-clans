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
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.user.Users;
import org.gepron1x.clans.plugin.config.Configs;
import org.gepron1x.clans.plugin.config.messages.HelpCommandConfig;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

public class MemberCommand extends AbstractClanCommand {


	public MemberCommand(@NotNull Logger logger, CachingClanRepository clanRepository, Users users,
						 @NotNull Configs configs, @NotNull FactoryOfTheFuture futuresFactory) {
		super(logger, clanRepository, users, configs, futuresFactory);
	}

	@Override
	public void register(CommandManager<CommandSender> manager) {

		HelpCommandConfig.Messages.Description.Member descriptions = messages.help().messages().descriptions().member();

		Command.Builder<CommandSender> builder = manager.commandBuilder("clan").literal("member").senderType(Player.class);
		manager.command(builder
				.literal("set")
				.literal("role").meta(CommandMeta.DESCRIPTION, descriptions.setRole())
				.permission(Permission.of("clans.member.set.role"))
				.argument(manager.argumentBuilder(ClanMember.class, "member"))
				.argument(manager.argumentBuilder(ClanRole.class, "role"))
				.handler(clanExecutionHandler(
								new PermissiveClanExecutionHandler(
										this::setRole, ClanPermission.SET_ROLE, this.messages)
						)
				)
		);

		manager.command(builder.literal("kick").meta(CommandMeta.DESCRIPTION, descriptions.kick())
				.permission(Permission.of("clans.member.kick"))
				.argument(manager.argumentBuilder(ClanMember.class, "member"))
				.handler(clanExecutionHandler(
						new PermissiveClanExecutionHandler(this::kickMember, ClanPermission.SET_ROLE, this.messages))
				)
		);

		manager.command(builder.literal("set").literal("owner").meta(CommandMeta.DESCRIPTION, descriptions.setOwner())
				.argument(manager.argumentBuilder(ClanMember.class, "member"))
				.handler(clanExecutionHandler(this::setOwner))
		);

	}


	private void setRole(CommandContext<CommandSender> context) {
		Player player = (Player) context.getSender();
		ClanMember other = context.get("member");
		ClanRole role = context.get("role");
		Clan clan = context.get(ClanExecutionHandler.CLAN);
		ClanMember member = context.get(ClanExecutionHandler.CLAN_MEMBER);


		if (other.equals(member)) {
			messages.cannotDoActionOnYourSelf().send(player);
			return;
		}

		if (other.role().weight() > member.role().weight()) {
			messages.commands().member().memberHasHigherWeight().with("member", member).send(player);
			return;
		}

		if (member.role().weight() <= role.weight()) {
			messages.commands().member().role().roleHasHigherWeight().with("role", role.displayName()).send(player);
			return;
		}

		clan.edit(edition -> edition.editMember(other.uniqueId(), memberEdition -> memberEdition.appoint(role)))
				.thenAccept(c -> messages.commands().member().role().success().send(player))
				.exceptionally(exceptionHandler(player));

	}

	private void setOwner(CommandContext<CommandSender> context) {
		Clan clan = context.get(ClanExecutionHandler.CLAN);
		ClanMember member = context.get(ClanExecutionHandler.CLAN_MEMBER);
		Player player = (Player) context.getSender();
		ClanMember newOwner = context.get("member");
		if (!clan.owner().equals(member)) {
			this.messages.commands().member().onlyOwnerCanDoThis().send(player);
			return;
		}
		clan.edit(edition -> {
			edition.owner(newOwner).editMember(newOwner.uniqueId(), memberEdition -> memberEdition.appoint(member.role()));
		}).exceptionally(exceptionHandler(player));

	}


	private void kickMember(CommandContext<CommandSender> context) {
		Player player = (Player) context.getSender();

		Clan clan = context.get(ClanExecutionHandler.CLAN);
		ClanMember member = context.get(ClanExecutionHandler.CLAN_MEMBER);

		ClanMember other = context.get("member");
		if (other.equals(member)) {
			messages.cannotDoActionOnYourSelf().send(player);
			return;
		}


		if (other.role().weight() >= member.role().weight()) {
			messages.commands().member().memberHasHigherWeight().with("member", member).send(player);
		}


		clan.edit(clanEdition -> clanEdition.removeMember(other)).thenAccept(newClan -> {
			messages.commands().member().kick().success().send(player);
		}).exceptionally(exceptionHandler(player));
	}


}

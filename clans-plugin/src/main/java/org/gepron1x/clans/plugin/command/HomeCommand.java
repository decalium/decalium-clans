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
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionHandler;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.permission.Permission;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.Validations;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.user.Users;
import org.gepron1x.clans.plugin.command.argument.ComponentArgument;
import org.gepron1x.clans.plugin.config.Configs;
import org.gepron1x.clans.plugin.config.messages.HelpCommandConfig;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

public class HomeCommand extends AbstractClanCommand {
	private final ClanBuilderFactory builderFactory;

	public HomeCommand(@NotNull Logger logger,
					   @NotNull CachingClanRepository clanRepository,
					   Users users,
					   @NotNull Configs configs,
					   @NotNull FactoryOfTheFuture futuresFactory,
					   @NotNull ClanBuilderFactory builderFactory) {
		super(logger, clanRepository, users, configs, futuresFactory);
		this.builderFactory = builderFactory;
	}

	@Override
	public void register(CommandManager<CommandSender> manager) {

		HelpCommandConfig.Messages.Description.Home description = messages.help().messages().descriptions().home();
		Command.Builder<CommandSender> builder = manager.commandBuilder("clan")
				.literal("home")
				.senderType(Player.class);


		manager.command(builder.literal("create").meta(CommandMeta.DESCRIPTION, description.create())
				.permission(Permission.of("clans.home.create"))
				.argument(StringArgument.of("name"))
				.argument(ComponentArgument.<CommandSender>builder("display_name").mode(StringArgument.StringMode.GREEDY_FLAG_YIELDING).serializer(clansConfig.userComponentFormat()).asOptional())
				.handler(
						clanExecutionHandler(
								new PermissiveClanExecutionHandler(this::createHome, ClanPermission.ADD_HOME, this.messages)
						)
				)
		);

		manager.command(builder.literal("delete").meta(CommandMeta.DESCRIPTION, description.delete())
				.permission(Permission.of("clans.home.delete"))
				.argument(manager.argumentBuilder(ClanHome.class, "home"))
				.handler(clanExecutionHandler(new PermissiveClanExecutionHandler(
						checkHomeOwner(this::deleteHome),
						ClanPermission.REMOVE_HOME,
						this.messages

				)))
		);

		manager.command(builder.literal("teleport").meta(CommandMeta.DESCRIPTION, description.teleport())
				.permission(Permission.of("clans.home.teleport"))
				.argument(manager.argumentBuilder(ClanHome.class, "home"))
				.handler(clanExecutionHandler(
								this::teleportToHome
						)
				)
		);

		manager.command(builder.literal("rename").meta(CommandMeta.DESCRIPTION, description.rename())
				.permission(Permission.of("clans.home.rename"))
				.argument(manager.argumentBuilder(ClanHome.class, "home"))
				.argument(ComponentArgument.greedy("display_name", clansConfig.userComponentFormat()))
				.handler(clanExecutionHandler(
								checkHomeOwner(this::renameHome)
						)
				)
		);

	}

	private void createHome(CommandContext<CommandSender> context) {
		Player player = (Player) context.getSender();
		String name = context.get("name");
		if (!Validations.checkHomeName(name)) {
			this.messages.commands().home().invalidHomeName().send(player);
			return;
		}
		Component displayName = context.<Component>getOptional("display_name").orElseGet(() -> Component.text(name, NamedTextColor.GRAY));

		ItemStack icon = player.getInventory().getItemInMainHand();
		if (icon.getType().isAir()) {
			icon = new ItemStack(Material.PLAYER_HEAD);
			icon.editMeta(meta -> ((SkullMeta) meta).setPlayerProfile(player.getPlayerProfile()));
		}
		Location location = player.getLocation();

		ClanHome home = builderFactory.homeBuilder()
				.name(name)
				.creator(player.getUniqueId())
				.displayName(displayName)
				.location(location)
				.icon(icon).build();

		Clan clan = context.get(ClanExecutionHandler.CLAN);
		if (clan.homes().size() >= this.clansConfig.homes().maxHomes()) {
			messages.commands().home().tooManyHomes().send(player);
			return;
		}
		if (clan.home(name).isPresent()) {
			messages.commands().home().homeAlreadyExists().with("name", name).send(player);
			return;
		}
		clan.edit(edition -> edition.addHome(home)).thenAccept(c -> messages.commands().home().created().send(player))
				.exceptionally(this.exceptionHandler(context.getSender()));
	}

	private void deleteHome(CommandContext<CommandSender> context) {
		Player player = (Player) context.getSender();

		Clan clan = context.get(ClanExecutionHandler.CLAN);
		ClanHome home = context.get("home");

		clan.edit(edition -> edition.removeHome(home)).thenAccept(c -> {
			messages.commands().home().deleted().send(player);
		}).exceptionally(exceptionHandler(player));
	}

	private void teleportToHome(CommandContext<CommandSender> context) {
		Player player = (Player) context.getSender();
		ClanHome home = context.get("home");
		player.teleportAsync(home.location()).thenAccept(bool -> {
			if (bool) this.messages.commands().home().teleported().with("home", home.displayName()).send(player);
		}).exceptionally(exceptionHandler(player));
	}

	private void renameHome(CommandContext<CommandSender> context) {
		Clan clan = context.get(ClanExecutionHandler.CLAN);
		ClanHome home = context.get("home");
		Component name = context.get("display_name");
		clan.edit(edition -> edition.editHome(home.name(), homeEdition -> {
			homeEdition.rename(name);
		})).thenAccept(c -> {
			this.messages.commands().home().renamed().send(context.getSender());
		}).exceptionally(exceptionHandler(context.getSender()));
	}


	private CommandExecutionHandler<CommandSender> checkHomeOwner(CommandExecutionHandler<CommandSender> delegate) {
		return ctx -> {
			ClanMember member = ctx.get(ClanExecutionHandler.CLAN_MEMBER);
			ClanHome home = ctx.get("home");
			if (!home.creator().equals(member.uniqueId()) && !member.hasPermission(ClanPermission.EDIT_OTHERS_HOMES)) {
				this.messages.noClanPermission().send(ctx.getSender());
				return;
			}
			delegate.execute(ctx);
		};
	}


}

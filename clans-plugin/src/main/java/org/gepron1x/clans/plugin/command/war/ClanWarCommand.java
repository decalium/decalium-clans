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
package org.gepron1x.clans.plugin.command.war;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionHandler;
import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.permission.Permission;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import io.leangen.geantyref.TypeToken;
import io.papermc.paper.util.Tick;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.audience.ClanAudience;
import org.gepron1x.clans.api.chat.ClanTagResolver;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.reference.TagClanReference;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.user.Users;
import org.gepron1x.clans.api.util.ClanOnlinePlayers;
import org.gepron1x.clans.api.util.player.PlayerReference;
import org.gepron1x.clans.api.war.Wars;
import org.gepron1x.clans.plugin.DecaliumClansPlugin;
import org.gepron1x.clans.plugin.command.AbstractClanCommand;
import org.gepron1x.clans.plugin.command.ClanExecutionHandler;
import org.gepron1x.clans.plugin.command.parser.ClanOfSender;
import org.gepron1x.clans.plugin.config.Configs;
import org.gepron1x.clans.plugin.config.messages.HelpCommandConfig;
import org.slf4j.Logger;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class ClanWarCommand extends AbstractClanCommand {
	private final Wars wars;

	private static final CloudKey<ClanWarRequest> REQUEST = SimpleCloudKey.of("decaliumclans_request", TypeToken.get(ClanWarRequest.class));
	private final Table<String, String, ClanWarRequest> requestMap = HashBasedTable.create();

	public ClanWarCommand(Logger logger,
						  CachingClanRepository clanRepository,
						  Users users,
						  Configs configs,
						  FactoryOfTheFuture futuresFactory,
						  Wars wars
	) {
		super(logger, clanRepository, users, configs, futuresFactory);
		this.wars = wars;
	}

	@Override
	public void register(CommandManager<CommandSender> manager) {
		HelpCommandConfig.Messages.Description.War descriptions = messages.help().messages().descriptions().war();
		Command.Builder<CommandSender> builder = manager.commandBuilder("clan").literal("war").senderType(Player.class);

		manager.command(builder.literal("request").meta(CommandMeta.DESCRIPTION, descriptions.request())
				.permission(Permission.of("clans.war.request"))
				.argument(manager.argumentBuilder(ClanReference.class, "clan"))
				.handler(
						clanExecutionHandler(
								permissionRequired(this::requestWar, ClanPermission.SEND_WAR_REQUEST)
						)
				)
		);

		manager.command(builder.literal("accept").meta(CommandMeta.DESCRIPTION, descriptions.accept())
				.permission(Permission.of("clans.war.accept"))
				.argument(StringArgument.<CommandSender>newBuilder("tag").withSuggestionsProvider(this::requestsCompletion))
				.handler(
						clanExecutionHandler(
								permissionRequired(requireRequest(this::acceptWar), ClanPermission.ACCEPT_WAR)
						)
				)
		);

		manager.command(builder.literal("decline").meta(CommandMeta.DESCRIPTION, descriptions.decline())
				.permission(Permission.of("clans.war.decline"))
				.argument(StringArgument.<CommandSender>newBuilder("tag").withSuggestionsProvider(this::requestsCompletion))
				.handler(clanExecutionHandler(permissionRequired(requireRequest(this::declineWar), ClanPermission.ACCEPT_WAR)))
		);


	}


	private void requestWar(CommandContext<CommandSender> context) {
		Player player = (Player) context.getSender();
		Clan clan = context.get(ClanExecutionHandler.CLAN);
		ClanReference actor = new TagClanReference(this.clanRepository, clan.tag());
		ClanReference victim = context.get("clan");

		if (victim.cached().isEmpty()) {
			this.messages.noOnlinePlayers().send(player);
			return;
		}
		if (victim.equals(actor)) {
			this.messages.cannotDoActionOnYourSelf().send(player);
			return;
		}

		victim.cached().map(Clan::members).ifPresent(members -> {
			TagResolver resolver = ClanTagResolver.prefixed(clan);
			for (ClanMember member : members) {
				member.asPlayer(player.getServer()).ifPresent(p -> {
					this.messages.commands().wars().requestMessage().with(resolver).send(p);
					if (member.hasPermission(ClanPermission.ACCEPT_WAR)) {
						this.messages.commands().wars().acceptMessage().with(resolver).withMiniMessage("tag", clan.tag()).send(p);
					}
				});
			}
		});

		this.messages.commands().wars().requestSent().send(player);

		PlayerReference ref = PlayerReference.reference(player);

		ClanWarRequest request = new ClanWarRequest(wars, ref, actor, victim);
		String tag = victim.cached().map(Clan::tag).orElseThrow();
		this.requestMap.put(tag, clan.tag(), request);
		Bukkit.getScheduler().runTaskLater(DecaliumClansPlugin.getPlugin(DecaliumClansPlugin.class), () -> {
			this.requestMap.remove(tag, clan.tag());
		}, Tick.tick().fromDuration(Duration.ofMinutes(3)));

	}

	private void acceptWar(CommandContext<CommandSender> context) {
		Player player = (Player) context.getSender();
		ClanWarRequest request = context.get(REQUEST);

		Player captain1 = request.actor().orElseThrow();
		Player captain2 = player;
		Clan actor = request.actorClan().orElseThrow();
		Clan victim = request.victimClan().orElseThrow();
		if(!request.actor().online()) {
			this.messages.commands().wars().captainShouldBeOnline()
					.send(Audience.audience(new ClanAudience(actor), new ClanAudience(victim)));
			return;
		}
		if(!captain1.getWorld().equals(captain2.getWorld()) || !captain1.getWorld().getName().equalsIgnoreCase(clansConfig.wars().warWorld())) {
			this.messages.commands().wars().shouldBeInWarWorld().send(Audience.audience(captain1, captain2));
			return;
		}
		request.accept();
		teleportTeam(captain1, new ClanOnlinePlayers(actor));
		teleportTeam(captain2, new ClanOnlinePlayers(victim));
		this.messages.commands().wars().accepted().with("clan", ClanTagResolver.clan(actor)).send(player);
		this.messages.commands().wars().victimAccepted().with(ClanTagResolver.prefixed(victim))
				.send(new ClanAudience(actor, player.getServer()));
		this.requestMap.remove(context.get(ClanExecutionHandler.CLAN).tag(), context.<String>get("tag"));

	}

	private void declineWar(CommandContext<CommandSender> context) {
		CommandSender sender = context.getSender();
		ClanWarRequest request = context.get(REQUEST);
		Clan actor = request.actorClan().orElseThrow();
		Clan victim = request.victimClan().orElseThrow();
		this.messages.commands().wars().declined().with(ClanTagResolver.prefixed(actor)).send(sender);
		this.messages.commands().wars().victimDeclined().with(ClanTagResolver.prefixed(victim))
				.send(new ClanAudience(actor, sender.getServer()));
		this.requestMap.remove(context.get(ClanExecutionHandler.CLAN).tag(), context.<String>get("tag"));
	}

	private CommandExecutionHandler<CommandSender> requireRequest(CommandExecutionHandler<CommandSender> delegate) {
		return ctx -> {
			String tag = ctx.get("tag");
			Clan clan = ctx.get(ClanExecutionHandler.CLAN);
			ClanWarRequest request = this.requestMap.get(clan.tag(), tag);
			if (request == null || request.actorClan().cached().isEmpty()) {
				this.messages.commands().wars().noRequests().with("tag", tag).send(ctx.getSender());
				return;
			}
			ctx.store(REQUEST, request);
			delegate.execute(ctx);
		};
	}

	private List<String> requestsCompletion(CommandContext<CommandSender> context, String s) {
		return new ClanOfSender(this.clanRepository, context.getSender()).clan()
				.map(Clan::tag)
				.map(requestMap::row)
				.map(Map::keySet).map(List::copyOf)
				.orElseGet(Collections::emptyList);
	}


	private static void teleportTeam(Player leader, Iterable<Player> players) {
		World world = leader.getWorld();
		Location location = leader.getLocation();
		double radius = 3;
		double t = 0;
		for(Player player : players) {
			if(player.equals(leader)) continue;
			double x = (radius * Math.cos(t)) + location.getX();
			double z = (radius * Math.sin(t)) + location.getZ();
			t += Math.PI / 6;
			player.teleportAsync(world.getHighestBlockAt((int) x, (int) z).getLocation().add(0, 1, 0));
		}
	}


}

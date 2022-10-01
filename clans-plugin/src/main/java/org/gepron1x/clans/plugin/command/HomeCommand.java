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
import cloud.commandframework.arguments.flags.CommandFlag;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionHandler;
import cloud.commandframework.permission.Permission;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.edition.home.HomeEdition;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.user.Users;
import org.gepron1x.clans.plugin.command.argument.ComponentArgument;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

public class HomeCommand extends AbstractClanCommand {
    private final ClanBuilderFactory builderFactory;

    public HomeCommand(@NotNull Logger logger,
                       @NotNull CachingClanRepository clanRepository,
                       Users users,
                       @NotNull ClansConfig clansConfig,
                       @NotNull MessagesConfig messages,
                       @NotNull FactoryOfTheFuture futuresFactory,
                       @NotNull ClanBuilderFactory builderFactory) {
        super(logger, clanRepository, users, clansConfig, messages, futuresFactory);
        this.builderFactory = builderFactory;
    }

    @Override
    public void register(CommandManager<CommandSender> manager) {


        Command.Builder<CommandSender> builder = manager.commandBuilder("clan")
                .literal("home")
                .senderType(Player.class);


        manager.command(builder.literal("create")
                .permission(Permission.of("clans.home.create"))
                .flag(CommandFlag.newBuilder("name").withAliases("n", "t").withArgument(StringArgument.of("name")))
                .argument(ComponentArgument.<CommandSender>builder("display_name").mode(StringArgument.StringMode.GREEDY_FLAG_YIELDING).serializer(clansConfig.userComponentFormat()))
                .handler(
                       clanExecutionHandler(
                                new PermissiveClanExecutionHandler(this::createHome, ClanPermission.ADD_HOME, this.messages)
                               )
                )
        );

        manager.command(builder.literal("delete")
                .permission(Permission.of("clans.home.delete"))
                .argument(manager.argumentBuilder(ClanHome.class, "home"))
                .handler(clanExecutionHandler(new PermissiveClanExecutionHandler(
                        checkHomeOwner(this::deleteHome),
                        ClanPermission.REMOVE_HOME,
                        this.messages

                )))
        );

        manager.command(builder.literal("teleport")
                .permission(Permission.of("clans.home.teleport"))
                .argument(manager.argumentBuilder(ClanHome.class, "home"))
                .handler(clanExecutionHandler(
                        this::teleportToHome
                        )
                )
        );

        manager.command(builder.literal("rename")
                .permission(Permission.of("clans.home.rename"))
                .argument(manager.argumentBuilder(ClanHome.class, "home"))
                .argument(ComponentArgument.greedy("display_name", clansConfig.userComponentFormat()))
                .handler(clanExecutionHandler(
                        checkHomeOwner(this::renameHome)
                )
                )
        );

        manager.command(builder.literal("upgrade").permission("clans.home.upgrade")
                .argument(manager.argumentBuilder(ClanHome.class, "home"))
                .handler(clanExecutionHandler(this::upgradeHome)));

    }




    private void createHome(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        Component displayName = context.get("display_name");
        String name = context.flags().get("name");
        if(name == null) name = clansConfig.displayNameFormat().formatTag(displayName);
        if(name.length() < clansConfig.displayNameFormat().minTagSize()) {
            player.sendMessage(this.messages.commands().creation().invalidTag());
        }

        ItemStack icon = player.getInventory().getItemInMainHand();
        if(icon.getType().isAir()) {
            icon = new ItemStack(Material.PLAYER_HEAD);
            icon.editMeta(SkullMeta.class, meta -> meta.setPlayerProfile(player.getPlayerProfile()));
        }
        Location location = player.getLocation();

        ClanHome home = builderFactory.homeBuilder()
                .name(name)
                .creator(player.getUniqueId())
                .displayName(displayName)
                .location(location)
                .icon(icon).build();

        Clan clan = context.get(ClanExecutionHandler.CLAN);
        if(clan.homes().size() >= this.clansConfig.homes().maxHomes()) {
            player.sendMessage(messages.commands().home().tooManyHomes());
            return;
        }
        if(clan.home(name).isPresent()) {
            player.sendMessage(messages.commands().home().homeAlreadyExists().with("name", name));
            return;
        }
       clan.edit(edition -> edition.addHome(home)).thenAccept(c -> player.sendMessage(messages.commands().home().created()))
               .exceptionally(this.exceptionHandler(context.getSender()));
    }

    private void deleteHome(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();

        Clan clan = context.get(ClanExecutionHandler.CLAN);
        ClanHome home = context.get("home");

       clan.edit(edition -> edition.removeHome(home)).thenAccept(c -> {
            if(c != null) player.sendMessage(messages.commands().home().deleted());
        }).exceptionally(exceptionHandler(player));
    }

    private void teleportToHome(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        ClanHome home = context.get("home");
        player.teleportAsync(home.location()).thenAccept(bool -> {
            if(bool) player.sendMessage(this.messages.commands().home().teleported().with("home", home.displayName()));
        }).exceptionally(exceptionHandler(player));
    }

    private void renameHome(CommandContext<CommandSender> context) {
        Clan clan = context.get(ClanExecutionHandler.CLAN);
        ClanHome home = context.get("home");
        Component name = context.get("display_name");
        clan.edit(edition -> edition.editHome(home.name(), homeEdition -> {
            homeEdition.rename(name);
        })).thenAccept(c -> {
            context.getSender().sendMessage(this.messages.commands().home().renamed());
        }).exceptionally(exceptionHandler(context.getSender()));
    }

    private void upgradeHome(CommandContext<CommandSender> context) {
        Clan clan = context.get(ClanExecutionHandler.CLAN);
        ClanHome home = context.get("home");
        clan.edit(edition -> edition.editHome(home.name(), HomeEdition::upgrade)).thenAccept(c -> {
            context.getSender().sendMessage(this.messages.commands().home().upgraded().with("level", home.level() + 1));
        }).exceptionally(this.exceptionHandler(context.getSender()));
    }



    private CommandExecutionHandler<CommandSender> checkHomeOwner(CommandExecutionHandler<CommandSender> delegate) {
        return ctx -> {
            ClanMember member = ctx.get(ClanExecutionHandler.CLAN_MEMBER);
            ClanHome home = ctx.get("home");
            if(!home.creator().equals(member.uniqueId()) && !member.hasPermission(ClanPermission.EDIT_OTHERS_HOMES)) {
                ctx.getSender().sendMessage(this.messages.noClanPermission());
                return;
            }
            delegate.execute(ctx);
        };
    }




}

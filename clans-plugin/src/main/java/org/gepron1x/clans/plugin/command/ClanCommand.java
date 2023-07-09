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
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.permission.Permission;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.api.chat.ClanTagResolver;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.user.Users;
import org.gepron1x.clans.plugin.command.argument.ComponentArgument;
import org.gepron1x.clans.plugin.config.Configs;
import org.gepron1x.clans.plugin.config.messages.HelpCommandConfig;
import org.gepron1x.clans.plugin.util.message.Message;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.UUID;

public class ClanCommand extends AbstractClanCommand {




    private final RoleRegistry roleRegistry;
    private final ClanBuilderFactory builderFactory;

    public ClanCommand(@NotNull Logger logger, CachingClanRepository repository, @NotNull Users users,
                       @NotNull Configs configs,
                       @NotNull FactoryOfTheFuture futuresFactory,
                       @NotNull ClanBuilderFactory builderFactory,
                       @NotNull RoleRegistry roleRegistry) {

        super(logger, repository, users, configs, futuresFactory);
        this.builderFactory = builderFactory;
        this.roleRegistry = roleRegistry;
    }
    @Override
    public void register(CommandManager<CommandSender> manager) {

        Command.Builder<CommandSender> builder = manager.commandBuilder("clan").senderType(Player.class);

        HelpCommandConfig.Messages.Description descriptions = messages.help().messages().descriptions();

        manager.command(builder.literal("create").meta(CommandMeta.DESCRIPTION, descriptions.create())
                .permission("clans.create")
                .argument(StringArgument.of("tag", StringArgument.StringMode.GREEDY))
                .handler(this::createClan)
        );

        manager.command(builder.literal("delete").meta(CommandMeta.DESCRIPTION, descriptions.delete())
                .permission(Permission.of("clans.delete"))
                .handler(clanExecutionHandler(
                                new PermissiveClanExecutionHandler(this::deleteClan, ClanPermission.DISBAND, this.messages)
                                )
                )
        );

        manager.command(builder.literal("rename").meta(CommandMeta.DESCRIPTION, descriptions.rename())
                .permission(Permission.of("clans.rename"))
                .argument(ComponentArgument.greedy("display_name", clansConfig.userComponentFormat()))
                .handler(
                        clanExecutionHandler(
                                new PermissiveClanExecutionHandler(this::rename, ClanPermission.SET_DISPLAY_NAME, this.messages)
                        )
                )
        );

        manager.command(builder.literal("member").literal("list").meta(CommandMeta.DESCRIPTION, descriptions.member().list())
                .permission(Permission.of("clans.member.list"))
                .handler(clanExecutionHandler(this::listMembers)));

        manager.command(builder.literal("info", "myclan").meta(CommandMeta.DESCRIPTION, descriptions.info())
                .permission(Permission.of("clans.info"))
                .handler(clanExecutionHandler(this::myClan)));

        manager.command(builder.literal("leave").meta(CommandMeta.DESCRIPTION, descriptions.leave())
                .permission(Permission.of("clans.leave"))
                .handler(clanExecutionHandler(this::leaveClan)));

        manager.command(builder.literal("upgrade").meta(CommandMeta.DESCRIPTION, descriptions.upgrade()).permission("clans.upgrade")
                .handler(clanExecutionHandler(this::upgradeClan)));
    }


    private void createClan(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();

        String tag = context.get("tag");
        if(tag.length() < clansConfig.displayNameFormat().minTagSize()) {
            player.sendMessage(this.messages.commands().creation().invalidTag());
			return;
        }

		if(!clansConfig.displayNameFormat().tagRegex().matcher(tag).matches()) {
			player.sendMessage(this.messages.commands().creation().invalidTag());
			return;
		}

        UUID uuid = player.getUniqueId();

        ClanMember member = builderFactory.memberBuilder()
                .uuid(uuid)
                .role(roleRegistry.ownerRole())
                .build();

        DraftClan clan = builderFactory.draftClanBuilder()
                .tag(tag)
                .displayName(Component.text(tag))
                .owner(member)
                .build();

        users.userFor(player).create(clan).thenAcceptSync(result -> {
            if(result.isSuccess()) {
                player.sendMessage(messages.commands().creation().success().with("tag", tag).with("name", tag));
            } else {
                player.sendMessage(switch (result.status()) {
                    case MEMBERS_IN_OTHER_CLANS -> messages.alreadyInClan();
                    case ALREADY_EXISTS -> messages.commands().creation().clanWithTagAlreadyExists();
                    default -> throw new IllegalStateException("Unexpected value: " + result.status());
                });
            }
        }).exceptionally(exceptionHandler(player));

    }

    private void rename(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        Clan clan = context.get(ClanExecutionHandler.CLAN);
        Component displayName = context.get("display_name");
        clan.edit(edition -> edition.rename(displayName))
                .thenAccept(c -> player.sendMessage(this.messages.commands().displayNameSet().with("name", displayName)))
                .exceptionally(exceptionHandler(player));
    }

    private void myClan(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        Clan clan = context.get(ClanExecutionHandler.CLAN);
        player.sendMessage(this.messages.commands().infoFormat().with("clan", ClanTagResolver.clan(clan)));
    }

    private void listMembers(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        Clan clan = context.get(ClanExecutionHandler.CLAN);
       for(ClanMember member : clan.members()) {
           player.sendMessage(Message.message("<role> <member>")
                   .with("role", member.role())
                   .with("member", member));
       }
    }



    private void leaveClan(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        Clan clan = context.get(ClanExecutionHandler.CLAN);
        if(clan.owner().uniqueId().equals(player.getUniqueId())) {
            player.sendMessage(this.messages.commands().ownerCannotLeave());
            return;
        }
        clan.edit(edition -> edition.removeMember(context.get(ClanExecutionHandler.CLAN_MEMBER))).thenAccept(c -> {
            player.sendMessage(this.messages.commands().left());
        }).exceptionally(exceptionHandler(player));
    }

    private void deleteClan(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        Clan clan = context.get(ClanExecutionHandler.CLAN);
        clanRepository.removeClan(clan).thenAcceptSync(success -> {
            if(success) player.sendMessage(this.messages.commands().deletion().success());
        }).exceptionally(exceptionHandler(player));
    }


    private void upgradeClan(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        Clan clan = context.get(ClanExecutionHandler.CLAN);
        clan.edit(ClanEdition::upgrade).thenAccept(c -> player.sendMessage(this.messages.level().upgraded().with("level", c.level())))
                .exceptionally(exceptionHandler(player));
    }
}

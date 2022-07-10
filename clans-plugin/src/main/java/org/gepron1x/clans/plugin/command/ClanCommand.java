package org.gepron1x.clans.plugin.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.api.Validations;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.gepron1x.clans.plugin.command.argument.ComponentArgument;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.gepron1x.clans.plugin.util.message.Message;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.UUID;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;

public class ClanCommand extends AbstractClanCommand {


    private final RoleRegistry roleRegistry;
    private final ClanBuilderFactory builderFactory;

    public ClanCommand(@NotNull Logger logger, @NotNull CachingClanRepository repository,
                       @NotNull ClansConfig config,
                       @NotNull MessagesConfig messages,
                       @NotNull FactoryOfTheFuture futuresFactory,
                       @NotNull ClanBuilderFactory builderFactory,
                       @NotNull RoleRegistry roleRegistry) {

        super(logger, repository, config, messages, futuresFactory);
        this.builderFactory = builderFactory;
        this.roleRegistry = roleRegistry;
    }
    @Override
    public void register(CommandManager<CommandSender> manager) {

        Command.Builder<CommandSender> builder = manager.commandBuilder("clan").senderType(Player.class);

        manager.command(builder.literal("create")
                .permission("clans.create")
                .argument(StringArgument.of("tag"))
                .argument(ComponentArgument.optional("display_name", StringArgument.StringMode.GREEDY))
                .handler(this::createClan)
        );

        manager.command(builder.literal("delete")
                .permission("clans.delete")
                .handler(clanExecutionHandler(
                                new PermissiveClanExecutionHandler(this::deleteClan, ClanPermission.DISBAND, this.messages)
                                )
                )
        );

        manager.command(builder.literal("rename")
                .permission("clans.rename")
                .argument(ComponentArgument.greedy("display_name"))
                .handler(
                        clanExecutionHandler(
                                new PermissiveClanExecutionHandler(this::setDisplayName, ClanPermission.SET_DISPLAY_NAME, this.messages)
                        )
                )
        );

        manager.command(builder.literal("memberlist").permission("clans.memberlist")
                .handler(clanExecutionHandler(this::listMembers)));

        manager.command(builder.literal("myclan").permission("clans.myclan")
                .handler(clanExecutionHandler(this::myClan)));

        manager.command(builder.literal("leave").permission("clans.leave")
                .handler(clanExecutionHandler(this::leaveClan)));
    }


    private void createClan(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        String tag = context.get("tag");
        if(!Validations.checkTag(tag)) {
            player.sendMessage(this.messages.commands().creation().invalidTag());
            return;
        }

        Component displayName = context.<Component>getOptional("display_name")
                .orElseGet(() -> text(tag, NamedTextColor.GRAY));

        UUID uuid = player.getUniqueId();

        ClanMember member = builderFactory.memberBuilder()
                .uuid(uuid)
                .role(roleRegistry.ownerRole())
                .build();

        DraftClan clan = builderFactory.draftClanBuilder()
                .tag(tag)
                .displayName(displayName)
                .owner(member)
                .build();

        clanRepository.createClan(clan).thenAcceptSync(result -> {
            if(result.isSuccess()) {
                player.sendMessage(messages.commands().creation().success().with("tag", tag).with("name", displayName));
            } else {
                player.sendMessage(switch (result.status()) {
                    case MEMBERS_IN_OTHER_CLANS -> messages.alreadyInClan();
                    case ALREADY_EXISTS -> messages.commands().creation().clanWithTagAlreadyExists();
                    default -> throw new IllegalStateException("Unexpected value: " + result.status());
                });
            }
        }).exceptionally(this::exceptionHandler);

    }

    private void setDisplayName(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        Clan clan = context.get(ClanExecutionHandler.CLAN);
        Component displayName = context.get("display_name");
        clan.edit(edition -> edition.rename(displayName))
                .thenAccept(c -> player.sendMessage(this.messages.commands().displayNameSet().with("name", displayName)));
    }

    private void myClan(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        Clan clan = context.get(ClanExecutionHandler.CLAN);
        player.sendMessage(Component.textOfChildren(text("You are member of "), clan, text("clan")));
        player.sendMessage(Component.textOfChildren(
                Component.textOfChildren(text("Kills: "), text(clan.statisticOr(StatisticType.KILLS, 0))),
                newline(),
                Component.textOfChildren(text("Deaths: "), text(clan.statisticOr(StatisticType.DEATHS, 0)))
        ));
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
        clan.edit(edition -> edition.removeMember(context.get(ClanExecutionHandler.CLAN_MEMBER))).thenAccept(c -> {
            player.sendMessage(this.messages.commands().left());
        });
    }

    private void deleteClan(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        Clan clan = context.get(ClanExecutionHandler.CLAN);
        clanRepository.removeClan(clan).thenAcceptSync(success -> {
            if(success) player.sendMessage(this.messages.commands().deletion().success());
        });
    }
}

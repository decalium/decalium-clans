package org.gepron1x.clans.plugin.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.CachingClanRepository;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.plugin.command.argument.ComponentArgument;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.gepron1x.clans.plugin.util.message.Message;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Objects;
import java.util.UUID;

public class ClanCommand extends AbstractClanCommand {


    private final RoleRegistry roleRegistry;
    private final ClanBuilderFactory builderFactory;

    public ClanCommand(@NotNull Logger logger, @NotNull CachingClanRepository manager,
                       @NotNull ClansConfig config,
                       @NotNull MessagesConfig messages,
                       @NotNull FactoryOfTheFuture futuresFactory,
                       @NotNull ClanBuilderFactory builderFactory,
                       @NotNull RoleRegistry roleRegistry) {

        super(logger, manager, config, messages, futuresFactory);
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
                .handler(this::deleteClan)
        );

        manager.command(builder.literal("set").literal("displayname")
                .permission("clans.set.displayname")
                .argument(ComponentArgument.greedy("display_name"))
                .handler(this::setDisplayName)
        );

        manager.command(builder.literal("memberlist").permission("clans.memberlist").handler(this::listMembers));

        manager.command(builder.literal("myclan").permission("clans.myclan").handler(this::myClan));
    }


    private void createClan(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        String tag = context.get("tag");
        Component displayName = context.<Component>getOptional("display_name")
                .orElseGet(() -> Component.text(tag, NamedTextColor.GRAY));

        UUID uuid = player.getUniqueId();

        ClanMember member = builderFactory.memberBuilder()
                .uuid(uuid)
                .role(roleRegistry.getOwnerRole())
                .build();

        DraftClan clan = builderFactory.draftClanBuilder()
                .tag(tag)
                .displayName(displayName)
                .owner(member)
                .build();

        clanManager.createClan(clan).thenAcceptSync(result -> {
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
        Component displayName = context.get("display_name");

        requireClan(player).thenComposeSync(clan -> {
            if(clan == null) return nullFuture();
            ClanMember member = getMember(clan, player);

            if(!checkPermission(player, member, ClanPermission.SET_DISPLAY_NAME)) return nullFuture();

            return clanManager.editClan(clan, editor -> editor.setDisplayName(displayName));
        }).thenAcceptSync(c -> {
            if(c != null) player.sendMessage(messages.commands().displayNameSet().with("name", displayName));
        }).exceptionally(this::exceptionHandler);
    }

    private void myClan(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        UUID uuid = player.getUniqueId();
        clanManager.requestUserClan(uuid).thenAcceptSync(clan -> {
            if(!checkClan(player, clan)) return;
            Objects.requireNonNull(clan);
            player.sendMessage(Component.text().color(NamedTextColor.AQUA).append(Component.text("You are a member of ")).append(clan.getDisplayName()).append(Component.text(" clan")));
        }).exceptionally(this::exceptionHandler);;
    }

    private void listMembers(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        UUID uuid = player.getUniqueId();
        requireClan(player).thenAcceptSync(clan -> {
            if(clan == null) return;
            for(ClanMember member : clan.getMembers()) {
                player.sendMessage(Message.message("<role> <member>")
                        .with("role", member.role())
                        .with("member", member.asOffline(player.getServer()).getName()));
            }
        });
    }

    private void deleteClan(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        UUID uuid = player.getUniqueId();

        requireClan(player).thenComposeSync(clan -> {
            if(clan == null) return futuresFactory.completedFuture(false);
            ClanMember member = getMember(clan, uuid);
            if(!checkPermission(player, member, ClanPermission.DISBAND)) return futuresFactory.completedFuture(false);
            return this.clanManager.removeClan(clan);
        }).thenAcceptSync(success -> {
            if(success) player.sendMessage(messages.commands().deletion().success());
        }).exceptionally(this::exceptionHandler);;
    }
}

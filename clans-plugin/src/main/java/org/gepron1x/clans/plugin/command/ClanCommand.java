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
import org.gepron1x.clans.api.ClanManager;
import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.plugin.command.argument.ComponentArgument;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;

public class ClanCommand extends AbstractCommand {

    private static final String TAG = "tag";

    private final RoleRegistry roleRegistry;
    private final ClanManager manager;
    private final ClansConfig config;
    private final MessagesConfig messages;
    private final ClanBuilderFactory builderFactory;

    public ClanCommand(@NotNull ClanBuilderFactory builderFactory,
                       @NotNull RoleRegistry roleRegistry,
                       @NotNull ClanManager manager,
                       @NotNull ClansConfig config,
                       @NotNull MessagesConfig messages
                       ) {
        this.builderFactory = builderFactory;
        this.roleRegistry = roleRegistry;
        this.manager = manager;
        this.config = config;
        this.messages = messages;
    }
    @Override
    public void register(CommandManager<CommandSender> manager) {

        Command.Builder<CommandSender> builder = manager.commandBuilder("clan");
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

        manager.command(builder.literal("myclan").permission("clans.myclan").handler(this::myClan));
    }


    private void createClan(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        String tag = context.get("tag");
        Component displayName = requireNonNull(context.getOrSupplyDefault("display_name", () -> Component.text(tag)));
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

        manager.createClan(clan).thenAcceptSync(result -> {
            if(result.isSuccess()) {
                player.sendMessage(messages.commands().creation().success().with("tag", tag).with("name", displayName));
            } else {

                player.sendMessage(switch (result.status()) {
                    case MEMBERS_IN_OTHER_CLANS -> messages.alreadyInClan();
                    case ALREADY_EXISTS -> messages.commands().creation().clanWithTagAlreadyExists();
                    default -> throw new IllegalStateException("Unexpected value: " + result.status());
                });
            }
        });

    }

    private void myClan(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        UUID uuid = player.getUniqueId();
        manager.getUserClan(uuid).thenAcceptSync(clan -> {
            if(clan == null) {
                player.sendMessage(messages.notInTheClan());
                return;
            }
            player.sendMessage(Component.text().color(NamedTextColor.AQUA).append(Component.text("You are a member of ")).append(clan.getDisplayName()).append(Component.text(" clan")));
        });
    }

    private void deleteClan(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        UUID uuid = player.getUniqueId();

        manager.getUserClan(uuid).thenComposeSync(clan -> {
            if(clan == null) {
                player.sendMessage(messages.notInTheClan());
                return CompletableFuture.completedFuture(false);
            }
            ClanMember member = getMember(clan, uuid);
            if(!member.hasPermission(ClanPermission.DISBAND)) {
                player.sendMessage(messages.noClanPermission());
                return CompletableFuture.completedFuture(false);
            }
            return this.manager.removeClan(clan);
        }).thenAcceptSync(success -> {
            if(success) player.sendMessage(messages.commands().deletion().success());
        });
    }
}

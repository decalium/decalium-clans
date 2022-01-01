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
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class ClanCommand extends AbstractCommand {


    private final RoleRegistry roleRegistry;
    private final ClanBuilderFactory builderFactory;

    public ClanCommand(@NotNull ClanManager manager,
                       @NotNull ClansConfig config,
                       @NotNull MessagesConfig messages,
                       @NotNull FactoryOfTheFuture futuresFactory,
                       @NotNull ClanBuilderFactory builderFactory,
                       @NotNull RoleRegistry roleRegistry) {

        super(manager, config, messages, futuresFactory);
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
        });

    }

    private void myClan(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        UUID uuid = player.getUniqueId();
        clanManager.getUserClan(uuid).thenAcceptSync(clan -> {
            if(!checkClan(player, clan)) return;
            Objects.requireNonNull(clan);
            player.sendMessage(Component.text().color(NamedTextColor.AQUA).append(Component.text("You are a member of ")).append(clan.getDisplayName()).append(Component.text(" clan")));
        });
    }

    private void deleteClan(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        UUID uuid = player.getUniqueId();

        clanManager.getUserClan(uuid).thenComposeSync(clan -> {
            if(!checkClan(player, clan)) return nullFuture();
            Objects.requireNonNull(clan);
            ClanMember member = getMember(clan, uuid);
            if(!checkPermission(player, member, ClanPermission.DISBAND)) return nullFuture();
            return this.clanManager.removeClan(clan);
        }).thenAcceptSync(success -> {
            if(success) player.sendMessage(messages.commands().deletion().success());
        });
    }
}

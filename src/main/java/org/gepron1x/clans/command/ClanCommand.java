package org.gepron1x.clans.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.meta.CommandMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.ParseException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.gepron1x.clans.Permissions;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.ClanBuilder;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.clan.member.role.ClanPermission;
import org.gepron1x.clans.command.argument.ComponentArgument;
import org.gepron1x.clans.config.MessagesConfig;
import org.gepron1x.clans.ClanManager;
import org.gepron1x.clans.util.registry.ClanRoleRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;

public class ClanCommand extends BaseClanCommand {
    private static final String CLAN_TAG = "clan_tag";
    private static final String DISPLAY_NAME = "display_name";
    public static final CommandMeta.Key<Boolean> CLAN_MEMBERS_ONLY = CommandMeta.Key.of(boolean.class, "clan_members_only");
    private final Plugin plugin;
    private final ClanRoleRegistry roles;


    private final Set<UUID> deletionConfirmations = new HashSet<>();


    public ClanCommand(@NotNull Plugin plugin, @NotNull ClanManager manager, @NotNull MessagesConfig messages, @NotNull ClanRoleRegistry roles) {
        super(manager, messages);
        this.plugin = plugin;
        this.roles = roles;
    }

    public void setMessages(MessagesConfig config) {
        this.messages = config;
    }

    private void createClan(@NotNull CommandContext<CommandSender> ctx) {
        String tag = ctx.get(CLAN_TAG);
        Player player = (Player) ctx.getSender();

        if (clanManager.getUserClan(player.getUniqueId()) != null) {
            player.sendMessage(messages.alreadyInClan());
            return;
        }
        if (clanManager.getClan(tag) != null) {
            player.sendMessage(messages.creation().clanWithTagAlreadyExists().with("tag", tag));
            return;
        }
        Component displayName = text(tag, NamedTextColor.GRAY);
        Clan clan = new ClanBuilder(tag).creator(new ClanMember(player, roles.getOwnerRole()))
                .displayName(displayName).build();
        clanManager.addClan(clan);
        player.sendMessage(messages.creation().success().with("name", displayName));

    }

    private void deleteClan(@NotNull CommandContext<CommandSender> ctx) {
        CommandSender sender = ctx.getSender();
        Player player = (Player) sender;
        player.sendMessage(messages.deletion().confirm());
        UUID uuid = player.getUniqueId();
        deletionConfirmations.add(uuid);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> deletionConfirmations.remove(uuid), 60 * 20L);

    }

    private void confirmDeletion(@NotNull CommandContext<CommandSender> ctx) {
        Player player = (Player) ctx.getSender();
        UUID uuid = player.getUniqueId();
        if (!deletionConfirmations.contains(uuid)) {
            player.sendMessage(messages.deletion().nothingToConfirm());
            return;
        }
        Clan clan = clanManager.getUserClan(player);
        if (clan != null) {
            clanManager.deleteClan(clan);
            player.sendMessage(messages.deletion().success());
        }
        deletionConfirmations.remove(uuid);
    }

    private void clanList(@NotNull CommandContext<CommandSender> ctx) {
        CommandSender sender = ctx.getSender();
        clanManager.getClans().stream().map(clan -> {
            Component members = clan
                    .getMembers().stream()
                    .map(member -> messages.clanList().memberFormat()
                            .with("name", member.asOffline().getName())
                            .with("role", member.getRole().getDisplayName()))
                    .map(ComponentLike::asComponent)
                    .collect(Component.toComponent(newline()));

            return
                    messages.clanList().clanFormat()
                            .stream().map(c ->
                                    c.with("tag", clan.getTag())
                                            .with("display_name", clan.getDisplayName())
                                            .with("members", members))
                            .map(ComponentLike::asComponent)
                            .collect(Component.toComponent(newline()));
        }).forEach(sender::sendMessage);

    }

    private void setDisplayName(CommandContext<CommandSender> ctx) {
        Player player = (Player) ctx.getSender();
        Component name = ctx.get(DISPLAY_NAME);
        Clan clan = getClan(player);
        clan.setDisplayName(name);
        player.sendMessage(messages.displayName().success().with("name", name));

    }



    @Override
    public void register(@NotNull CommandManager<CommandSender> manager) {
        Command.Builder<CommandSender> builder = manager.commandBuilder("clan");
        manager.command(builder.literal("create")
                .senderType(Player.class)
                .permission(Permissions.CREATE)
                .argument(StringArgument.of(CLAN_TAG))
                .handler(this::createClan)
        );

        CommandConfirmationManager<CommandSender> deleteConfirmationManager = new CommandConfirmationManager<>(
                1L,
                TimeUnit.MINUTES,
                ctx -> ctx.getCommandContext().getSender().sendMessage(messages.deletion().confirm()),
                sender -> sender.sendMessage(messages.deletion().nothingToConfirm())
        );
        deleteConfirmationManager.registerConfirmationProcessor(manager);

        Command.Builder<CommandSender> delete = builder.literal("delete");

        manager.command(delete
                .senderType(Player.class)
                .meta(ClanPermission.CLAN_PERMISSION, ClanPermission.DELETE_CLAN)
                .meta(CommandConfirmationManager.META_CONFIRMATION_REQUIRED, true)
                .handler(this::deleteClan)
        );
        manager.command(delete.literal("confirm")
                .permission(Permissions.DELETE)
                .meta(ClanPermission.CLAN_PERMISSION, ClanPermission.DELETE_CLAN)
                .handler(deleteConfirmationManager.createConfirmationExecutionHandler())
        );
        manager.command(builder.literal("set").literal("displayname")
                .permission(Permissions.SET_DISPLAY_NAME)
                .senderType(Player.class)
                .meta(ClanPermission.CLAN_PERMISSION, ClanPermission.SET_DISPLAY_NAME)
                .argument(ComponentArgument.greedy(DISPLAY_NAME))
                .handler(this::setDisplayName)
        );
        manager.command(builder.literal("list")
                .permission(Permissions.LIST)
                .handler(this::clanList)
        );


    }
}

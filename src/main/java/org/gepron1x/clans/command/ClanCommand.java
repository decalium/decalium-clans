package org.gepron1x.clans.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.meta.CommandMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.ParseException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.DecaliumClans;
import org.gepron1x.clans.Permissions;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.ClanBuilder;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.clan.member.role.ClanPermission;
import org.gepron1x.clans.config.MessagesConfig;
import org.gepron1x.clans.ClanManager;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.*;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;

public class ClanCommand extends BaseClanCommand {
    private static final String CLAN_TAG = "clan_tag";
    private static final String DISPLAY_NAME = "display_name";
    public static final CommandMeta.Key<Boolean> CLAN_MEMBERS_ONLY = CommandMeta.Key.of(boolean.class, "clan_members_only");
    private final DecaliumClans plugin;


    private final Set<UUID> deletionConfirmations = new HashSet<>();


    public ClanCommand(DecaliumClans plugin, ClanManager manager, MessagesConfig messages) {
        super(manager, messages);
        this.plugin = plugin;
    }

    public void setMessages(MessagesConfig config) {
        this.messages = config;
    }

    public void createClan(CommandContext<CommandSender> ctx) {
        String tag = ctx.get(CLAN_TAG);
        Player player = (Player) ctx.getSender();

        if (manager.getUserClan(player.getUniqueId()) != null) {
            player.sendMessage(messages.alreadyInClan());
            return;
        }
        if (manager.getClan(tag) != null) {
            player.sendMessage(messages.creation().clanWithTagAlreadyExists().withPlaceholder("tag", tag));
            return;
        }
        Component displayName = text(tag, NamedTextColor.GRAY);
        Clan clan = new ClanBuilder(tag).creator(new ClanMember(player, plugin.getOwnerRole()))
                .emptyMembers().emptyStatistics().displayName(displayName).build();
        manager.addClan(clan);
        player.sendMessage(messages.creation().success().withPlaceholder("name", displayName));

    }

    public void deleteClan(CommandContext<CommandSender> ctx) {
        CommandSender sender = ctx.getSender();
        Player player = (Player) sender;
        player.sendMessage(messages.deletion().confirm());
        UUID uuid = player.getUniqueId();
        deletionConfirmations.add(uuid);
        Bukkit.getScheduler().runTaskLater(plugin, () -> deletionConfirmations.remove(uuid), 60 * 20L);

    }

    public void confirmDeletion(CommandContext<CommandSender> ctx) {
        Player player = (Player) ctx.getSender();
        UUID uuid = player.getUniqueId();
        if (!deletionConfirmations.contains(uuid)) {
            player.sendMessage(messages.deletion().nothingToConfirm());
            return;
        }
        Clan clan = manager.getUserClan(player);
        if (clan != null) {
            manager.deleteClan(clan);
            player.sendMessage(messages.deletion().success());
        }
        deletionConfirmations.remove(uuid);
    }

    public void clanList(CommandContext<CommandSender> ctx) {
        CommandSender sender = ctx.getSender();
        manager.getClans().stream().map(clan -> {
            Component members = clan
                    .getMembers().stream()
                    .map(member -> messages.clanList().memberFormat()
                            .withPlaceholder("name", member.asOffline().getName())
                            .withPlaceholder("role", member.getRole().getDisplayName()))
                    .map(ComponentLike::asComponent)
                    .collect(Component.toComponent(newline()));

            return
                    messages.clanList().clanFormat()
                            .stream().map(c ->
                                    c.withPlaceholder("tag", clan.getTag())
                                            .withPlaceholder("display_name", clan.getDisplayName())
                                            .withPlaceholder("members", members))
                            .map(ComponentLike::asComponent)
                            .collect(Component.toComponent(newline()));
        }).forEach(sender::sendMessage);

    }

    public void setDisplayName(CommandContext<CommandSender> ctx) {
        Player player = (Player) ctx.getSender();
        String name = ctx.get(DISPLAY_NAME);
        Clan clan = getClan(player);
        try {
            Component displayName = MiniMessage.get().parse(name);
            clan.setDisplayName(displayName);
            player.sendMessage(messages.displayName().success().withPlaceholder("name", displayName));
        } catch (ParseException e) {
            player.sendMessage(messages.displayName().errorInSyntax());
        }
    }

    public void confirmDropTables(CommandSender sender) {
        if (!sender.equals(Bukkit.getConsoleSender())) {
            sender.sendMessage(TextComponent.ofChildren(text("WARNING!", NamedTextColor.DARK_RED), text("this command can be executed only from console!")));
            sender.sendMessage(text("if you dont understand what are you doing, please dont."));
            sender.sendMessage(text("this command is only for debug purposes."));
            return;
        }
        sender.sendMessage(text("Dropping tables..."));
        plugin.getJdbi().useHandle(handle -> {
            for (String table : Arrays.asList("clans", "members", "stats")) {
                handle.execute(MessageFormat.format("DROP TABLE {0}", table));
            }
        });
        sender.sendMessage(text("Dropped successfully. Please restart your server!"));

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

        manager.command(builder.literal("delete")
                .senderType(Player.class)
                .meta(ClanPermission.CLAN_PERMISSION, ClanPermission.DELETE_CLAN)
                .handler(this::deleteClan)
        );
        manager.command(builder.literal("delete").literal("confirm")
                .permission(Permissions.DELETE)
                .meta(ClanPermission.CLAN_PERMISSION, ClanPermission.DELETE_CLAN)
                .handler(this::confirmDeletion)
        );
        manager.command(builder.literal("set").literal("displayname")
                .permission(Permissions.SET_DISPLAY_NAME)
                .senderType(Player.class)
                .meta(ClanPermission.CLAN_PERMISSION, ClanPermission.SET_DISPLAY_NAME)
                .argument(StringArgument.greedy(DISPLAY_NAME))
                .handler(this::setDisplayName)
        );
        manager.command(builder.literal("list")
                .permission(Permissions.LIST)
                .handler(this::clanList)
        );


    }
}

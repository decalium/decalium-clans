package org.gepron1x.clans.command;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.ParseException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.DecaliumClans;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.ClanBuilder;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.clan.member.role.ClanPermission;
import org.gepron1x.clans.config.MessagesConfig;
import org.gepron1x.clans.ClanManager;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;

@CommandAlias("clan")
public class ClanCommand extends BaseClanCommand {
    private final DecaliumClans plugin;


    private final Set<UUID> deletionConfirmations = new HashSet<>();



    public ClanCommand(DecaliumClans plugin, ClanManager manager, MessagesConfig messages) {
        super(manager, messages);
        this.plugin = plugin;
    }
    public void setMessages(MessagesConfig config) {
        this.messages = config;
    }
    @CommandPermission("clans.create")
    @Subcommand("create")
    public void createClan(Player player, String tag) {

        if(manager.getUserClan(player.getUniqueId()) != null) {
            player.sendMessage(messages.alreadyInClan());
            return;
        }
        if(manager.getClan(tag) != null) {
            player.sendMessage(messages.creation().clanWithTagAlreadyExists().parse("tag", tag));
            return;
        }
        Clan clan = new ClanBuilder(tag).creator(new ClanMember(player, plugin.getOwnerRole()))
                .emptyMembers().emptyStatistics().displayName(text(tag, NamedTextColor.GRAY)).build();
        manager.addClan(clan);
        player.sendMessage(messages.creation().success().parse("name", clan.getDisplayName()));

    }
    @CommandPermission("clans.delete")
    @Subcommand("delete")
    public void deleteClan(Player player) {
        UUID uuid = player.getUniqueId();

        Clan clan = getClanIfPresent(player);
        if(clan == null) return;
        if(!hasPermission(player, clan, ClanPermission.DELETE_CLAN)) return;

        player.sendMessage(messages.deletion().confirm());
        deletionConfirmations.add(uuid);
        Bukkit.getScheduler().runTaskLater(plugin, () -> deletionConfirmations.remove(uuid), 60 * 20L);

    }
    @CommandPermission("clans.delete")
    @Subcommand("delete confirm")
    public void confirmDeletion(Player player) {
        UUID uuid = player.getUniqueId();
        if(!deletionConfirmations.contains(uuid)) {
            player.sendMessage(messages.deletion().nothingToConfirm());
            return;
        }
        Clan clan = getClanIfPresent(player);
        if(clan != null) {
            manager.deleteClan(clan);
            player.sendMessage(messages.deletion().success());
        }
        deletionConfirmations.remove(uuid);
    }
    @CommandPermission("clans.list")
    @Subcommand("list")
    public void clanList(Player player) {
        manager.getClans().stream().map(clan -> {
            Component members = clan
                    .getMembers().stream()
                    .map(member -> messages.clanList().memberFormat()
                            .parse("name", member.asOffline().getName(), "role", member.getRole()))
                    .collect(Component.toComponent(newline()));

            return
                    messages.clanList().clanFormat()
                    .stream().map(c -> c.parse("tag", clan.getTag(),
                            "name", clan.getDisplayName(),
                            "members", members))
                    .collect(Component.toComponent(newline()));
        }).forEach(player::sendMessage);

    }
    @CommandPermission("clans.set.displayname")
    @Subcommand("set displayname")
    public void setDisplayName(Player player, String[] args) {

        Clan clan = getClanIfPresent(player);
        if(clan == null) return;
        if(!hasPermission(player, clan, ClanPermission.SET_DISPLAY_NAME)) return;
        try {
            Component displayName = MiniMessage.get().parse(String.join(" ", args));
            clan.setDisplayName(displayName);
            player.sendMessage(messages.displayName().success().parse("name", displayName));
        } catch (ParseException e) {
            player.sendMessage(messages.displayName().errorInSyntax());
        }
    }
    @Subcommand("drop tables confirm")
    @CommandPermission("decaliumclans.admin.droptables")
    public void confirmDropTables(CommandSender sender) {
        if(!sender.equals(Bukkit.getConsoleSender())) {
            sender.sendMessage(TextComponent.ofChildren(text("WARNING!", NamedTextColor.DARK_RED), text("this command can be executed only from console!")));
            sender.sendMessage(text("if you dont understand what are you doing, please dont."));
            sender.sendMessage(text("this command is only for debug purposes."));
            return;
        }
        sender.sendMessage(text("Dropping tables..."));
            plugin.getJdbi().useHandle(handle -> {
                for(String table : Arrays.asList("clans", "members", "stats")) {
                    handle.execute(MessageFormat.format("DROP TABLE {0}", table));
                }
            });
            sender.sendMessage(text("Dropped successfully. Please restart your server!"));

    }


}

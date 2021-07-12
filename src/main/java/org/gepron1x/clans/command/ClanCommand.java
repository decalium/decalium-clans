package org.gepron1x.clans.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.gepron1x.clans.DecaliumClans;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.config.MessagesConfig;
import org.gepron1x.clans.helper.ClanHelper;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.role.ClanPermission;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static net.kyori.adventure.text.Component.*;

@CommandAlias("clan")
public class ClanCommand extends BaseCommand {
    private final DecaliumClans plugin;
    private final MiniMessage mm;


    private final Set<UUID> deletionConfirmations = new HashSet<>();

    private final MessagesConfig messages;

    public ClanCommand(DecaliumClans plugin, MessagesConfig messages) {
        this.plugin = plugin;
        this.mm = plugin.getMiniMessage();

        this.messages = messages;
    }

    @Subcommand("create")
    public void createClan(Player player, String tag) {
        ClanHelper helper = plugin.getClanHelper();
        if(helper.getUserClan(player.getUniqueId()) != null) {
            player.sendMessage(mm.parse(messages.alreadyInClan()));
            return;
        }
        if(helper.getClan(tag) != null) {
            player.sendMessage(mm.parse(messages.creation().clanWithTagAlreadyExists()));
            return;
        }
        Clan clan = new Clan(tag, player.getUniqueId(), text(tag).color(NamedTextColor.GRAY));
        helper.createClan(clan);
        helper.addMember(clan, new ClanMember(player, plugin.getOwnerRole()));
        player.sendMessage(mm.parse(messages.creation().success(), Template.of("clan", clan.getDisplayName())));

    }
    @Subcommand("delete")
    public void deleteClan(Player player) {
        ClanHelper helper = plugin.getClanHelper();
        UUID uuid = player.getUniqueId();
        Clan clan = helper.getUserClan(uuid);
        if(clan == null) {
            player.sendMessage(mm.parse(messages.notInClan()));
            return;
        }
        if(!clan.getMemberList().getMember(uuid).getRole().hasPermission(ClanPermission.DELETE_CLAN)) {
            player.sendMessage(mm.parse(messages.noClanPermission()));
            return;
        }
        player.sendMessage(mm.parse(messages.deletion().confirm()));

        deletionConfirmations.add(uuid);
        Bukkit.getScheduler().runTaskLater(plugin, () -> deletionConfirmations.remove(uuid), 60 * 20L);

    }
    @Subcommand("delete confirm")
    public void confirmDeletion(Player player) {
        ClanHelper helper = plugin.getClanHelper();
        UUID uuid = player.getUniqueId();
        if(!deletionConfirmations.contains(uuid)) {
            player.sendMessage(mm.parse(messages.deletion().nothingToConfirm()));
            return;
        }
        Clan clan = helper.getUserClan(uuid);
        if(clan != null) {
            helper.removeClan(clan);
            player.sendMessage(mm.parse(messages.deletion().success()));
        } else {
            player.sendMessage(mm.parse(messages.notInClan()));
        }
        deletionConfirmations.remove(uuid);
    }

    @Subcommand("list")
    public void clanList(Player player) {
        plugin.getClanHelper().getClans().stream().map(clan -> {
            Component members = clan.getMemberList()
                    .getMembers().stream()
                    .map(member -> mm.parse(messages.clanList().memberFormat(), Template.of("name", member.getName()),
                            Template.of("role", member.getRole().getDisplayName())
                    )).collect(Component.toComponent(newline()));

            return
                    messages.clanList().clanFormat()
                    .stream().map(c -> mm.parse(c, Template.of("clan_tag", clan.getTag()),
                    Template.of("clan_name", clan.getDisplayName()),
                    Template.of("members", members)))
                    .collect(Component.toComponent(newline()));
        }).forEach(player::sendMessage);

    }

    @Subcommand("set displayname")
    public void setDisplayName(Player player, String[] args) {
        ClanHelper helper = plugin.getClanHelper();
        Clan clan = helper.getUserClan(player.getUniqueId());
        if(clan == null) {
            player.sendMessage(mm.parse(messages.notInClan()));
            return;
        }
        if(!clan.getMemberList().getMember(player).hasPermission(ClanPermission.SET_DISPLAY_NAME)) {
            player.sendMessage(mm.parse(messages.noClanPermission()));
        }
        try {
            Component displayName = MiniMessage.get().parse(String.join(" ", args));
            helper.setClanDisplayName(clan, displayName);
            player.sendMessage(mm.parse(messages.displayName().success(), "name", displayName));
        } catch (Exception e) {
            player.sendMessage(mm.parse(messages.displayName().errorInSyntax()));
        }
    }
    @Subcommand("drop tables")
    @CommandPermission("decaliumclans.admin.droptables")
    public void dropTables(CommandSender sender) {
        sender.sendMessage(text("dropping tables...."));
        sender.sendMessage(text("please note that this would reqiure restart! clans wouldnt work anymore!"));
        plugin.getScheduler().async(task -> {
            plugin.getJdbi().useHandle(handle -> {
                for(String table : Arrays.asList("clans", "members", "stats")) handle.execute("DELETE FROM " + table);
            });
            sender.sendMessage(text("success!"));
        });

    }


}

package com.manya.clans.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.manya.clans.DecaliumClans;
import com.manya.clans.clan.member.ClanMember;
import com.manya.clans.config.MessagesConfig;
import com.manya.clans.helper.ClanHelper;
import com.manya.clans.clan.Clan;
import com.manya.clans.clan.role.ClanPermission;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static net.kyori.adventure.text.Component.*;

@CommandAlias("clan")
public class ClanCommand extends BaseCommand {
    private final DecaliumClans plugin;
    private final MiniMessage mm;


    private final Set<UUID> deletionConfirmations = new HashSet<>();
    private final ClanHelper helper;
    private final MessagesConfig messages;

    public ClanCommand(DecaliumClans plugin, ClanHelper helper, MessagesConfig messages) {
        this.plugin = plugin;
        this.mm = plugin.getMiniMessage();

        this.helper = helper;
        this.messages = messages;
    }

    @Subcommand("create")
    public void createClan(Player player, String tag) {
        if(helper.getUserClan(player.getUniqueId()) != null) {
            player.sendMessage(mm.parse(messages.alreadyInClan()));
            return;
        }
        if(helper.getClan(tag) != null) {
            player.sendMessage(mm.parse(messages.creation().clanWithTagAlreadyExists()));
            return;
        }
        Clan clan = new Clan(tag, player.getUniqueId(), text(tag).color(NamedTextColor.GRAY));
        helper.addClan(clan);
        helper.addMember(clan, new ClanMember(player, plugin.getOwnerRole()));
        player.sendMessage(mm.parse(messages.creation().success(), Template.of("clan", clan.getDisplayName())));

    }
    @Subcommand("delete")
    public void deleteClan(Player player) {
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
        helper.getClans().stream().map(clan -> {
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
        Clan clan = helper.getUserClan(player.getUniqueId());
        if(clan == null) {
            player.sendMessage(mm.parse(messages.notInClan()));
            return;
        }
        if(!clan.getMemberList().getMember(player).hasPermission(ClanPermission.SET_DISPLAY_NAME)) {
            player.sendMessage(mm.parse(messages.noClanPermission()));
        }

        Component displayName = mm.parse(String.join(" ", args));
        helper.setClanDisplayName(clan, displayName);

    }


}

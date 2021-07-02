package com.manya.clans.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.manya.clans.DecaliumClans;
import com.manya.clans.clan.member.ClanMember;
import com.manya.clans.config.MessagesConfig;
import com.manya.clans.manager.InviteManager;
import com.manya.clans.storage.ClanDao;
import com.manya.clans.clan.Clan;
import com.manya.clans.clan.role.ClanPermission;
import com.manya.clans.manager.ClanManager;
import com.manya.clans.storage.ClanMemberDao;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jdbi.v3.core.Jdbi;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static net.kyori.adventure.text.Component.*;

@CommandAlias("clan")
public class ClanCommand extends BaseCommand {
    private final DecaliumClans plugin;
    private final MiniMessage mm;
    private final ClanManager clanManager;
    private final Set<UUID> deletionConfirmations = new HashSet<>();
    private final InviteManager inviteManager;
    private final MessagesConfig messages;

    public ClanCommand(DecaliumClans plugin, ClanManager clanManager, MessagesConfig messages) {
        this.plugin = plugin;
        this.mm = plugin.getMiniMessage();
        this.clanManager = clanManager;
        this.inviteManager = new InviteManager(plugin, clanManager, messages);
        this.messages = messages;
    }

    @Subcommand("create")
    public void createClan(Player player, String tag) {
        if(clanManager.getUserClan(player.getUniqueId()) != null) {
            player.sendMessage(mm.parse(messages.alreadyInClan()));
            return;
        }
        if(clanManager.getClan(tag) != null) {
            player.sendMessage(mm.parse(messages.creation().clanWithTagAlreadyExists()));
            return;
        }
        Clan clan = new Clan(tag, player.getUniqueId(), text(tag).color(NamedTextColor.GRAY));
        clanManager.addClan(clan);
        clan.getMemberList().addMember(new ClanMember(player, plugin.getOwnerRole()));
        final Jdbi jdbi = plugin.getJdbi();
        plugin.getScheduler().async(task -> jdbi.withExtension(ClanDao.class, dao -> {
            dao.insertOrUpdateClan(clan);
            return null;
        }));
        player.sendMessage(mm.parse(messages.creation().success(), Template.of("clan", clan.getDisplayName())));

    }
    @Subcommand("delete")
    public void deleteClan(Player player) {
        UUID uuid = player.getUniqueId();
        Clan clan = clanManager.getUserClan(uuid);
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
        Clan clan = clanManager.getUserClan(uuid);
        if(clan != null) {
            clanManager.removeClan(clan);
            final Jdbi jdbi = plugin.getJdbi();
            plugin.getScheduler().async(task -> {
                jdbi.withExtension(ClanDao.class, dao -> {
                    dao.removeClan(clan);
                    return null;
                });
                jdbi.withExtension(ClanMemberDao.class, dao -> {
                    dao.clearMembers(clan);
                    return null;
                });
            });

            player.sendMessage(mm.parse(messages.deletion().success()));
        } else {

        }
        deletionConfirmations.remove(uuid);
    }
    @Subcommand("invite")
    public void invitePlayer(Player player, OnlinePlayer receiver) {
        inviteManager.createInvitation(player, receiver.getPlayer());
    }
    @Subcommand("invite accept")
    public void acceptInvite(Player player, String senderName) {
        inviteManager.acceptInvite(player, senderName);
    }
    @Subcommand("invite deny")
    public void denyInvite(Player player, String senderName) {
        inviteManager.denyInvite(player, senderName);
    }
    @Subcommand("list")
    public void clanList(Player player) {

        clanManager.getClans().stream().map(clan -> {
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
        Clan clan = clanManager.getUserClan(player.getUniqueId());
        if(clan == null) {
            player.sendMessage(mm.parse(messages.notInClan()));
            return;
        }
        if(!clan.getMemberList().getMember(player).hasPermission(ClanPermission.SET_DISPLAY_NAME)) {
            player.sendMessage(mm.parse(messages.noClanPermission()));
        }
        Component displayName = mm.parse(String.join(" ", args));
        clan.setDisplayName(displayName);
        plugin.getScheduler().async(task -> {
            plugin.getJdbi().withExtension(ClanDao.class, dao -> {
                dao.setDisplayName(clan, displayName);
                return null;
            });
        });


    }


}

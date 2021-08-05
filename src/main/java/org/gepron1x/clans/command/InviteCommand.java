package org.gepron1x.clans.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.gepron1x.clans.DecaliumClans;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.clan.member.role.ClanPermission;
import org.gepron1x.clans.config.MessagesConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.entity.Player;
import org.gepron1x.clans.manager.ClanManager;

import java.util.UUID;


@CommandAlias("clan")
@Subcommand("invite")
public final class InviteCommand extends BaseCommand {
    private final DecaliumClans plugin;
    private final ClanManager manager;
    private MessagesConfig messages;
    private final Table<UUID, String, Clan> clanInvitations = HashBasedTable.create();

    public InviteCommand(DecaliumClans plugin, ClanManager manager, MessagesConfig messages) {
        this.plugin = plugin;
        this.manager = manager;
        this.messages = messages;
    }
    public void setMessages(MessagesConfig messages) {
        this.messages = messages;
    }
    @
    @Default
    public void createInvitation(Player sender, OnlinePlayer onlineReceiver) {
        Player receiver = onlineReceiver.getPlayer();
        UUID senderUniqueId = sender.getUniqueId();
        Clan clan = manager.getUserClan(senderUniqueId);
        if(clan == null) {
            sender.sendMessage(messages.notInClan());
            return;
        }

        if(!clan.getMember(senderUniqueId).hasPermission(ClanPermission.INVITE_MEMBER)) {
            sender.sendMessage(messages.noClanPermission());
            return;
        }
        if(receiver.getUniqueId().equals(senderUniqueId)) {
            sender.sendMessage(messages.invite().cannotInviteSelf());
            return;
        }
        if(manager.getUserClan(receiver.getUniqueId()) != null) {
            sender.sendMessage(messages.alreadyInClan().parse("name", receiver.displayName()));
            return;
        }
        String senderName = sender.getName();
        String command = "/clan invite accept " + senderName;
        sender.sendMessage(messages.invite().invitationMessage()
                .parse("sender", sender.displayName(), "clan", clan.getDisplayName()));

        clanInvitations.put(receiver.getUniqueId(), senderName, clan);
        sender.sendMessage(messages.invite().invitationSent().parse("receiver", receiver.displayName()));

    }
    @Subcommand("accept")
    public void acceptInvite(Player receiver, String senderName) {
        UUID receiverUniqueId = receiver.getUniqueId();
        Clan clan = clanInvitations.get(receiverUniqueId, senderName);
        if(check(receiver, senderName)) return;
        clan.addMember(new ClanMember(receiver, plugin.getDefaultRole()));
        receiver.sendMessage(messages.invite().accepted().parse("sender", senderName, "clan", clan.getDisplayName()));



    }
    @Subcommand("deny")
    public void denyInvite(Player receiver, String senderName) {
        if(check(receiver, senderName)) return;
        receiver.sendMessage(messages.invite().denied().parse("sender", senderName));
        clanInvitations.remove(receiver.getUniqueId(), senderName);

    }
    private boolean check(Player receiver, String senderName) {
        UUID receiverUniqueId = receiver.getUniqueId();
        Clan clan = clanInvitations.get(receiverUniqueId, senderName);

        if(clan == null) {
            receiver.sendMessage(messages.invite().noInvitesFromThisPlayer().parse("sender", senderName));
            return true;
        }
        if(!manager.getClans().contains(clan)) {
            receiver.sendMessage(messages.invite().clanGotDeleted());
            return true;
        }
        return false;
    }
}

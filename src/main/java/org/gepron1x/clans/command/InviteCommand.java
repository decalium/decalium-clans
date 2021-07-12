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
import org.gepron1x.clans.clan.role.ClanPermission;
import org.gepron1x.clans.config.MessagesConfig;
import org.gepron1x.clans.helper.ClanHelper;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.entity.Player;

import java.util.UUID;


@CommandAlias("clan")
@Subcommand("invite")
public final class InviteCommand extends BaseCommand {
    private final DecaliumClans plugin;

    private final MiniMessage mm;
    private final MessagesConfig messages;
    private final Table<UUID, String, Clan> clanInvitations = HashBasedTable.create();

    public InviteCommand(DecaliumClans plugin, MessagesConfig messages) {
        this.plugin = plugin;
        this.mm = plugin.getMiniMessage();
        this.messages = messages;
    }
    @Default
    public void createInvitation(Player sender, OnlinePlayer onlineReceiver) {
        ClanHelper helper = plugin.getClanHelper();
        Player receiver = onlineReceiver.getPlayer();
        UUID senderUniqueId = sender.getUniqueId();
        Clan clan = helper.getUserClan(senderUniqueId);
        if(clan == null) {
            sender.sendMessage(mm.parse(messages.notInClan()));
            return;
        }
        if(!clan.getMemberList().getMember(senderUniqueId).hasPermission(ClanPermission.INVITE_MEMBER)) {
            sender.sendMessage(mm.parse(messages.noClanPermission()));
            return;
        }
        if(receiver.getUniqueId().equals(senderUniqueId)) {
            sender.sendMessage(mm.parse(messages.invite().cannotInviteSelf()));
            return;
        }
        if(helper.getUserClan(receiver.getUniqueId()) != null) {
            sender.sendMessage(mm.parse(messages.alreadyInClan(), Template.of("player", receiver.displayName())));
            return;
        }
        String senderName = sender.getName();
        String command = "/clan invite accept " + senderName;
        sender.sendMessage(mm.parse(messages.invite().invitationMessage(), Template.of("sender", sender.displayName()),
                Template.of("clan", clan.getDisplayName()),
                Template.of("command", command)));

        clanInvitations.put(receiver.getUniqueId(), senderName, clan);
        sender.sendMessage(mm.parse(messages.invite().invitationSent(), Template.of("receiver", receiver.displayName())));

    }
    @Subcommand("accept")
    public void acceptInvite(Player receiver, String senderName) {
        ClanHelper helper = plugin.getClanHelper();
        UUID receiverUniqueId = receiver.getUniqueId();
        Clan clan = clanInvitations.get(receiverUniqueId, senderName);
        if(check(receiver, senderName)) return;
        helper.addMember(clan, new ClanMember(receiver, plugin.getDefaultRole()));
        receiver.sendMessage( mm.parse(messages.invite().accepted()) );



    }
    @Subcommand("deny")
    public void denyInvite(Player receiver, String senderName) {
        if(check(receiver, senderName)) return;
        receiver.sendMessage(mm.parse(messages.invite().denied()));
        clanInvitations.remove(receiver.getUniqueId(), senderName);

    }
    private boolean check(Player receiver, String senderName) {
        ClanHelper helper = plugin.getClanHelper();
        UUID receiverUniqueId = receiver.getUniqueId();
        Clan clan = clanInvitations.get(receiverUniqueId, senderName);

        if(clan == null) {
            receiver.sendMessage(mm.parse(messages.invite().noInvitesFromThisPlayer()));
            return true;
        }
        if(!helper.getClans().contains(clan)) {
            receiver.sendMessage(mm.parse(messages.invite().clanGotDeleted()));
            return true;
        }
        return false;
    }
}

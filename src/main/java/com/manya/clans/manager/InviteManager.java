package com.manya.clans.manager;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.manya.clans.DecaliumClans;
import com.manya.clans.clan.Clan;
import com.manya.clans.clan.member.ClanMember;
import com.manya.clans.clan.role.ClanPermission;
import com.manya.clans.config.MessagesConfig;
import com.manya.clans.storage.ClanMemberDao;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.entity.Player;

import java.util.UUID;



public final class InviteManager {
    private final DecaliumClans plugin;
    private final ClanManager manager;
    private final MiniMessage mm;
    private final MessagesConfig messages;
    private final Table<UUID, String, Clan> clanInvitations = HashBasedTable.create();

    public InviteManager(DecaliumClans plugin, ClanManager manager, MessagesConfig messages) {
        this.plugin = plugin;
        this.mm = plugin.getMiniMessage();
        this.manager = manager;
        this.messages = messages;
    }
    public void createInvitation(Player sender, Player receiver) {

        UUID senderUniqueId = sender.getUniqueId();
        Clan clan = manager.getUserClan(senderUniqueId);
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
        }
        if(manager.getUserClan(receiver.getUniqueId()) != null) {
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
    public void acceptInvite(Player receiver, String senderName) {
        UUID receiverUniqueId = receiver.getUniqueId();
        Clan clan = clanInvitations.get(receiverUniqueId, senderName);
        if(!check(receiver, senderName)) return;
        manager.setPlayerClan(receiver, clan);
        ClanMember member = new ClanMember(receiver, plugin.getDefaultRole());
        clan.getMemberList().addMember(member);
        plugin.getScheduler().async(task -> plugin.getJdbi().withExtension(ClanMemberDao.class, dao -> {
            dao.addMember(member, clan);
            return null;
        }));
        receiver.sendMessage(mm.parse(messages.invite().accepted()));


    }
    public void denyInvite(Player receiver, String senderName) {
        if(!check(receiver, senderName)) return;
        receiver.sendMessage(mm.parse(messages.invite().denied()));
        clanInvitations.remove(receiver.getUniqueId(), senderName);

    }
    private boolean check(Player receiver, String senderName) {
        UUID receiverUniqueId = receiver.getUniqueId();
        Clan clan = clanInvitations.get(receiverUniqueId, senderName);

        if(clan == null) {
            receiver.sendMessage(mm.parse(messages.invite().noInvitesFromThisPlayer()));
            return false;
        }
        if(!manager.getClans().contains(clan)) {
            receiver.sendMessage(mm.parse(messages.invite().clanGotDeleted()));
            return false;
        }
        return true;
    }
}

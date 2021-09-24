package org.gepron1x.clans.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.context.CommandContext;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.DecaliumClans;
import org.gepron1x.clans.Permissions;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.clan.member.role.ClanPermission;
import org.gepron1x.clans.config.MessagesConfig;
import org.gepron1x.clans.ClanManager;
import org.gepron1x.clans.util.registry.ClanRoleRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;



public final class InviteCommand extends BaseClanCommand {
    private static final String RECEIVER = "receiver";
    private static final String SENDER = "sender";
    private final DecaliumClans plugin;
    private final ClanRoleRegistry roles;
    private final Table<UUID, String, Clan> clanInvitations = HashBasedTable.create();

    public InviteCommand(DecaliumClans plugin, ClanManager manager, MessagesConfig messages, ClanRoleRegistry roles) {
        super(manager, messages);
        this.plugin = plugin;
        this.roles = roles;
    }
    public void setMessages(MessagesConfig messages) {
        this.messages = messages;
    }

    private void createInvitation(CommandContext<CommandSender> ctx) {
        Player receiver = ctx.get(RECEIVER);
        Player sender = (Player) ctx.getSender();
        Template receiverTemplate = Template.of("receiver", receiver.displayName());
        UUID senderUniqueId = sender.getUniqueId();
        Clan clan = getClan(senderUniqueId);


        if(receiver.getUniqueId().equals(senderUniqueId)) {
            sender.sendMessage(messages.invite().cannotInviteSelf());
            return;
        }
        if(clanManager.getUserClan(receiver.getUniqueId()) != null) {
            sender.sendMessage(messages.alreadyInClan().with(receiverTemplate));
            return;
        }
        String senderName = sender.getName();

        sender.sendMessage(messages.invite().invitationMessage()
                .with("sender", sender.displayName())
                .with("clan", clan.getDisplayName())
        );

        clanInvitations.put(receiver.getUniqueId(), senderName, clan);
        sender.sendMessage(messages.invite().invitationSent().with(receiverTemplate));

    }

    private void acceptInvite(CommandContext<CommandSender> ctx) {
        Player receiver = (Player) ctx.getSender();
        String senderName = ctx.get(SENDER);
        UUID receiverUniqueId = receiver.getUniqueId();
        Clan clan = clanInvitations.get(receiverUniqueId, senderName);
        if(checkInvites(receiver, senderName)) return;
        clan.addMember(new ClanMember(receiver, roles.getDefaultRole()));

        receiver.sendMessage(messages.invite().accepted()
                .with("sender", senderName)
                .with("clan", clan.getDisplayName())
        );



    }

    private void denyInvite(CommandContext<CommandSender> ctx) {
        Player receiver = (Player) ctx.getSender();
        String senderName = ctx.get(SENDER);

        if(checkInvites(receiver, senderName)) return;
        receiver.sendMessage(messages.invite().denied().with("sender", senderName));
        clanInvitations.remove(receiver.getUniqueId(), senderName);

    }
    private boolean checkInvites(Player receiver, String senderName) {
        UUID receiverUniqueId = receiver.getUniqueId();
        Clan clan = clanInvitations.get(receiverUniqueId, senderName);

        if(clan == null) {
            receiver.sendMessage(messages.invite().noInvitesFromThisPlayer().with("receiver", receiver.displayName()));
            return true;
        }
        if(!clanManager.getClans().contains(clan)) {
            receiver.sendMessage(messages.invite().clanGotDeleted());
            return true;
        }
        return false;
    }

    @Override
    public void register(@NotNull CommandManager<CommandSender> manager) {
        Command.Builder<CommandSender> invite = manager.commandBuilder("clan").literal("invite");

        manager.command(invite
                .senderType(Player.class)
                .permission(Permissions.INVITE)
                .meta(ClanPermission.CLAN_PERMISSION, ClanPermission.INVITE)
                .argument(PlayerArgument.of(RECEIVER))
                .handler(this::createInvitation)
        );

        manager.command(invite.literal("accept")
                .senderType(Player.class)
                .permission(Permissions.INVITE)
                .argument(StringArgument.of(SENDER))
                .handler(this::acceptInvite)
        );

        manager.command(invite.literal("deny")
                .senderType(Player.class)
                .permission(Permissions.INVITE)
                .argument(StringArgument.of(SENDER))
                .handler(this::denyInvite)
        );

    }
}

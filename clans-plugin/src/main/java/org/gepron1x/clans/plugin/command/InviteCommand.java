package org.gepron1x.clans.plugin.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.arguments.selector.SinglePlayerSelector;
import cloud.commandframework.bukkit.parsers.selector.SinglePlayerSelectorArgument;
import cloud.commandframework.context.CommandContext;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.ClanManager;
import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.text;

public class InviteCommand extends AbstractCommand {


    private final FactoryOfTheFuture futuresFactory;
    private final ClanManager manager;
    private final ClanBuilderFactory builderFactory;
    private final RoleRegistry roleRegistry;
    private ClansConfig config;
    private MessagesConfig messages;

    private record Invitation(@NotNull UUID sender, @NotNull UUID receiver) {

    }

    private final Table<UUID, String, Invitation> invitations = HashBasedTable.create();


    public InviteCommand(FactoryOfTheFuture futuresFactory, ClanManager manager,
                         ClanBuilderFactory builderFactory, RoleRegistry roleRegistry,
                         ClansConfig config, MessagesConfig messages) {
        this.futuresFactory = futuresFactory;
        this.manager = manager;
        this.builderFactory = builderFactory;
        this.roleRegistry = roleRegistry;
        this.config = config;
        this.messages = messages;
    }

    @Override
    public void register(CommandManager<CommandSender> manager) {
        Command.Builder<CommandSender> builder = manager.commandBuilder("clan");

        manager.command(builder.literal("invite")
                .permission("clans.invite")
                .argument(SinglePlayerSelectorArgument.of("receiver"))
                .handler(this::invite)
        );

        manager.command(builder.literal("accept")
                .permission("clans.invite.accept")
                .argument(StringArgument.of("sender_name"))
                .handler(this::acceptInvite)
        );

        manager.command(builder.literal("decline")
                .permission("clans.invite.decline")
                .argument(StringArgument.of("sender_name"))
                .handler(this::declineInvite)
        );

    }


    private void invite(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        Player receiver = context.<SinglePlayerSelector>get("receiver").getPlayer();
        if (receiver == null) {
            player.sendMessage(text("Lol"));
            return;
        }

        CentralisedFuture<Clan> first = this.manager.getUserClan(player.getUniqueId());
        CentralisedFuture<Clan> second = this.manager.getUserClan(receiver.getUniqueId());

        futuresFactory.allOf(first, second).thenAcceptSync(ignored -> {
            Clan clan = first.join();
            Clan receiverClan = second.join();

            if (clan == null) {
                player.sendMessage(messages.notInTheClan());
                return;
            }
            if (!requireNonNull(clan.getMember(player)).hasPermission(ClanPermission.INVITE)) {
                player.sendMessage(messages.noClanPermission());
                return;
            }

            if (receiverClan != null) {
                player.sendMessage(messages.playerIsAlreadyInClan().with("player", receiver.displayName()));
                return;
            }
            invitations.put(receiver.getUniqueId(), player.getName(),
                    new Invitation(player.getUniqueId(), receiver.getUniqueId()));
            player.sendMessage(messages.commands().invitation().invitationSent().with("receiver", receiver.displayName()));
        });

    }

    private void acceptInvite(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        String name = context.get("sender_name");
        Invitation invitation = checkInvitation(player, name);
        if (invitation == null) return;

        ClanMember member = builderFactory.memberBuilder().uuid(invitation.receiver()).role(roleRegistry.getDefaultRole()).build();
        Player senderPlayer = player.getServer().getPlayer(invitation.sender());
        this.manager.getUserClan(invitation.sender())
                .thenComposeSync(clan -> {
                    if (clan == null) {
                        player.sendMessage(messages.commands().invitation().clanGotDeleted());
                        return futuresFactory.completedFuture(false);
                    }
                    return this.manager.editClan(clan, clanEditor -> clanEditor.addMember(member)).thenApply(c -> true);
                }).thenAcceptSync(bool -> {
                    if (!bool) return;
                        if (senderPlayer != null) {
                            senderPlayer.sendMessage(messages.commands().invitation().playerAccepted().with("receiver", player.displayName()));
                        }
                });
    }

    private void declineInvite(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        String name = context.get("sender_name");
        Invitation invitation = checkInvitation(player, name);
        if (invitation == null) return;

        Player senderPlayer = player.getServer().getPlayer(invitation.sender());

        player.sendMessage(messages.commands().invitation().declined());
        if (senderPlayer != null) {
            senderPlayer.sendMessage(messages.commands().invitation().playerDeclined().with("receiver", player.displayName()));
        }

        invitations.remove(player.getUniqueId(), name);

    }

    @Nullable
    private Invitation checkInvitation(Player player, String name) {
        Invitation invitation = invitations.get(player.getUniqueId(), name);
        if (invitation == null) {
            player.sendMessage(messages.commands().invitation().noInvitations().with("player", name));
        }
        return invitation;
    }

}

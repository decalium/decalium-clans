package org.gepron1x.clans.plugin.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.arguments.selector.SinglePlayerSelector;
import cloud.commandframework.bukkit.parsers.selector.SinglePlayerSelectorArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.permission.Permission;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;

public class InviteCommand extends AbstractClanCommand {

    private final ClanBuilderFactory builderFactory;
    private final RoleRegistry roleRegistry;

    private record Invitation(@NotNull UUID sender, @NotNull UUID receiver) {}

    private final Table<UUID, String, Invitation> invitations = HashBasedTable.create();


    public InviteCommand(@NotNull Logger logger, @NotNull CachingClanRepository clanManager,
                         @NotNull ClansConfig config,
                         @NotNull MessagesConfig messages,
                         @NotNull FactoryOfTheFuture futuresFactory,
                         @NotNull ClanBuilderFactory builderFactory,
                         @NotNull RoleRegistry roleRegistry)
    {
        super(logger, clanManager, config, messages, futuresFactory);
        this.builderFactory = builderFactory;
        this.roleRegistry = roleRegistry;
    }

    @Override
    public void register(CommandManager<CommandSender> manager) {
        Command.Builder<CommandSender> builder = manager.commandBuilder("clan").senderType(Player.class);


        manager.command(builder.literal("invite")
                .permission(Permission.of("clans.invite"))
                .argument(SinglePlayerSelectorArgument.of("receiver"))
                .handler(
                        clanExecutionHandler(
                                new PermissiveClanExecutionHandler(this::invite, ClanPermission.INVITE, this.messages)
                        )
                )
        );

        manager.command(builder.literal("accept")
                .permission(Permission.of("clans.invite.accept"))
                .argument(StringArgument.<CommandSender>newBuilder("sender_name")
                        .withSuggestionsProvider(this::invitationCompletion))
                .handler(this::acceptInvite)
        );

        manager.command(builder.literal("decline")
                .permission(Permission.of("clans.invite.decline"))
                .argument(StringArgument.<CommandSender>newBuilder("sender_name").withSuggestionsProvider(this::invitationCompletion))
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

        Clan clan = context.get(ClanExecutionHandler.CLAN);

        this.clanRepository.requestUserClan(receiver.getUniqueId()).thenAcceptSync(opt -> {


            if (opt.isPresent()) {
                player.sendMessage(messages.playerIsAlreadyInClan().with("player", receiver.displayName()));
                return;
            }
            invitations.put(receiver.getUniqueId(), player.getName(),
                    new Invitation(player.getUniqueId(), receiver.getUniqueId()));
            player.sendMessage(messages.commands().invitation().invitationSent().with("receiver", receiver.displayName()));

            receiver.sendMessage(messages.commands().invitation().invitationMessage()
                    .withMiniMessage("sender", player.getName())
                    .with("clan_display_name", clan.displayName())
            );
        }).exceptionally(this::exceptionHandler);

    }

    private void acceptInvite(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        String name = context.get("sender_name");
        Invitation invitation = checkInvitation(player, name);
        if (invitation == null) return;
        invitations.remove(player.getUniqueId(), name);
        ClanMember member = builderFactory.memberBuilder().uuid(invitation.receiver()).role(roleRegistry.defaultRole()).build();
        Player senderPlayer = player.getServer().getPlayer(invitation.sender());
        this.clanRepository.requestUserClan(invitation.sender())
                .thenComposeSync(clan -> {
                    if (clan.isEmpty()) {
                        player.sendMessage(messages.commands().invitation().clanGotDeleted());
                        return futuresFactory.completedFuture(false);
                    }
                    return clan.get().edit(edition -> edition.addMember(member)).thenApply(c -> true);
                }).thenAcceptSync(bool -> {
                    if (!bool) return;
                    if (senderPlayer != null) {
                        senderPlayer.sendMessage(messages.commands().invitation().playerAccepted().with("receiver", player.displayName()));
                    }
                }).exceptionally(this::exceptionHandler);
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

    private List<String> invitationCompletion(CommandContext<CommandSender> ctx, String s) {
        if(!(ctx.getSender() instanceof Player player)) return Collections.emptyList();
        UUID uuid = player.getUniqueId();
        return this.clanRepository.userClanIfCached(uuid).map(Clan::members)
                .map(members ->
                        members.stream()
                                .map(m -> m.asPlayer(player.getServer()))
                                .filter(Objects::nonNull)
                                .map(Player::getName)
                                .collect(Collectors.toList())
                )
                .orElse(Collections.emptyList());

    }

}

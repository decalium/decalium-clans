package org.gepron1x.clans.plugin.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.bukkit.parsers.OfflinePlayerArgument;
import cloud.commandframework.context.CommandContext;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class MemberCommand extends AbstractClanCommand {


    public MemberCommand(@NotNull Logger logger, CachingClanRepository clanManager,
                         @NotNull ClansConfig config,
                         @NotNull MessagesConfig messages, @NotNull FactoryOfTheFuture futuresFactory) {
        super(logger, clanManager, config, messages, futuresFactory);
    }

    @Override
    public void register(CommandManager<CommandSender> manager) {

        Command.Builder<CommandSender> builder = manager.commandBuilder("clan").literal("member").senderType(Player.class);
        manager.command(builder
                .literal("set")
                .literal("role")
                .permission("clans.member.set.role")
                .argument(OfflinePlayerArgument.<CommandSender>newBuilder("member").withSuggestionsProvider(this::memberCompletion))
                .argument(manager.argumentBuilder(ClanRole.class, "role"))
                .handler(new ClanExecutionHandler(
                        new PermissiveClanExecutionHandler(
                                this::setRole, ClanPermission.SET_ROLE, this.messages),
                        this.clanRepository, this.messages)
                )
        );

        manager.command(builder.literal("kick")
                .permission("clans.member.kick")
                .argument(OfflinePlayerArgument.<CommandSender>newBuilder("member").withSuggestionsProvider(this::memberCompletion))
                .handler(new ClanExecutionHandler(
                        new PermissiveClanExecutionHandler(this::kickMember, ClanPermission.SET_ROLE, this.messages),
                        this.clanRepository, this.messages)
                )
        );

    }


    private void setRole(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        OfflinePlayer memberPlayer = context.get("member");
        ClanRole role = context.get("role");
        Clan clan = context.get(ClanExecutionHandler.CLAN);
        ClanMember member = context.get(ClanExecutionHandler.CLAN_MEMBER);

        Optional<ClanMember> opt = clan.member(memberPlayer.getUniqueId());
        if (opt.isEmpty()) {
            player.sendMessage(messages.commands().member().notAMember()
                    .with("player", memberPlayer.getName()));
            return;
        }
        ClanMember other = opt.get();

        if(other.role().weight() > member.role().weight()) {
            player.sendMessage(messages.commands().member().memberHasHigherWeight().with("member", memberPlayer.getName()));
            return;
        }

        if(member.role().weight() <= role.weight()) {
            player.sendMessage(messages.commands().member().role().roleHasHigherWeight().with("role", role.displayName()));
            return;
        }

        clan.edit(edition -> edition.editMember(other.uniqueId(), memberEdition -> memberEdition.appoint(role)))
                .thenAccept(c -> player.sendMessage(messages.commands().member().role().success()))
                .exceptionally(this::exceptionHandler);

    }

    private void kickMember(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        OfflinePlayer memberPlayer = context.get("member");

        Clan clan = context.get(ClanExecutionHandler.CLAN);
        ClanMember member = context.get(ClanExecutionHandler.CLAN_MEMBER);

        Optional<ClanMember> opt = clan.member(memberPlayer);

        if(opt.isEmpty()) {
            player.sendMessage(messages.commands().member().notAMember()
                    .with("player", memberPlayer.getName()));
            return;
        }
        ClanMember other = opt.get();

        if (other.role().weight() >= member.role().weight()) {
            System.out.println("other: " + other.role().weight() + " member: " + member.role().weight());
            player.sendMessage(messages.commands().member().memberHasHigherWeight().with("member", memberPlayer.getName()));
        }


        clan.edit(clanEdition -> clanEdition.removeMember(other)).thenAccept(newClan -> {
            player.sendMessage(messages.commands().member().kick().success());
        }).exceptionally(this::exceptionHandler);
    }


    private List<String> memberCompletion(CommandContext<CommandSender> context, String s) {
        if (!(context.getSender() instanceof Player player)) return Collections.emptyList();
        Server server = player.getServer();
        return clanRepository.userClanIfCached(player.getUniqueId())
                .map(Clan::members)
                .map(members ->
                        members.stream()
                                .map(m -> m.asPlayer(server))
                                .filter(Objects::nonNull)
                                .map(Player::getName)
                                .collect(Collectors.toList())
                ).orElse(Collections.emptyList());

    }
}

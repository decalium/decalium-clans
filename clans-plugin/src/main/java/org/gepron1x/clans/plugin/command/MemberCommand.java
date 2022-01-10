package org.gepron1x.clans.plugin.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.bukkit.parsers.OfflinePlayerArgument;
import cloud.commandframework.context.CommandContext;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.CachingClanManager;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MemberCommand extends AbstractCommand {


    public MemberCommand(@NotNull Logger logger, CachingClanManager clanManager,
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
                .handler(this::setRole)
        );

        manager.command(builder.literal("kick")
                .permission("clans.member.kick")
                .argument(OfflinePlayerArgument.<CommandSender>newBuilder("member").withSuggestionsProvider(this::memberCompletion))
                .handler(this::kickMember)
        );

    }


    private void setRole(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        OfflinePlayer memberPlayer = context.get("member");
        ClanRole role = context.get("role");
        requireClan(player).thenComposeSync(clan -> {
            if (clan == null) return nullFuture();

            ClanMember member = getMember(clan, player);

            if (!checkPermission(player, member, ClanPermission.SET_ROLE)) return nullFuture();


            ClanMember other = clan.getMember(memberPlayer.getUniqueId());
            if (other == null) {
                player.sendMessage(messages.commands().member().notAMember()
                        .with("player", memberPlayer.getName()));
                return nullFuture();
            }



            if(other.getRole().getWeight() > member.getRole().getWeight()) {
                player.sendMessage(messages.commands().member().memberHasHigherWeight().with("member", memberPlayer.getName()));
                return nullFuture();
            }

            if(member.getRole().getWeight() <= role.getWeight()) {
                player.sendMessage(messages.commands().member().role().roleHasHigherWeight().with("role", role.getDisplayName()));
                return nullFuture();
            }

            return this.clanManager.editClan(clan,
                    clanEditor -> clanEditor
                            .editMember(memberPlayer.getUniqueId(), memberEditor -> memberEditor.setRole(role)));
        }).thenAcceptSync(clan -> {
            if (clan != null) player.sendMessage(messages.commands().member().role().success());
        }).exceptionally(this::exceptionHandler);
    }

    private void kickMember(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        OfflinePlayer memberPlayer = context.get("member");

        requireClan(player).thenComposeSync(clan -> {
            if (clan == null) return nullFuture();
            ClanMember member = getMember(clan, memberPlayer);

            if (!checkPermission(player, member, ClanPermission.KICK)) return nullFuture();
            ClanMember other = clan.getMember(memberPlayer);
            if (other == null) {
                player.sendMessage(messages.commands().member().notAMember()
                        .with("player", memberPlayer.getName()));
                return nullFuture();
            }
            if (other.getRole().getWeight() >= member.getRole().getWeight()) {
                player.sendMessage(messages.commands().member().memberHasHigherWeight().with("member", memberPlayer.getName()));
                return nullFuture();
            }

            return this.clanManager.editClan(clan, clanEditor -> clanEditor.removeMember(member));
        }).thenAccept(clan -> {
            if (clan != null) player.sendMessage(messages.commands().member().kick().success());
        }).exceptionally(this::exceptionHandler);
    }


    private List<String> memberCompletion(CommandContext<CommandSender> context, String s) {
        if (!(context.getSender() instanceof Player player)) return Collections.emptyList();
        Clan clan = clanManager.getUserClanIfPresent(player.getUniqueId());
        if (clan == null) return Collections.emptyList();
        Server server = player.getServer();
        return clan.getMembers().stream()
                .map(m -> m.asPlayer(server))
                .filter(Objects::nonNull)
                .map(Player::getName)
                .filter(st -> st.startsWith(s))
                .collect(Collectors.toList());
    }
}

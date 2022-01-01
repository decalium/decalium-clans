package org.gepron1x.clans.plugin.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.bukkit.parsers.OfflinePlayerArgument;
import cloud.commandframework.context.CommandContext;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.ClanManager;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Objects;

public class MemberCommand extends AbstractCommand {


    public MemberCommand(@NotNull ClanManager clanManager,
                         @NotNull ClansConfig config,
                         @NotNull MessagesConfig messages, @NotNull FactoryOfTheFuture futuresFactory) {
        super(clanManager, config, messages, futuresFactory);
    }
    @Override
    public void register(CommandManager<CommandSender> manager) {

        Command.Builder<CommandSender> builder = manager.commandBuilder("clan").literal("member").senderType(Player.class);
        manager.command(builder
                .literal("set")
                .literal("role")
                .permission("clans.set.role")
                .argument(OfflinePlayerArgument.of("member"))
                .argument(manager.argumentBuilder(ClanRole.class, "role"))
                .handler(this::setRole)
        );

    }


    private void setRole(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        OfflinePlayer memberPlayer = context.get("member");
        ClanRole role = context.get("role");
        this.clanManager.getUserClan(player.getUniqueId())
                .thenComposeSync(clan -> {
                    if(!checkClan(player, clan)) return nullFuture();
                    Objects.requireNonNull(clan);

                    ClanMember member = getMember(clan, player);
                    if(!checkPermission(player, member, ClanPermission.SET_ROLE)) return nullFuture();

                    ClanMember other = clan.getMember(memberPlayer.getUniqueId());

                    if(other == null) {
                        player.sendMessage(messages.commands().member().notAMember()
                                .with("player", memberPlayer.getName()));
                        return nullFuture();
                    }
                    return this.clanManager.editClan(clan,
                            clanEditor -> clanEditor
                                    .editMember(memberPlayer.getUniqueId(), memberEditor -> memberEditor.setRole(role)));
                }).thenAcceptSync(clan -> {
                    if(clan != null) player.sendMessage(messages.commands().member().role().success());
                });
    }

    private void kickMember(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        OfflinePlayer memberPlayer = context.get("member");
        this.clanManager.getUserClan(player).thenComposeSync(clan -> {

            if(!checkClan(player, clan)) return nullFuture();
            Objects.requireNonNull(clan);

            ClanMember member = getMember(clan, memberPlayer);

            if(!checkPermission(player, member, ClanPermission.KICK)) return nullFuture();
            ClanMember other = clan.getMember(memberPlayer);
            if(other == null) {
                player.sendMessage(messages.commands().member().notAMember()
                        .with("player", memberPlayer.getName()));
                return nullFuture();
            }
            if(other.getRole().getWeight() >= member.getRole().getWeight()) {
                player.sendMessage(messages.commands().member().memberHasHigherWeight().with("member", memberPlayer.getName()));
                return futuresFactory.completedFuture(null);
            }

            return this.clanManager.editClan(clan, clanEditor -> clanEditor.removeMember(member));
        }).thenAccept(clan -> {
            if(clan != null) player.sendMessage(messages.commands().member().kick().success());
        });
    }
}

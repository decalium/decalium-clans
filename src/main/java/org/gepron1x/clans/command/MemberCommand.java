package org.gepron1x.clans.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.bukkit.parsers.OfflinePlayerArgument;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.Permissions;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.clan.member.role.ClanPermission;
import org.gepron1x.clans.clan.member.role.ClanRole;
import org.gepron1x.clans.config.MessagesConfig;
import org.gepron1x.clans.ClanManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class MemberCommand extends BaseClanCommand {

    public static final String TARGET = "target", ROLE = "role";


    public MemberCommand(ClanManager manager, MessagesConfig messages) {
        super(manager, messages);

    }

    @Override
    public void register(@NotNull CommandManager<CommandSender> manager) {
        Command.Builder<CommandSender> member = manager.commandBuilder("clan").literal("member");
        manager.command(member.literal("set").literal("role")
                .senderType(Player.class)
                .permission(Permissions.MEMBER_SET_ROLE)
                .meta(ClanPermission.CLAN_PERMISSION, ClanPermission.SET_ROLE)
                .argument(OfflinePlayerArgument.of(TARGET))
                .argument(CommandArgument.ofType(ClanRole.class, ROLE))
                .handler(this::setRole)
        );
        manager.command(member.literal("kick")
                .senderType(Player.class)
                .permission(Permissions.MEMBER_KICK)
                .meta(ClanPermission.CLAN_PERMISSION, ClanPermission.KICK)
                .argument(OfflinePlayerArgument.of(TARGET))
                .handler(this::kick)
        );
    }

    public void setRole(CommandContext<CommandSender> ctx) {
        Player executor = (Player) ctx.getSender();
        Clan clan = getClan(executor);
        OfflinePlayer target = ctx.get(TARGET);
        ClanRole role = ctx.get(ROLE);
        Template targetTemplate = Template.of(TARGET, target.getName());

        ClanMember member = getMember(clan, executor);
        ClanMember targetMember = clan.getMember(target);
        if(targetMember == null) {
            executor.sendMessage(messages.targetIsNotInClan().withPlaceholder(targetTemplate));
            return;
        }

        if(targetMember.getRole().getWeight() >= member.getRole().getWeight()) {
            executor.sendMessage(messages.member().memberHasBiggerWeight().withPlaceholder(targetTemplate));
            return;
        }
        if(role.getWeight() >= member.getRole().getWeight()) {
            executor.sendMessage(messages.member().weightIsBigger().withPlaceholder(targetTemplate));
            return;
        }

        targetMember.setRole(role);
        executor.sendMessage(messages.member().setRole().success()
                .withPlaceholder(targetTemplate).withPlaceholder(ROLE, role.getDisplayName()));

    }

    public void kick(CommandContext<CommandSender> ctx) {
        Player executor = (Player) ctx.getSender();
        OfflinePlayer target = ctx.get(TARGET);
        Clan clan = getClan(executor);
        if(!isMember(executor, target, clan)) return;

        Template targetTemplate = Template.of(TARGET, target.getName());


        if(getMember(clan, executor).getRole().getWeight() <= getMember(clan, target).getRole().getWeight()) {
            executor.sendMessage(messages.member().memberHasBiggerWeight().withPlaceholder(targetTemplate));
            return;
        }
        clan.removeMember(target);
        executor.sendMessage(messages.member().kickSuccess().withPlaceholder(targetTemplate));
    }






}

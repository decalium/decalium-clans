package org.gepron1x.clans.plugin.command;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionHandler;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.plugin.config.MessagesConfig;

public class PermissiveClanExecutionHandler implements CommandExecutionHandler<CommandSender> {

    private final CommandExecutionHandler<CommandSender> delegate;
    private final ClanPermission permission;
    private final MessagesConfig messages;

    public PermissiveClanExecutionHandler(CommandExecutionHandler<CommandSender> delegate, ClanPermission permission, MessagesConfig messages) {

        this.delegate = delegate;
        this.permission = permission;
        this.messages = messages;
    }
    @Override
    public void execute(@NonNull CommandContext<CommandSender> commandContext) {
        ClanMember member = commandContext.get(ClanExecutionHandler.CLAN_MEMBER);
        if(!member.hasPermission(permission)) {
            commandContext.getSender().sendMessage(messages.noClanPermission());
            return;
        }
        delegate.execute(commandContext);
    }
}

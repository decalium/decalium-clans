package org.gepron1x.clans.plugin.command;

import cloud.commandframework.CommandManager;
import org.bukkit.command.CommandSender;

public abstract class AbstractCommand {


    public abstract void register(CommandManager<CommandSender> manager);

}

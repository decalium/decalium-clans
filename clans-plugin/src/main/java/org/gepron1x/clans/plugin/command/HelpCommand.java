/*
 * decalium-clans-rewrite
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans-rewrite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans-rewrite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans-rewrite. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.plugin.command;

import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import org.bukkit.command.CommandSender;

public final class HelpCommand implements AbstractCommand {

    private final MinecraftHelp<CommandSender> help;

    public HelpCommand(MinecraftHelp<CommandSender> help) {
        this.help = help;
    }
    @Override
    public void register(CommandManager<CommandSender> manager) {
        manager.command(manager.commandBuilder("clan").literal("help", "usage").argument(IntegerArgument.of("page")));
    }

    private void help(CommandContext<CommandSender> context) {
        help.queryCommands(context.getRawInputJoined(), context.getSender());

    }
}

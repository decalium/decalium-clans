/*
 * decalium-clans
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.plugin.command;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionHandler;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.plugin.config.messages.MessagesConfig;

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
		if (!member.hasPermission(permission)) {
			messages.noClanPermission().send(commandContext.getSender());
			return;
		}
		delegate.execute(commandContext);
	}
}

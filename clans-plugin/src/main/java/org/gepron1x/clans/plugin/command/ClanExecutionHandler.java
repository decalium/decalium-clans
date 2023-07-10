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
import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import io.leangen.geantyref.TypeToken;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.user.Users;
import org.gepron1x.clans.plugin.config.messages.MessagesConfig;
import org.slf4j.Logger;

public final class ClanExecutionHandler implements CommandExecutionHandler<CommandSender> {

    public static final CloudKey<Clan> CLAN = SimpleCloudKey.of("decalium_clan", TypeToken.get(Clan.class));
    public static final CloudKey<ClanMember> CLAN_MEMBER = SimpleCloudKey.of("decalium_member", TypeToken.get(ClanMember.class));

    private final CommandExecutionHandler<CommandSender> delegate;
    private final Users users;
    private final MessagesConfig messages;
    private final Logger logger;

    public ClanExecutionHandler(CommandExecutionHandler<CommandSender> delegate,
                                Users users,
                                MessagesConfig messages,
                                Logger logger) {
        this.delegate = delegate;
        this.users = users;
        this.messages = messages;
        this.logger = logger;
    }

    @Override
    public void execute(@NonNull CommandContext<CommandSender> commandContext) {
        Player player = (Player) commandContext.getSender();
        users.userFor(player).clan().ifPresentOrElse(clan -> {
            commandContext.store(CLAN, clan);
            commandContext.store(CLAN_MEMBER, clan.member(player).orElseThrow());
            this.delegate.execute(commandContext);
        }, () -> messages.notInTheClan().send(player));
    }
}

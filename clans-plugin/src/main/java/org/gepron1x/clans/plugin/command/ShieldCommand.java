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

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.meta.CommandMeta;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.shield.Shields;
import org.gepron1x.clans.api.user.Users;
import org.gepron1x.clans.plugin.config.messages.MessagesConfig;
import org.gepron1x.clans.plugin.config.settings.ClansConfig;
import org.slf4j.Logger;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.time.Duration;

public final class ShieldCommand extends AbstractClanCommand {
    private final Shields shields;

    public ShieldCommand(Logger logger, CachingClanRepository clanRepository, Users users, ClansConfig clansConfig, MessagesConfig messages, FactoryOfTheFuture futuresFactory, Shields shields) {
        super(logger, clanRepository, users, clansConfig, messages, futuresFactory);
        this.shields = shields;
    }

    @Override
    public void register(CommandManager<CommandSender> manager) {
        var descriptions = messages.help().messages().descriptions().shield();
        Command.Builder<CommandSender> builder = manager.commandBuilder("clan").literal("shield").senderType(Player.class);
        manager.command(builder.argument(IntegerArgument.of("duration")).permission("clans.shield.apply").handler(clanExecutionHandler(this::addShield)).meta(CommandMeta.DESCRIPTION, descriptions.apply()));
    }

    private void addShield(CommandContext<CommandSender> context) {
        Clan clan = context.get(ClanExecutionHandler.CLAN);
        shields.add(clan, Duration.ofSeconds((int) context.get("duration"))).thenAccept(shield -> {
            context.getSender().sendMessage("Worked");
        }).exceptionally(this.exceptionHandler(context.getSender()));
    }
}

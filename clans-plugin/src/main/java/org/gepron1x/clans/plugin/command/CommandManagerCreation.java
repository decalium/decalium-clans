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

import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.parser.ParserRegistry;
import cloud.commandframework.exceptions.ArgumentParseException;
import cloud.commandframework.exceptions.InvalidCommandSenderException;
import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.exceptions.NoPermissionException;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import io.leangen.geantyref.TypeToken;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.api.exception.DescribingException;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.plugin.command.parser.*;
import org.gepron1x.clans.plugin.config.messages.MessagesConfig;

import java.util.function.UnaryOperator;

public final class CommandManagerCreation {

    private final Plugin plugin;
    private final CachingClanRepository repository;
    private final RoleRegistry roleRegistry;
    private final MessagesConfig messages;

    public CommandManagerCreation(Plugin plugin, CachingClanRepository repository, RoleRegistry roleRegistry, MessagesConfig messages) {

        this.plugin = plugin;
        this.repository = repository;
        this.roleRegistry = roleRegistry;
        this.messages = messages;
    }


    public PaperCommandManager<CommandSender> create() throws Exception {
        PaperCommandManager<CommandSender> manager = new PaperCommandManager<>(
                plugin,
                CommandExecutionCoordinator.simpleCoordinator(),
                UnaryOperator.identity(), UnaryOperator.identity()
        );

        registerParsers(manager);
        exceptionHandlers(manager);
        manager.registerBrigadier();
        manager.setSetting(CommandManager.ManagerSettings.OVERRIDE_EXISTING_COMMANDS, true);

        return manager;
    }

    private void registerParsers(CommandManager<CommandSender> manager) {
        ParserRegistry<CommandSender> parserRegistry = manager.parserRegistry();
        parserRegistry.registerParserSupplier(TypeToken.get(ClanRole.class), params ->
            new MessagingParser<>(new ClanRoleParser<>(roleRegistry), messages.commands().member().role().roleNotFound())
        );
        parserRegistry.registerParserSupplier(TypeToken.get(ClanMember.class), params ->
            new MessagingParser<>(new MemberParser(repository, plugin.getServer()), messages.commands().member().notAMember())
        );
        parserRegistry.registerParserSupplier(TypeToken.get(ClanHome.class), params ->
                new MessagingParser<>(new HomeParser(repository), messages.commands().home().homeNotFound())
        );
        parserRegistry.registerParserSupplier(TypeToken.get(ClanReference.class), params ->
            new MessagingParser<>(new ClanReferenceParser(repository), messages.noOnlinePlayers())
        );
    }

    private void exceptionHandlers(CommandManager<CommandSender> manager) {
        manager.registerExceptionHandler(NoPermissionException.class, (sender, ex) -> {
            sender.sendMessage(this.messages.noPermission());
        });
        manager.registerExceptionHandler(InvalidSyntaxException.class, (sender, ex) -> {
            sender.sendMessage(this.messages.commands().invalidSyntax().withMiniMessage("syntax", ex.getCorrectSyntax().replace("|", "<gray>, </gray>")));
        });

        manager.registerExceptionHandler(ArgumentParseException.class, (sender, ex) -> {
            Throwable exception = ex.getCause();
            if(exception instanceof DescribingException e) {
                sender.sendMessage(e.description());
            }
            else {
                sender.sendMessage(messages.commands().invalidArgument().withMiniMessage("message", ex.getMessage()));
            }
        });

        manager.registerExceptionHandler(InvalidCommandSenderException.class, (sender, ex) -> {
            if(ex.getRequiredSender().equals(Player.class)) {
                sender.sendMessage(messages.commands().onlyPlayersCanDoThis());
            } else {
                sender.sendMessage("Only senders with type " + ex.getRequiredSender().getSimpleName() + " can do this.");
            }
        });
    }
}

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
import cloud.commandframework.execution.CommandExecutionHandler;
import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.permission.CommandPermission;
import cloud.commandframework.permission.PredicatePermission;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.exception.DescribingException;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.user.Users;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.slf4j.Logger;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.concurrent.CompletionException;
import java.util.function.Function;
public abstract class AbstractClanCommand implements AbstractCommand {

    public static final CommandMeta.Key<Component> DESCRIPTION = CommandMeta.Key.of(Component.class, "cmd_description");

    public static final CloudKey<Void> CLAN_REQUIRED = SimpleCloudKey.of("clan_required");


    protected final Logger logger;
    protected final Users users;
    protected final CachingClanRepository clanRepository;
    protected final ClansConfig clansConfig;
    protected final MessagesConfig messages;
    protected final FactoryOfTheFuture futuresFactory;

    public AbstractClanCommand(Logger logger, CachingClanRepository clanRepository, Users users,
                               ClansConfig clansConfig,
                               MessagesConfig messages,
                               FactoryOfTheFuture futuresFactory) {
        this.logger = logger;
        this.clanRepository = clanRepository;
        this.users = users;
        this.clansConfig = clansConfig;
        this.messages = messages;
        this.futuresFactory = futuresFactory;
    }



    public abstract void register(CommandManager<CommandSender> manager);


    
    protected <T> T exceptionHandler(Throwable throwable) {
        logger.error("A future completed exceptionally: ", throwable);
        return null;
    }

    protected <T> Function<Throwable, T> exceptionHandler(Audience sender) {
        return t -> {
            if(t instanceof CompletionException completionException && completionException.getCause() instanceof DescribingException ex) {
                sender.sendMessage(ex.description());
                return null;
            }
            sender.sendMessage(Component.text("Error happened while executing command; see console for more info.", NamedTextColor.RED));
            return exceptionHandler(t);
        };
    }

    protected ClanExecutionHandler clanExecutionHandler(CommandExecutionHandler<CommandSender> delegate) {
        return new ClanExecutionHandler(delegate, this.users, this.messages, this.logger);
    }

    protected PermissiveClanExecutionHandler permissionRequired(CommandExecutionHandler<CommandSender> delegate, ClanPermission permission) {
        return new PermissiveClanExecutionHandler(delegate, permission, this.messages);
    }

    protected CommandPermission clanRequired() {
        return PredicatePermission.of(SimpleCloudKey.of("clan_required"), sender -> {
            if(!(sender instanceof Player player)) return false;
            return this.users.userFor(player).clan().isPresent();
        });
    }












}

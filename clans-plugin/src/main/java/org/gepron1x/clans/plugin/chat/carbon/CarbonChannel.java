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
package org.gepron1x.clans.plugin.chat.carbon;

import net.draycia.carbon.api.channels.ChatChannel;
import net.draycia.carbon.api.users.CarbonPlayer;
import net.draycia.carbon.api.util.RenderedMessage;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.gepron1x.clans.plugin.chat.common.Channel;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class CarbonChannel implements ChatChannel {

    private final Channel channel;
    private final Server server;
    private final ComponentLike message;
    private final String command;
    private final String permission;

    public CarbonChannel(Channel channel, Server server, ComponentLike message, String command, String permission) {

        this.channel = channel;
        this.server = server;
        this.message = message;
        this.command = command;
        this.permission = permission;
    }

    private Optional<Player> player(CarbonPlayer player) {
        return Optional.ofNullable(server.getPlayer(player.uuid()));
    }

    private ChannelPermissionResult result(CarbonPlayer player) {
        return ChannelPermissionResult.allowedIf(message.asComponent(), () -> {
            return player(player).map(this.channel::usePermitted).orElse(false);
        });
    }
    @Override
    public ChannelPermissionResult speechPermitted(CarbonPlayer carbonPlayer) {
        return result(carbonPlayer);
    }

    @Override
    public ChannelPermissionResult hearingPermitted(CarbonPlayer player) {
        return result(player);
    }

    @Override
    public List<Audience> recipients(CarbonPlayer sender) {
        Set<? extends Audience> audiences = player(sender).map(this.channel::recipients).orElse(Collections.emptySet());
        return List.copyOf(audiences);
    }

    @Override
    public Set<CarbonPlayer> filterRecipients(CarbonPlayer sender, Set<CarbonPlayer> recipients) {
        return player(sender).map(player -> {
            Set<Player> players = recipients.stream().map(this::player)
                    .filter(Optional::isPresent)
                    .map(Optional::get).collect(Collectors.toSet());
            return recipients.stream().filter(carbonPlayer -> player(carbonPlayer).map(players::contains).orElse(false))
                    .collect(Collectors.toUnmodifiableSet());
        }).orElse(Collections.emptySet());
    }

    @Override
    public @Nullable String quickPrefix() {
        return this.channel.prefix();
    }

    @Override
    public boolean shouldRegisterCommands() {
        return true;
    }

    @Override
    public String commandName() {
        return command;
    }

    @Override
    public List<String> commandAliases() {
        return List.of();
    }

    @Override
    public @MonotonicNonNull String permission() {
        return permission;
    }

    @Override
    public double radius() {
        return -1;
    }

    @Override
    public @NotNull RenderedMessage render(CarbonPlayer sender, Audience recipient, Component message, Component originalMessage) {
        Player player = player(sender).orElseThrow();
        return new RenderedMessage(this.channel.render(player, recipient, message, originalMessage), MessageType.CHAT);
    }

    @Override
    public @NotNull Key key() {
        return channel.key();
    }
}

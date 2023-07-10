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
import org.bukkit.Server;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.gepron1x.clans.api.chat.ClanTagResolver;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.IdentifiedDraftClan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.plugin.cache.ClanCache;
import org.gepron1x.clans.plugin.chat.resolvers.PapiTagResolver;
import org.gepron1x.clans.plugin.config.Configs;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public final class ClanChatChannel implements ChatChannel {
    private static final Key KEY = Key.key("decaliumclans", "clanchat");
    private final Server server;
    private final ClanCache cache;
    private final Configs configs;

    public ClanChatChannel(@NotNull Server server, @NotNull ClanCache cache, @NotNull Configs configs) {
        this.server = server;
        this.cache = cache;
        this.configs = configs;
    }
    @Override
    public ChannelPermissionResult speechPermitted(CarbonPlayer carbonPlayer) {
        return ifInTheClan(carbonPlayer);
    }

    @Override
    public ChannelPermissionResult hearingPermitted(CarbonPlayer player) {
        return ifInTheClan(player);
    }

    private ChannelPermissionResult ifInTheClan(CarbonPlayer player) {
        return ChannelPermissionResult.allowedIf(
                configs.config().chat().notInTheClan().asComponent(),
                () -> cache.getUserClan(player.uuid()) != null
        );
    }

    @Override
    public List<Audience> recipients(CarbonPlayer sender) {
        IdentifiedDraftClan clan = cache.getUserClan(sender.uuid());
        if(clan == null) return Collections.emptyList();
        return clan.members().stream()
                .map(m -> m.asPlayer(server))
                .filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public Set<CarbonPlayer> filterRecipients(CarbonPlayer sender, Set<CarbonPlayer> recipients) {
        IdentifiedDraftClan clan = cache.getUserClan(sender.uuid());
        if(clan == null) return Collections.emptySet();

        return recipients.stream().filter(p -> clan.member(sender.uuid()).isPresent()).collect(Collectors.toSet());
    }

    @Override
    public @NotNull String quickPrefix() {
        return "~";
    }

    @Override
    public boolean shouldRegisterCommands() {
        return true;
    }

    @Override
    public String commandName() {
        return "clanschat";
    }

    @Override
    public List<String> commandAliases() {
        return Collections.emptyList();
    }

    @Override
    public @MonotonicNonNull String permission() {
        return "clans.chat";
    }

    @Override
    public double radius() {
        return -1;
    }

    @Override
    public @NotNull RenderedMessage render(CarbonPlayer sender, Audience recipient, Component message, Component originalMessage) {

        DraftClan clan = Objects.requireNonNull(cache.getUserClan(sender.uuid()));
        ClanMember member = clan.member(sender.uuid()).orElseThrow();
        return new RenderedMessage(configs.config().chat().format()
                .with(new PapiTagResolver(this.server.getPlayer(sender.uuid())))
                .with(ClanTagResolver.prefixed(clan))
                .with("role", member.role())
                .with("member", CarbonPlayer.renderName(sender))
                .with("message", originalMessage)
                .asComponent(), MessageType.CHAT);
    }

    @Override
    public @NotNull Key key() {
        return KEY;
    }
}

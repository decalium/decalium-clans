package org.gepron1x.clans.plugin.chat;

import net.draycia.carbon.api.channels.ChatChannel;
import net.draycia.carbon.api.users.CarbonPlayer;
import net.draycia.carbon.api.util.RenderedMessage;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.IdentifiedDraftClan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.plugin.cache.ClanCacheImpl;
import org.gepron1x.clans.plugin.chat.resolvers.ClanTagResolver;
import org.gepron1x.clans.plugin.chat.resolvers.PapiTagResolver;
import org.gepron1x.clans.plugin.chat.resolvers.PrefixedTagResolver;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class ClanChatChannel implements ChatChannel {
    private static final Key KEY = Key.key("decaliumclans", "clanchat");
    private final Server server;
    private final ClanCacheImpl cache;
    private final MessagesConfig messages;
    private final ClansConfig clansConfig;

    public ClanChatChannel(@NotNull Server server, @NotNull ClanCacheImpl cache, @NotNull MessagesConfig messages, @NotNull ClansConfig clansConfig) {
        this.server = server;
        this.cache = cache;
        this.messages = messages;
        this.clansConfig = clansConfig;
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
                messages.notInTheClan().asComponent(),
                () -> cache.getUserClan(player.uuid()) != null
        );
    }

    @Override
    public List<Audience> recipients(CarbonPlayer sender) {
        IdentifiedDraftClan clan = cache.getUserClan(sender.uuid());
        if(clan == null) return Collections.emptyList();
        return clan.members().stream()
                .map(m -> m.asPlayer(server))
                .filter(Objects::nonNull)
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
        return "clanchat";
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
        return new RenderedMessage(clansConfig.chat().format()
                .with(new PapiTagResolver(this.server.getPlayer(sender.uuid())))
                .with(PrefixedTagResolver.prefixed(ClanTagResolver.clan(clan), "clan"))
                .with("role", member.role())
                .with("member", CarbonPlayer.renderName(sender))
                .with("message", message)
                .asComponent(), MessageType.CHAT);
    }

    @Override
    public @NotNull Key key() {
        return KEY;
    }
}

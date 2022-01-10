package org.gepron1x.clans.plugin.chat;

import net.draycia.carbon.api.channels.ChatChannel;
import net.draycia.carbon.api.users.CarbonPlayer;
import net.draycia.carbon.api.util.RenderedMessage;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Server;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.gepron1x.clans.api.ClanCache;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ClanChatChannel implements ChatChannel {
    private static final Key KEY = Key.key("decaliumclans", "clanchat");
    private final Server server;
    private final ClanCache cache;
    private final MessagesConfig messages;

    public ClanChatChannel(@NotNull Server server, @NotNull ClanCache cache, @NotNull MessagesConfig messages) {
        this.server = server;
        this.cache = cache;
        this.messages = messages;
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
        Clan clan = cache.getUserClan(sender.uuid());
        if(clan == null) return Collections.emptyList();
        return clan.getMembers().stream()
                .map(m -> m.asPlayer(server))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Set<CarbonPlayer> filterRecipients(CarbonPlayer sender, Set<CarbonPlayer> recipients) {
        Clan clan = cache.getUserClan(sender.uuid());
        if(clan == null) return Collections.emptySet();

        return recipients.stream().filter(p -> clan.getMember(sender.uuid()) != null).collect(Collectors.toSet());
    }

    @Override
    public @Nullable String quickPrefix() {
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
    public @NotNull RenderedMessage render(CarbonPlayer sender, Audience recipient, Component message, Component originalMessage) {
        String template = "Clan chat| <role> <name> -> <message>";
        return new RenderedMessage(MiniMessage.miniMessage().deserialize(template), MessageType.CHAT);
    }

    @Override
    public @NotNull Key key() {
        return Key.key("decaliumclans", "clans");
    }
}

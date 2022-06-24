package org.gepron1x.clans.api.audience;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class RenderedPlayerName implements ComponentLike {

    private final UUID uuid;
    private final Server server;

    public RenderedPlayerName(UUID uuid, Server server) {

        this.uuid = uuid;
        this.server = server;
    }
    @Override
    public @NotNull Component asComponent() {
        OfflinePlayer player = server.getOfflinePlayer(uuid);
        Player onlinePlayer = player.getPlayer();
        if(onlinePlayer != null) return onlinePlayer.displayName();

        String name = player.getName();
        if(name != null) return Component.text(name, NamedTextColor.GRAY);

        return Component.text().append(
                Component.text("Unknown player "),
                Component.text("("+uuid+")").clickEvent(ClickEvent.copyToClipboard(uuid.toString()))
        ).color(NamedTextColor.GRAY).build();

    }
}

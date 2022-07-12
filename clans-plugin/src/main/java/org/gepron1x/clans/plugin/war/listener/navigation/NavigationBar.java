package org.gepron1x.clans.plugin.war.listener.navigation;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.jetbrains.annotations.NotNull;

public final class NavigationBar implements ComponentLike {

    private final MessagesConfig messages;
    private final Player player;
    private final Player target;

    public NavigationBar(MessagesConfig messages, Player player, Player target) {
        this.messages = messages;
        this.player = player;
        this.target = target;
    }

    @Override
    public @NotNull Component asComponent() {
        Location first = player.getEyeLocation();
        Location second = target.getLocation();
        double distance = first.distance(second);
        return messages.war().navigationBarFormat()
                .with("target", target.displayName())
                .with("distance", "%.1f".formatted(distance))
                .with("arrow", NavigationArrow.arrow(first, second)).asComponent();
    }
}

package org.gepron1x.clans.plugin.war.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public final class NavigationBar implements ComponentLike {

    private final Player player;
    private final Player target;

    public NavigationBar(Player player, Player target) {
        this.player = player;
        this.target = target;
    }

    @Override
    public @NotNull Component asComponent() {
        Location first = player.getEyeLocation();
        Location second = target.getLocation();
        double distance = player.getLocation().distance(target.getLocation());
        return text()
                .append(text("Distance to "))
                .append(target.displayName())
                .append(text(": "))
                .append(text(String.format("%.1f", distance)))
                .append(text(new NavigationArrow(first, second).toString()).color(NamedTextColor.DARK_RED))
                .color(NamedTextColor.GRAY)
                .build();
    }
}

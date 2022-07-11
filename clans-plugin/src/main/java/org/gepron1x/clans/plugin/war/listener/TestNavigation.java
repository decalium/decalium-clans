package org.gepron1x.clans.plugin.war.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public final class TestNavigation implements Runnable {

    private final Server server;

    public TestNavigation(Server server) {

        this.server = server;
    }
    @Override
    public void run() {
        for(Player player : this.server.getOnlinePlayers()) {
            Location first = player.getLocation().clone().set(0, 0, 0);
            Location second = player.getEyeLocation();
            player.sendActionBar(Component.text(new NavigationArrow(second, first).toString()).decorate(TextDecoration.BOLD).color(NamedTextColor.GOLD));
        }

    }
}

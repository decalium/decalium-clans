package org.gepron1x.clans.plugin.bootstrap;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.gepron1x.clans.api.war.Wars;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.gepron1x.clans.plugin.war.announce.AnnouncingWars;
import org.gepron1x.clans.plugin.war.impl.DefaultWars;
import org.gepron1x.clans.plugin.war.listener.DeathListener;
import org.gepron1x.clans.plugin.war.listener.NoTeamDamageListener;
import org.gepron1x.clans.plugin.war.listener.navigation.Navigation;

public final class WarsCreation {

    private final Plugin plugin;
    private final ClansConfig config;
    private final MessagesConfig messages;

    public WarsCreation(Plugin plugin, ClansConfig config, MessagesConfig messages) {

        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
    }

    public Wars create() {

        PluginManager pm = plugin.getServer().getPluginManager();
        Wars base = new DefaultWars(plugin.getServer());
        Wars wars = new AnnouncingWars(base, messages);
        pm.registerEvents(new DeathListener(wars), plugin);
        if(config.wars().disableTeamDamage()) pm.registerEvents(new NoTeamDamageListener(base), plugin);
        plugin.getServer().getScheduler().runTaskTimer(plugin, new Navigation(base, messages), 5, 5);
        return wars;

    }
}

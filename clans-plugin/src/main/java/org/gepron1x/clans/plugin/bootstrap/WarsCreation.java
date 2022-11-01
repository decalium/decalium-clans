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
package org.gepron1x.clans.plugin.bootstrap;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.gepron1x.clans.api.war.Wars;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.gepron1x.clans.plugin.listener.CrystalExplosionListener;
import org.gepron1x.clans.plugin.war.announce.AnnouncingWars;
import org.gepron1x.clans.plugin.war.impl.DefaultWars;
import org.gepron1x.clans.plugin.war.listener.DeathListener;
import org.gepron1x.clans.plugin.war.listener.NoTeamDamageListener;
import org.gepron1x.clans.plugin.war.navigation.Navigation;

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
        pm.registerEvents(new CrystalExplosionListener(), plugin);
        plugin.getServer().getScheduler().runTaskTimer(plugin, new Navigation(base, messages), 5, 5);
        return wars;

    }
}

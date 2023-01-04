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
package org.gepron1x.clans.plugin.util.services;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

import java.util.Optional;

public final class PluginServices implements Services {

    private final Plugin plugin;
    private final ServicesManager services;

    public PluginServices(Plugin plugin) {

        this.plugin = plugin;
        services = plugin.getServer().getServicesManager();
    }
    @Override
    public <T> void register(Class<T> clazz, T service, ServicePriority priority) {
        services.register(clazz, service, this.plugin, priority);
    }

    @Override
    public void unregister(Object service) {
        services.unregister(service);
    }

    @Override
    public <T> Optional<T> get(Class<T> clazz) {
        return Optional.ofNullable(services.getRegistration(clazz)).map(RegisteredServiceProvider::getProvider);
    }
}

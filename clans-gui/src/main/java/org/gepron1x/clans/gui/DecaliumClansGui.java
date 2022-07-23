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
package org.gepron1x.clans.gui;

import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.api.DecaliumClansApi;

import java.util.Optional;
import java.util.function.UnaryOperator;

public final class DecaliumClansGui extends JavaPlugin {

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        DecaliumClansApi api = Optional.ofNullable(this.getServer().getServicesManager()
                        .getRegistration(DecaliumClansApi.class))
                .map(RegisteredServiceProvider::getProvider).orElseThrow();
        PaperCommandManager<CommandSender> commandManager;
        try {
            commandManager = new PaperCommandManager<>(this,
                    CommandExecutionCoordinator.simpleCoordinator(),
                    UnaryOperator.identity(), UnaryOperator.identity());
        } catch (Exception e) {
            getSLF4JLogger().error("Error initializing command manager: ", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        commandManager.registerBrigadier();
        commandManager.command(commandManager.commandBuilder("clangui").senderType(Player.class)
                .permission("clans.gui")
                .handler(ctx -> {
                    Player player = (Player) ctx.getSender();
                    api.repository().userClanIfCached(player).ifPresent(clan -> {
                        new ClanGui(getServer(), clan).asGui().show(player);
                    });
                })
        );

    }
}

/*
 * decalium-clans
 * Copyright © 2023 George Pronyuk <https://vk.com/gpronyuk>
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

import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.jeff_media.customblockdata.CustomBlockData;
import me.gepronix.decaliumcustomitems.DecaliumCustomItems;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.gui.item.ClanRegionItem;
import org.gepron1x.clans.plugin.DecaliumClansPlugin;
import org.gepron1x.clans.plugin.util.message.Message;
import org.gepron1x.clans.plugin.util.services.PluginServices;

import java.util.function.UnaryOperator;

public final class DecaliumClansGui extends JavaPlugin {

	private static final MiniMessage MINI_MESSAGE = MiniMessage.builder().postProcessor(c -> c.decoration(TextDecoration.ITALIC, false)).build();

	public static Message message(String value) {
		return Message.message(value, MINI_MESSAGE);
	}

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
		CustomBlockData.registerListener(this);

        DecaliumClansApi api = new PluginServices(this).get(DecaliumClansApi.class).orElseThrow();
		DecaliumClansPlugin clansPlugin = JavaPlugin.getPlugin(DecaliumClansPlugin.class);
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

		DecaliumCustomItems.get().getItemRegistry().registerItem(new ClanRegionItem(this, api, clansPlugin.messages()).build());
        commandManager.registerBrigadier();
        commandManager.command(commandManager.commandBuilder("clangui").senderType(Player.class)
                .permission("clans.gui").argument(PlayerArgument.optional("player"))
                .handler(ctx -> {
                    Player player = (Player) ctx.getOrSupplyDefault("player", ctx::getSender);
                    api.repository().userClanIfCached(player).ifPresentOrElse(clan -> {
                        new ClanGui(getServer(), clan, api).asGui().show((Player) ctx.getSender());
                    }, () -> {
						ctx.getSender().sendMessage(clansPlugin.messages().notInTheClan());
					});
                })
        );

    }
}

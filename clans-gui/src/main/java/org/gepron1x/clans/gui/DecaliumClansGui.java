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

import com.jeff_media.customblockdata.CustomBlockData;
import me.gepronix.decaliumcustomitems.DecaliumCustomItems;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.user.ClanUser;
import org.gepron1x.clans.gui.item.ClanRegionItem;
import org.gepron1x.clans.libraries.cloud.commandframework.bukkit.parsers.PlayerArgument;
import org.gepron1x.clans.plugin.DecaliumClansPlugin;
import org.gepron1x.clans.plugin.util.message.TextMessage;
import org.gepron1x.clans.plugin.util.services.PluginServices;

public final class DecaliumClansGui extends JavaPlugin {

	public static final MiniMessage MINI_MESSAGE = MiniMessage.builder()
			.postProcessor(c -> {
				var result = c;
				if(!result.hasDecoration(TextDecoration.ITALIC)) result = result.decoration(TextDecoration.ITALIC, false);
				return result;
			})
			.editTags(builder -> {
			builder.resolver(new DecaliumColorResolver());
	}).build();

	public static TextMessage message(String value) {
		return TextMessage.message(value, MINI_MESSAGE);
	}

    @Override
    public void onDisable() {
        DecaliumCustomItems.get().getItemRegistry().removeItem(ClanRegionItem.HOME_ITEM);
    }

    @Override
    public void onEnable() {
		CustomBlockData.registerListener(this);

        DecaliumClansApi api = new PluginServices(this).get(DecaliumClansApi.class).orElseThrow();
		DecaliumClansPlugin clansPlugin = JavaPlugin.getPlugin(DecaliumClansPlugin.class);
        var commandManager = clansPlugin.commandManager();

		DecaliumCustomItems.get().getItemRegistry().registerItem(new ClanRegionItem(this, api, clansPlugin.messages()).build());
        commandManager.command(commandManager.commandBuilder("clan").literal("gui").senderType(Player.class)
                .permission("clans.gui").argument(PlayerArgument.optional("player"))
                .handler(ctx -> {
                    Player player = (Player) ctx.getOrSupplyDefault("player", ctx::getSender);
					Player viewer = (Player) ctx.getSender();
					api.users().userFor(player).clan().ifPresent(clan -> {
						new ClanGui(getServer(), clan, api.users().userFor(viewer), api).asGui().show(viewer);
					});
                })
        );
		commandManager.command(commandManager.commandBuilder("clan").senderType(Player.class).permission("clans.gui").handler(ctx -> {
			Player sender = (Player) ctx.getSender();
			ClanUser user = api.users().userFor(sender);
			user.clan().map(clan -> new ClanGui(getServer(), clan, user, api).asGui())
					.orElseGet(() -> new ClanCreationGui(user, api).asGui()).show(sender);
		}));

    }
}

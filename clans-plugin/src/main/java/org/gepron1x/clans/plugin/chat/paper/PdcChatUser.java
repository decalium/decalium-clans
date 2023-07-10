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
package org.gepron1x.clans.plugin.chat.paper;

import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.plugin.DecaliumClansPlugin;

import java.util.Optional;

public final class PdcChatUser implements ChatUser {

	private static final NamespacedKey CHAT_CHANNEL = new NamespacedKey(JavaPlugin.getPlugin(DecaliumClansPlugin.class), "chat_channel");

	private final PersistentDataContainer container;

	public PdcChatUser(PersistentDataContainer container) {

		this.container = container;
	}

	public PdcChatUser(PersistentDataHolder holder) {
		this(holder.getPersistentDataContainer());
	}

	@Override
	public Optional<Key> currentChannelKey() {
		return Optional.ofNullable(container.get(CHAT_CHANNEL, KeyDataType.KEY));
	}

	@Override
	public void currentChannelKey(Key key) {
		container.set(CHAT_CHANNEL, KeyDataType.KEY, key);
	}
}

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
package org.gepron1x.clans.plugin.config;


import com.google.common.base.MoreObjects;
import org.gepron1x.clans.plugin.config.messages.MessagesConfig;
import org.gepron1x.clans.plugin.config.settings.ClansConfig;

import java.util.Objects;

public final class Configs {


	private final Configuration<ClansConfig> clansConfig;
	private final Configuration<MessagesConfig> messagesConfig;

	public Configs(Configuration<ClansConfig> clansConfig, Configuration<MessagesConfig> messagesConfig) {

		this.clansConfig = clansConfig;
		this.messagesConfig = messagesConfig;
	}

	public void reload() {
		this.clansConfig.reloadConfig();
		this.messagesConfig.reloadConfig();
	}

	public ClansConfig config() {
		return this.clansConfig.data();
	}

	public MessagesConfig messages() {
		return this.messagesConfig.data();
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Configs configs = (Configs) o;
		return Objects.equals(clansConfig, configs.clansConfig) && Objects.equals(messagesConfig, configs.messagesConfig);
	}

	@Override
	public int hashCode() {
		return Objects.hash(clansConfig, messagesConfig);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("clansConfig", clansConfig)
				.add("messagesConfig", messagesConfig)
				.toString();
	}
}

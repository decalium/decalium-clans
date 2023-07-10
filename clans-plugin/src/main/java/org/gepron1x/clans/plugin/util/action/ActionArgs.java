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
package org.gepron1x.clans.plugin.util.action;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.gepron1x.clans.plugin.util.message.TextMessage;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

final class ActionArgs {
	private final List<String> values;

	final static class Arg {
		private final String value;

		public Arg(String value) {
			this.value = value;
		}


		public String asString() {
			return value;
		}

		public int asInteger() {
			return Integer.parseInt(value);
		}

		public float asFloat() {
			return Float.parseFloat(value);
		}

		public double asDouble() {
			return Double.parseDouble(value);
		}

		public Key asKey() {
			return Key.key(value);
		}

		public Duration asDuration() {
			return Duration.ofMillis(asInteger() * 50L);
		}

		public TextMessage asMessage(MiniMessage miniMessage) {
			return TextMessage.message(value, miniMessage);
		}
	}

	public ActionArgs(List<String> values) {

		this.values = values;
	}


	public Optional<Arg> arg(int index) {
		if (values.size() <= index) return Optional.empty();
		return Optional.of(new Arg(values.get(index)));
	}

	public Arg requireArg(int index) {
		return arg(index).orElseThrow();
	}


}

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
package org.gepron1x.clans.api.region;


import java.time.Duration;
import java.time.Instant;

public interface Shield {

	Instant started();

	Instant end();

	default Duration length() {
		return Duration.between(started(), end());
	}

	default Duration left() {
		Duration left = Duration.between(Instant.now(), end());
		if (left.isNegative()) return Duration.ZERO;
		return left;
	}

	default boolean expired() {
		return Instant.now().isAfter(end());
	}

	default boolean active() {
		return !expired();
	}


	Shield NONE = new Shield() {

		@Override
		public Instant started() {
			return Instant.MIN;
		}

		@Override
		public Instant end() {
			return Instant.MIN;
		}

		@Override
		public Duration length() {
			return Duration.ZERO;
		}

		@Override
		public Duration left() {
			return Duration.ZERO;
		}

		@Override
		public boolean expired() {
			return true;
		}


	};
}

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
package org.gepron1x.clans.plugin.clan.member;

import com.google.common.base.MoreObjects;
import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record ClanRoleImpl(String name, Component displayName, int weight,
						   Set<ClanPermission> permissions) implements ClanRole {
	@Override
	public @NotNull String name() {
		return name;
	}

	@Override
	public @NotNull Component displayName() {
		return displayName;
	}

	@Override
	public int weight() {
		return weight;
	}

	@Override
	public @NotNull @Unmodifiable Set<ClanPermission> permissions() {
		return permissions;
	}

	@Override
	public @NotNull ClanRole.Builder toBuilder() {
		return builder().name(name).displayName(displayName).weight(weight).permissions(permissions);
	}

	public static ClanRole.Builder builder() {
		return new BuilderImpl();
	}

	public static class BuilderImpl implements ClanRole.Builder {

		private String name;
		private Component displayName;
		private int weight = 0;
		private final Set<ClanPermission> permissions = new HashSet<>();

		@Override
		public @NotNull Builder name(@NotNull String name) {
			this.name = name;
			return this;
		}

		@Override
		public @NotNull Builder displayName(@NotNull Component displayName) {
			this.displayName = displayName;
			return this;
		}

		@Override
		public @NotNull Builder weight(int weight) {
			this.weight = weight;
			return this;
		}

		@Override
		public @NotNull Builder permissions(@NotNull Collection<? extends ClanPermission> permissions) {
			this.permissions.clear();
			this.permissions.addAll(permissions);
			return this;
		}

		@Override
		public @NotNull Builder addPermission(@NotNull ClanPermission permission) {
			this.permissions.add(permission);
			return this;
		}

		@Override
		public @NotNull Builder emptyPermissions() {
			this.permissions.clear();
			return this;
		}

		@Override
		public @NotNull ClanRole build() {
			return new ClanRoleImpl(name, displayName, weight, Set.copyOf(permissions));
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			BuilderImpl builder = (BuilderImpl) o;
			return weight == builder.weight &&
					Objects.equals(name, builder.name) &&
					Objects.equals(displayName, builder.displayName) &&
					permissions.equals(builder.permissions);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name, displayName, weight, permissions);
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("name", name)
					.add("displayName", displayName)
					.add("weight", weight)
					.add("permissions", permissions)
					.toString();
		}
	}
}

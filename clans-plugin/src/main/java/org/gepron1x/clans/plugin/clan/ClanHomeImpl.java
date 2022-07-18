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
package org.gepron1x.clans.plugin.clan;

import com.google.common.base.MoreObjects;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.edition.home.HomeEdition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public final class ClanHomeImpl implements ClanHome {
    private final String name;
    private final Component displayName;
    private final UUID creator;
    private final Location location;
    private final ItemStack icon;

    public ClanHomeImpl(String name, Component component, UUID creator, Location location, ItemStack icon) {
        this.name = name;
        displayName = component;
        this.creator = creator;
        this.location = location;
        this.icon = icon;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, displayName, creator, location, icon);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof ClanHomeImpl that)) return false;
        return name.equals(that.name) &&
                displayName.equals(that.displayName) &&
                creator.equals(that.creator) &&
                location.equals(that.location) &&
                Objects.equals(icon, that.icon);

    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("displayName", displayName)
                .add("creator", creator)
                .add("location", location)
                .add("icon", icon)
                .toString();

    }

    @Override
    public @NotNull String name() {
        return name;
    }

    @Override
    public @NotNull Component displayName() {
        return displayName;
    }

    @Override
    public @NotNull UUID creator() {
        return creator;
    }

    @Override
    public @NotNull Location location() {
        return location.clone();
    }

    @Override
    public @Nullable ItemStack icon() {
        return icon == null ? null : icon.clone();
    }







    @Override
    public @NotNull ClanHome.Builder toBuilder() {
        return builder()
                .name(name)
                .displayName(displayName)
                .creator(creator)
                .location(location())
                .icon(icon());
    }

    public static BuilderImpl builder() {
        return new BuilderImpl();
    }

    public static class BuilderImpl implements Builder {

        private String name;
        private Component displayName;
        private UUID creator;
        private Location location;
        private ItemStack icon;

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
        public @NotNull Builder creator(@NotNull UUID creator) {
            this.creator = creator;
            return this;
        }

        @Override
        public @NotNull Builder location(@NotNull Location location) {
            this.location = location;
            return this;
        }

        @Override
        public @NotNull Builder icon(@Nullable ItemStack icon) {
            this.icon = icon;
            return this;
        }

        @Override
        public @NotNull ClanHome build() {
            return new ClanHomeImpl(
                    name,
                    displayName,
                    creator,
                    location.clone(),
                    icon == null ? null : icon.clone()
            );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BuilderImpl builder = (BuilderImpl) o;
            return Objects.equals(name, builder.name) &&
                    Objects.equals(displayName, builder.displayName) &&
                    Objects.equals(creator, builder.creator) &&
                    Objects.equals(location, builder.location) &&
                    Objects.equals(icon, builder.icon);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, displayName, creator, location, icon);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("name", name)
                    .add("displayName", displayName)
                    .add("creator", creator)
                    .add("location", location)
                    .add("icon", icon)
                    .toString();
        }

        @Override
        public void applyEdition(Consumer<HomeEdition> consumer) {
            consumer.accept(new HomeEditionImpl());
        }


        private final class HomeEditionImpl implements HomeEdition {

            @Override
            public HomeEdition setIcon(@Nullable ItemStack icon) {
                BuilderImpl.this.icon(icon);
                return this;
            }

            @Override
            public HomeEdition move(@NotNull Location location) {
                BuilderImpl.this.location(location);
                return this;
            }

            @Override
            public HomeEdition rename(@NotNull Component displayName) {
                BuilderImpl.this.displayName(displayName);
                return this;
            }
        }
    }
}

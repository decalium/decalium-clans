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
import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.edition.home.HomeEdition;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public final class ClanHomeImpl implements ClanHome {
    private final String name;
    private final Component displayName;
    private final UUID creator;
    private final Location location;
    private final ItemStack icon;
    private final int level;

    public ClanHomeImpl(String name, Component component, UUID creator, Location location, ItemStack icon, int level) {
        this.name = name;
        displayName = component;
        this.creator = creator;
        this.location = location;
        this.icon = icon;
        this.level = level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClanHomeImpl clanHome = (ClanHomeImpl) o;
        return level == clanHome.level && name.equals(clanHome.name) && displayName.equals(clanHome.displayName) && creator.equals(clanHome.creator) && location.equals(clanHome.location) && icon.equals(clanHome.icon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, displayName, creator, location, icon, level);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("displayName", displayName)
                .add("creator", creator)
                .add("location", location)
                .add("icon", icon)
                .add("level", level)
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
    public @NotNull ItemStack icon() {
        return icon.clone();
    }

    @Override
    public int level() {
        return level;
    }


    @Override
    public @NotNull ClanHome.Builder toBuilder() {
        return builder()
                .name(name)
                .displayName(displayName)
                .creator(creator)
                .location(location())
                .icon(icon()).level(level);
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
        private int level = 0;

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
        public @NotNull Builder icon(@NotNull ItemStack icon) {
            this.icon = icon;
            return this;
        }

        @Override
        public @NotNull Builder level(int level) {
            this.level = level;
            return this;
        }

        @Override
        public @NotNull ClanHome build() {
            return new ClanHomeImpl(
                    name,
                    displayName,
                    creator,
                    location.clone(),
                    icon.clone(),
                    level
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
                    Objects.equals(icon, builder.icon) &&
                    level == builder.level;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, displayName, creator, location, icon, level);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("name", name)
                    .add("displayName", displayName)
                    .add("creator", creator)
                    .add("location", location)
                    .add("icon", icon)
                    .add("level", level)
                    .toString();
        }

        @Override
        public void applyEdition(Consumer<HomeEdition> consumer) {
            consumer.accept(new HomeEditionImpl());
        }


        private final class HomeEditionImpl implements HomeEdition {

            @Override
            public HomeEdition setIcon(@NotNull ItemStack icon) {
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

            @Override
            public HomeEdition upgrade() {
                BuilderImpl.this.level += 1;
                return this;
            }

            @Override
            public HomeEdition downgrade() {
                Preconditions.checkState(BuilderImpl.this.level >= 0, "Cant downgrade home with 0 level");
                BuilderImpl.this.level -= 1;
                return this;
            }
        }
    }
}

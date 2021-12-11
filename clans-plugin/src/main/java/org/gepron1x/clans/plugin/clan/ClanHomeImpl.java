package org.gepron1x.clans.plugin.clan;

import com.google.common.base.MoreObjects;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.clan.ClanHome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

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
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return displayName;
    }

    @Override
    public @NotNull UUID getCreator() {
        return creator;
    }

    @Override
    public @NotNull Location getLocation() {
        return location.clone();
    }

    @Override
    public @Nullable ItemStack getIcon() {
        return icon == null ? null : icon.clone();
    }







    @Override
    public @NotNull ClanHome.Builder toBuilder() {
        return builder()
                .name(name)
                .displayName(displayName)
                .creator(creator)
                .location(location)
                .icon(icon);
    }

    public static BuilderImpl builder() {
        return new BuilderImpl();
    }

    public static class BuilderImpl implements ClanHome.Builder {

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
                    location,
                    icon
            );
        }
    }
}

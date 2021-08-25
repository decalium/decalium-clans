package org.gepron1x.clans.clan.home;

import com.google.common.base.MoreObjects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.util.Buildable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.storage.property.DefaultProperty;
import org.gepron1x.clans.storage.property.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class ClanHome implements Buildable<ClanHome, ClanHome.Builder> {


    private static class ClanHomeProperty<V> extends DefaultProperty<ClanHome, V> {
        public ClanHomeProperty(String name, Class<V> valueType, Function<ClanHome, V> getter, BiConsumer<ClanHome, V> setter) {
            super(name, ClanHome.class, valueType, getter, setter);
        }
    }
    public static final Property<ClanHome, Component> DISPLAY_NAME = new ClanHomeProperty<>(
            "display_name",
            Component.class,
            home -> home.displayName,
            (home, component) -> home.displayName = component

    );
    public static final Property<ClanHome, Location> LOCATION = new ClanHomeProperty<>(
            "location",
            Location.class,
            home -> home.location,
            (home, location) -> home.location = location
    );
    public static final Property<ClanHome, ItemStack> ICON = new ClanHomeProperty<>(
            "icon",
            ItemStack.class,
            home -> home.icon,
            (home, icon) -> home.icon = icon
    );

    private final UUID owner;
    private final String name;
    private Component displayName;
    private Location location;
    private ItemStack icon;

    public ClanHome(
                    @NotNull UUID owner,
                    @NotNull String name,
                    @NotNull Component displayName,
                    @NotNull Location location,
                    @Nullable ItemStack icon) {
        this.owner = owner;
        this.name = name;
        this.displayName = displayName;
        this.location = location;
        this.icon = icon;
    }
    public ClanHome(@NotNull UUID owner, @NotNull String name, @NotNull Location location) {
        this(owner, name, Component.text(name, NamedTextColor.GRAY), location, new ItemStack(Material.COBBLESTONE));
    }


    public String getName() {
        return name;
    }

    public Component getDisplayName() {
        return displayName;
    }

    public void setDisplayName(Component displayName) {
        DISPLAY_NAME.set(this, displayName);
    }

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        LOCATION.set(this, location);
    }

    public ItemStack getIcon() {
        return icon;
    }

    public void setIcon(ItemStack icon) {
        ICON.set(this, icon);
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getOwner() {
        return owner;
    }
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("location", location)
                .add("displayName", displayName)
                .add("name", name)
                .add("icon", icon)
                .add("owner", owner).toString();
    }

    @Override
    public @NotNull ClanHome.Builder toBuilder() {
        return new Builder()
                .location(location)
                .displayName(displayName)
                .name(name)
                .icon(icon)
                .owner(owner);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClanHome clanHome = (ClanHome) o;
        return owner.equals(clanHome.owner) &&
                name.equals(clanHome.name) &&
                displayName.equals(clanHome.displayName) &&
                location.equals(clanHome.location) &&
                Objects.equals(icon, clanHome.icon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, name, displayName, location, icon);
    }

    public static class Builder implements Buildable.Builder<ClanHome> {
       private Location location;
       private Component displayName;
       private String name;
       private ItemStack icon;
       private UUID owner;
        private Builder() {}
        public Builder location(Location location) {
            this.location = location;
            return this;
        }
        public Builder owner(UUID uuid) {
            this.owner = uuid;
            return this;
        }
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        public Builder displayName(Component displayName) {
            this.displayName = displayName;
            return this;
        }
        public Builder icon(ItemStack icon) {
            this.icon = icon;
            return this;
        }
        public UUID owner() { return owner; }
        public Location location() {return location; }
        public String name() {return name; }
        public ItemStack icon() {return icon; }
        public Component displayName() {return displayName; }

        @Override
        public @NotNull ClanHome build() {
            return new ClanHome(
                    requireNonNull(owner, "owner"),
                    requireNonNull(name, "name"),
                    requireNonNull(displayName, "displayName"),
                    requireNonNull(location, "location"),
                    icon
            );
        }

    }
}

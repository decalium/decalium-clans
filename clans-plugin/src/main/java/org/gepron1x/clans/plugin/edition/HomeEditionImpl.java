package org.gepron1x.clans.plugin.edition;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.edition.HomeEdition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class HomeEditionImpl implements HomeEdition {

    private final ClanHome home;
    private final ClanHome.Builder builder;

    public HomeEditionImpl(@NotNull ClanHome home, @NotNull ClanHome.Builder builder) {

        this.home = home;
        this.builder = builder;
    }

    @Override
    public HomeEdition setIcon(@Nullable ItemStack icon) {
        builder.icon(icon);
        return this;
    }

    @Override
    public HomeEdition setLocation(@NotNull Location location) {
        builder.location(location);
        return this;
    }

    @Override
    public HomeEdition setDisplayName(@NotNull Component displayName) {
        builder.displayName(displayName);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HomeEditionImpl that = (HomeEditionImpl) o;
        return home.equals(that.home) && builder.equals(that.builder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(home, builder);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

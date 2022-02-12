package org.gepron1x.clans.plugin.editor;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.editor.HomeEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class HomeEditorImpl implements HomeEditor {

    private final ClanHome home;
    private final ClanHome.Builder builder;

    public HomeEditorImpl(@NotNull ClanHome home, @NotNull ClanHome.Builder builder) {

        this.home = home;
        this.builder = builder;
    }

    @Override
    public HomeEditor setIcon(@Nullable ItemStack icon) {
        builder.icon(icon);
        return this;
    }

    @Override
    public HomeEditor setLocation(@NotNull Location location) {
        builder.location(location);
        return this;
    }

    @Override
    public HomeEditor setDisplayName(@NotNull Component displayName) {
        builder.displayName(displayName);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HomeEditorImpl that = (HomeEditorImpl) o;
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

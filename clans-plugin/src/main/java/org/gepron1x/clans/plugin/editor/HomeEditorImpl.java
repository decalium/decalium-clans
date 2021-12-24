package org.gepron1x.clans.plugin.editor;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.editor.HomeEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class HomeEditorImpl implements HomeEditor {

    private final ClanHome home;
    private final ClanHome.Builder builder;

    public HomeEditorImpl(@NotNull ClanHome home, @NotNull ClanHome.Builder builder) {

        this.home = home;
        this.builder = builder;
    }
    @Override
    public ClanHome getTarget() {
        return home;
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
}

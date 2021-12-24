package org.gepron1x.clans.plugin.storage.implementation.sql.editor;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.editor.HomeEditor;
import org.jdbi.v3.core.Handle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SqlHomeEditor implements HomeEditor {
    private static final String UPDATE_ICON = "UPDATE homes SET icon=<icon> WHERE name=<name> AND clan_id=<clan_id>";
    private static final String UPDATE_LOCATION = "UPDATE homes SET x=<x>, y=<y>, z=<z>, world=<world> WHERE name=<name> AND clan_id=<clan_id>";
    private static final String UPDATE_DISPLAY_NAME = "UPDATE homes SET display_name=<display_name> WHERE name=<name> AND clan_id=<clan_id>";
    private final Handle handle;
    private final Clan clan;
    private final ClanHome home;

    public SqlHomeEditor(@NotNull Handle handle, @NotNull Clan clan, @NotNull ClanHome home) {

        this.handle = handle;
        this.clan = clan;
        this.home = home;
    }
    @Override
    public ClanHome getTarget() {
        return home;
    }

    @Override
    public HomeEditor setIcon(@Nullable ItemStack icon) {
        handle.createUpdate(UPDATE_ICON)
                .bind("name", home.getName())
                .bind("clan_id", clan.getId())
                .bind("icon", icon);
        return this;
    }

    @Override
    public HomeEditor setLocation(@NotNull Location location) {
        handle.createUpdate(UPDATE_LOCATION)
                .bind("name", home.getName())
                .bind("clan_id", clan.getId())
                .bind("x", location.getBlockX())
                .bind("y", location.getBlockY())
                .bind("z", location.getBlockZ())
                .bind("world", location.getWorld().getName())
                .execute();
        return this;
    }

    @Override
    public HomeEditor setDisplayName(@NotNull Component displayName) {
        handle.createUpdate(UPDATE_DISPLAY_NAME)
                .bind("name", home.getName())
                .bind("clan_id", clan.getId())
                .bind("display_name", displayName)
                .execute();
        return this;
    }
}

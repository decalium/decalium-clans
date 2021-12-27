package org.gepron1x.clans.plugin.storage.implementation.sql.editor;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.editor.HomeEditor;
import org.intellij.lang.annotations.Language;
import org.jdbi.v3.core.Handle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SqlHomeEditor implements HomeEditor {
    @Language("SQL")
    private static final String UPDATE_ICON = "UPDATE `homes` SET `icon`=? WHERE `name`=? AND `clan_id`=?";
    @Language("SQL")
    private static final String UPDATE_LOCATION = "UPDATE `locations` SET `x`=?, `y`=?, `z`=?, `world`=? WHERE `home_id`=(SELECT id FROM homes WHERE `clan_id`=? AND `name`=?)";
    @Language("SQL")
    private static final String UPDATE_DISPLAY_NAME = "UPDATE `homes` SET `display_name`=? WHERE `name`=? AND `clan_id`=?";
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
                .bind(1, home.getName())
                .bind(2, clan.getId())
                .bind(0, icon);
        return this;
    }

    @Override
    public HomeEditor setLocation(@NotNull Location location) {
        handle.createUpdate(UPDATE_LOCATION)
                .bind(5, home.getName())
                .bind(4, clan.getId())
                .bind(0, location.getBlockX())
                .bind(1, location.getBlockY())
                .bind(2, location.getBlockZ())
                .bind(3, location.getWorld().getName())
                .execute();
        return this;
    }

    @Override
    public HomeEditor setDisplayName(@NotNull Component displayName) {
        handle.createUpdate(UPDATE_DISPLAY_NAME)
                .bind(1, home.getName())
                .bind(2, clan.getId())
                .bind(0, displayName)
                .execute();
        return this;
    }
}

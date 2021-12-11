package org.gepron1x.clans.api.editor;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.clan.ClanHome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HomeEditor extends Editor<ClanHome> {
    HomeEditor setIcon(@Nullable ItemStack icon);
    HomeEditor setLocation(@NotNull Location location);
    HomeEditor setDisplayName(@NotNull Component displayName);
}

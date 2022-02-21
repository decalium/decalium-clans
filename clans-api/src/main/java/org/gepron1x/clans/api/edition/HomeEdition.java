package org.gepron1x.clans.api.edition;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.clan.ClanHome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HomeEdition extends Edition<ClanHome> {

    @Override
    @NotNull
    default Class<ClanHome> getTarget() { return ClanHome.class; }

    HomeEdition setIcon(@Nullable ItemStack icon);
    HomeEdition setLocation(@NotNull Location location);
    HomeEdition setDisplayName(@NotNull Component displayName);
}

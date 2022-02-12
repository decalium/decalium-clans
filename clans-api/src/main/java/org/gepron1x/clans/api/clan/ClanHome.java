package org.gepron1x.clans.api.clan;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.util.Buildable;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ClanHome extends Buildable<ClanHome, ClanHome.Builder>, ComponentLike {
    @Override
    @NotNull
    default Component asComponent() {
        return getDisplayName();
    }

    @NotNull String getName();
    @NotNull Component getDisplayName();
    @NotNull UUID getCreator();
    @NotNull Location getLocation();
    @Nullable ItemStack getIcon();



    interface Builder extends Buildable.Builder<ClanHome> {
        @Contract("_ -> this")
        @NotNull Builder name(@NotNull String name);

        @Contract("_ -> this")
        @NotNull Builder displayName(@NotNull Component displayName);

        @Contract("_ -> this")
        @NotNull Builder creator(@NotNull UUID creator);

        @Contract("_ -> this")
        @NotNull Builder location(@NotNull Location location);

        @Contract("_ -> this")
        @NotNull Builder icon(@Nullable ItemStack icon);




    }
}

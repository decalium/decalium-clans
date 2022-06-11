package org.gepron1x.clans.api.clan.home;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.util.Buildable;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.edition.EditionApplicable;
import org.gepron1x.clans.api.edition.home.HomeEdition;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ClanHome extends Buildable<ClanHome, ClanHome.Builder>, ComponentLike {
    @Override
    @NotNull
    default Component asComponent() {
        return displayName();
    }

    @NotNull String name();
    @NotNull Component displayName();
    @NotNull UUID creator();
    @NotNull Location location();
    @Nullable ItemStack icon();



    interface Builder extends Buildable.Builder<ClanHome>, EditionApplicable<ClanHome, HomeEdition> {
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

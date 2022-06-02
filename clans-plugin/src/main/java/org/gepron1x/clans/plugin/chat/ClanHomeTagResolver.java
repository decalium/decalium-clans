package org.gepron1x.clans.plugin.chat;

import com.google.common.base.Strings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.gepron1x.clans.api.clan.ClanHome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ClanHomeTagResolver(@NotNull ClanHome clanHome) implements TagResolver.WithoutArguments {

    private static final String NAME = "name";
    private static final String DISPLAY_NAME = "display_name";
    private static final String OWNER_UUID = "owner_uuid";
    private static final String OWNER_NAME = "owner_name";
    private static final String LOCATION_X = "location_x";
    private static final String LOCATION_Y = "location_y";
    private static final String LOCATION_Z = "location_z";
    private static final String LOCATION_WORLD = "location_world";

    private static final String ICON = "icon";

    public static ClanHomeTagResolver home(@NotNull ClanHome home) {
        return new ClanHomeTagResolver(home);
    }


    @Override
    public @Nullable Tag resolve(@NotNull String name) {
        Component component = switch(name) {
            case NAME -> Component.text(clanHome.name());
            case DISPLAY_NAME -> clanHome.displayName();
            case OWNER_UUID -> Component.text(clanHome.creator().toString());
            case OWNER_NAME -> Component.text(Strings.nullToEmpty(Bukkit.getOfflinePlayer(clanHome.creator()).getName()));
            case LOCATION_X -> Component.text(clanHome.location().getBlockX());
            case LOCATION_Y -> Component.text(clanHome.location().getBlockY());
            case LOCATION_Z -> Component.text(clanHome.location().getBlockZ());
            case LOCATION_WORLD -> Component.text(clanHome.location().getWorld().getName());
            case ICON -> Component.text("[]").hoverEvent(clanHome.icon());
            default -> null;
        };
        return component == null ? null : Tag.inserting(component);
    }
}

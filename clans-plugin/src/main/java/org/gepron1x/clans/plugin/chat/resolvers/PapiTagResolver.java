package org.gepron1x.clans.plugin.chat.resolvers;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PapiTagResolver implements TagResolver.WithoutArguments {

    private final OfflinePlayer player;

    public PapiTagResolver(@Nullable OfflinePlayer player) {

        this.player = player;
    }


    @Override
    public @Nullable Tag resolve(@NotNull String name) {
        if(!isPlaceholder(name)) return null;
        String result = PlaceholderAPI.setPlaceholders(this.player, name);

        Component component;
        if(result.indexOf(LegacyComponentSerializer.SECTION_CHAR) != -1) {
            component = LegacyComponentSerializer.legacyAmpersand().deserialize(result);
        } else {
            component = Component.text(result);
        }

        return Tag.inserting(component);
    }

    private boolean isPlaceholder(String name) {
       return name.charAt(0) == '%' && name.charAt(name.length() - 1) == '%';
    }

    @Override
    public boolean has(@NotNull String name) {
        return isPlaceholder(name);
    }
}

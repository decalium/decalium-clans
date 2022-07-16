package org.gepron1x.clans.plugin.chat.resolvers;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PapiTagResolver implements TagResolver {

    private final OfflinePlayer player;

    public PapiTagResolver(@Nullable OfflinePlayer player) {

        this.player = player;
    }




    private boolean isPlaceholder(String name) {
       return name.equals("papi");
    }

    @Override
    public @Nullable Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) throws ParsingException {
        String str = arguments.popOr("Define the placeholder you wanna to use.").value();
        String percents = "%" + str + "%";
        String result = PlaceholderAPI.setPlaceholders(this.player, percents);
        if(result.equals(percents)) return null;
        Component component;
        if(result.indexOf(LegacyComponentSerializer.SECTION_CHAR) != -1) {
            component = LegacyComponentSerializer.legacyAmpersand().deserialize(result);
        } else {
            component = Component.text(result);
        }

        return Tag.inserting(component);
    }

    @Override
    public boolean has(@NotNull String name) {
        return isPlaceholder(name);
    }
}

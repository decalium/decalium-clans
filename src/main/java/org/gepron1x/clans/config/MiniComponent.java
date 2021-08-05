package org.gepron1x.clans.config;


import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class MiniComponent implements ComponentLike {
    private final MiniMessage miniMessage;
    private final String value;

    public MiniComponent(String value, MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
        this.value = value;
    }
    public MiniComponent(String value) {
        this(value, MiniMessage.get());
    }

    public Component parse(@Nullable Player player, Object... templates) {
        String toParse = player == null ? value : PlaceholderAPI.setPlaceholders(player, value);
        return miniMessage.parse(toParse, templates);

    }
    public Component parse(Object... templates) {
        return parse(null, templates);
    }

    public String getValue() {
        return value;
    }

    @Override
    public @NonNull Component asComponent() {
        return parse();
    }
}

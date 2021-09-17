package org.gepron1x.clans.config;


import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
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
        return miniMessage.parse(papi(player, value), templates);

    }
    public Component parse(Object... templates) {
        return miniMessage.parse(value, templates);
    }

    public Component parse(@Nullable Player player, Template... templates) {
        return miniMessage.parse(papi(player, value), templates);
    }
    public Component parse(Template... templates) {
        return miniMessage.parse(value, templates);
    }
    public Component parse(Collection<? extends Template> templates) {
        return miniMessage.parse(value, List.copyOf(templates));
    }
    public MiniComponent withPlaceholder(Template template) {
        return new MutableMiniComponent(value, miniMessage, template);
    }
    public MiniComponent withPlaceholder(String key, String value) {
        return withPlaceholder(Template.of(key, value));
    }
    public MiniComponent withPlaceholder(String key, Component value) {
        return withPlaceholder(Template.of(key, value));
    }
    public MiniComponent withPlaceholder(String key, ComponentLike like) {
        return withPlaceholder(key, like.asComponent());
    }


    private static String papi(@Nullable Player player, String input) {
        return player == null ? input : PlaceholderAPI.setPlaceholders(player, input);
    }

    public String getValue() {
        return value;
    }

    @Override
    public @NonNull @NotNull Component asComponent() {
        return parse();
    }

}

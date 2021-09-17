package org.gepron1x.clans.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MutableMiniComponent extends MiniComponent {
    private final List<Template> templates;
    public MutableMiniComponent(String value, MiniMessage miniMessage, Collection<Template> placeholders) {
        super(value, miniMessage);
        this.templates = new ArrayList<>(placeholders);
    }
    public MutableMiniComponent(String value, MiniMessage miniMessage, Template... placeholders) {
        this(value, miniMessage, Arrays.asList(placeholders));
    }

    public MutableMiniComponent(String value, MiniMessage miniMessage) {
        this(value, miniMessage, Collections.emptyList());
    }

    @Override
    public MiniComponent withPlaceholder(Template template) {
        templates.add(template);
        return this;
    }

    @Override
    public @NonNull @NotNull Component asComponent() {
        return parse(templates);
    }
}

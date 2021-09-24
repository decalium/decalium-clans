package org.gepron1x.clans.config;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Message implements ComponentLike {
    private final List<Template> placeholders;
    private final MiniMessage miniMessage;
    private final String value;

    public Message(String value, MiniMessage miniMessage, Collection<Template> placeholders) {
        this(value, miniMessage, new ArrayList<>(placeholders));
    }
    public Message(String value, MiniMessage miniMessage) {
        this(value, miniMessage, Collections.emptyList());
    }
    private Message(String value, MiniMessage miniMessage, List<Template> placeholders) {
        this.value = value;
        this.miniMessage = miniMessage;
        this.placeholders = placeholders;
    }
    public Message(String value) {
        this(value, MiniMessage.get());
    }




    public Component parse(Template... templates) {
        return parse(Arrays.asList(templates));
    }
    public Component parse(Collection<? extends Template> templates) {
        ArrayList<Template> temporary = new ArrayList<>(templates);
        temporary.addAll(placeholders);
        return miniMessage.parse(value, temporary);
    }
    public Component parse() {
        return miniMessage.parse(value, placeholders);
    }

    public Message with(Template template) {
        ArrayList<Template> temporary = new ArrayList<>(placeholders);
        temporary.add(template);
        return new Message(value, miniMessage, temporary);
    }

    public Message with(Collection<Template> templates) {
        if(templates.isEmpty()) return this;
        ArrayList<Template> temporary = new ArrayList<>(templates);
        temporary.addAll(placeholders);
        return new Message(value, miniMessage, temporary);
    }

    public Message with(Template... templates) {
        return with(Arrays.asList(templates));
    }
    public Message with(String key, String value) {
        return with(Template.of(key, value));
    }
    public Message with(String key, Component value) {
        return with(Template.of(key, value));
    }
    public Message with(String key, ComponentLike like) {
        return with(key, like.asComponent());
    }




    public String getValue() {
        return value;
    }

    @Override
    public @NonNull Component asComponent() {
        return parse();
    }

}

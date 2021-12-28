package org.gepron1x.clans.util;

import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record Message(MiniMessage miniMessage,
                      String value) implements ComponentLike, TemplateHolder<Message.Container> {

    @Override
    public @NotNull Component asComponent() {
        return miniMessage.deserialize(value);
    }

    @Override
    public Container with(Template template) {
        return with(Collections.singleton(template));
    }

    @Override
    public Container with(Iterable<Template> templates) {

        if (templates instanceof Collection<Template> collection) {
            return with(collection);
        }

        return with(Lists.newArrayList(templates));
    }

    public static Message message(@NotNull String value, @NotNull MiniMessage miniMessage) {
        return new Message(miniMessage, value);
    }

    public static Message message(@NotNull String value) {
        return message(value, MiniMessage.miniMessage());
    }

    @Override
    public Container with(Collection<Template> templates) {
        return new Container(value, miniMessage, templates);
    }


    public static final class Container implements ComponentLike, TemplateHolder<Container>, TemplateResolver {
        private final String value;
        private final MiniMessage miniMessage;
        private final Map<String, Template> templates;

        private Container(String value, MiniMessage miniMessage, Collection<Template> templates) {
            this.value = value;
            this.miniMessage = miniMessage;
            this.templates = new HashMap<>(templates.size());
            with(templates);
        }

        @Override
        public @NotNull Component asComponent() {
            return miniMessage.deserialize(value, this);
        }


        @Override
        public Container with(Template template) {
            this.templates.put(template.key(), template);
            return this;
        }

        @Override
        public Container with(Iterable<Template> templates) {
            if (templates instanceof Collection<Template> collection) return with(collection);
            for (Template template : templates) this.templates.put(template.key(), template);
            return this;
        }

        @Override
        public Container with(Collection<Template> templates) {
            for(Template template : templates) this.templates.put(template.key(), template);
            return this;
        }

        @Override
        public boolean canResolve(@NotNull String key) {
            return templates.containsKey(key);
        }

        @Override
        public @Nullable Template resolve(@NotNull String key) {
            return templates.get(key);
        }
    }


}

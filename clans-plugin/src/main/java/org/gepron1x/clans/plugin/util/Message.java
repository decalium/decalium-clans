package org.gepron1x.clans.plugin.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.placeholder.Placeholder;
import net.kyori.adventure.text.minimessage.placeholder.PlaceholderResolver;
import net.kyori.adventure.text.minimessage.placeholder.Replacement;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record Message(MiniMessage miniMessage,
                      String value) implements ComponentLike, WithPlaceholders<Message.Container> {

    public static Message message(@NotNull String value, @NotNull MiniMessage miniMessage) {
        return new Message(miniMessage, value);
    }

    public static Message message(@NotNull String value) {
        return message(value, MiniMessage.miniMessage());
    }

    @Override
    public @NotNull Component asComponent() {
        return miniMessage.deserialize(value);
    }

    @Override
    public Container with(Placeholder<?> placeholder) {
        return with(Collections.singleton(placeholder));
    }

    @Override
    public Container with(Iterable<? extends Placeholder<?>> placeholders) {
        Iterator<? extends Placeholder<?>> iterator = placeholders.iterator();
        Container container = with(iterator.next());
        while(iterator.hasNext()) container.with(iterator.next());
        return container;
    }

    @Override
    public Container with(Collection<? extends Placeholder<?>> templates) {
        return new Container(value, miniMessage, Collections.emptyList(), templates);
    }

    @Override
    public Container with(PlaceholderResolver placeholderResolver) {
        return new Container(value, miniMessage, Collections.singletonList(placeholderResolver), Collections.emptySet());
    }


    public static final class Container implements ComponentLike, WithPlaceholders<Container> {
        private final String value;
        private final MiniMessage miniMessage;
        private final PlaceholderResolver resolver;
        private final List<PlaceholderResolver> resolvers;
        private final Map<String, Replacement<?>> replacements;

        private Container(String value, MiniMessage miniMessage, List<? extends PlaceholderResolver> resolvers, Collection<? extends Placeholder<?>> templates) {
            this.value = value;
            this.miniMessage = miniMessage;
            this.replacements = new HashMap<>(templates.size());
            with(templates);
            this.resolvers = new ArrayList<>(resolvers);
            this.resolver = PlaceholderResolver.combining(PlaceholderResolver.map(this.replacements), PlaceholderResolver.combining(this.resolvers));
        }

        @Override
        public @NotNull Component asComponent() {
            return miniMessage.deserialize(value, resolver);
        }



        @Override
        public Container with(Placeholder<?> placeholder) {
            this.replacements.put(placeholder.key(), placeholder);
            return this;
        }

        @Override
        public Container with(Iterable<? extends Placeholder<?>> placeholders) {
            for(Placeholder<?> placeholder : placeholders) {
                with(placeholder);
            }
            return this;
        }

        @Override
        public Container with(Collection<? extends Placeholder<?>> placeholders) {
            for(Placeholder<?> placeholder : placeholders) {
                with(placeholder);
            }
            return this;
        }

        @Override
        public Container with(PlaceholderResolver placeholderResolver) {
            this.resolvers.add(placeholderResolver);
            return this;
        }


    }


}

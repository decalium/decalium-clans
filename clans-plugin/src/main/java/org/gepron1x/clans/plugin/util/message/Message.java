package org.gepron1x.clans.plugin.util.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public record Message(MiniMessage miniMessage,
                      String value) implements ComponentLike, Formatted<Message.Container> {

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
    public Container with(TagResolver tagResolver) {
        return new Container(value, miniMessage, TagResolver.builder().resolver(tagResolver));
    }

    @Override
    public Container with(String key, Tag tag) {
        return new Container(value, miniMessage, TagResolver.builder().tag(key, tag));
    }

    @Override
    public Container with(Collection<? extends TagResolver> resolvers) {
        return new Container(value, miniMessage, TagResolver.builder().resolvers(resolvers));
    }


    public static final class Container implements Formatted<Container>, ComponentLike {

        private final String value;
        private final MiniMessage miniMessage;
        private final TagResolver.Builder builder;

        private Container(String value, MiniMessage miniMessage, TagResolver.Builder builder) {
            this.value = value;
            this.miniMessage = miniMessage;
            this.builder = builder;
        }

        @Override
        public @NotNull Component asComponent() {
            return this.miniMessage.deserialize(this.value,
                   builder.build()
            );

        }

        @Override
        public Container with(TagResolver tagResolver) {
            this.builder.resolver(tagResolver);
            return this;
        }

        @Override
        public Container with(Collection<? extends TagResolver> resolvers) {
            this.builder.resolvers(resolvers);
            return this;
        }

        @Override
        public Container with(String key, Tag tag) {
            this.builder.tag(key, tag);
            return this;
        }
    }




}

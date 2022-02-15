package org.gepron1x.clans.plugin.util.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record Message(MiniMessage miniMessage,
                      String value) implements ComponentLike, WithTags<Message.Container> {

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
        ArrayList<TagResolver> resolvers = new ArrayList<>(1);
        resolvers.add(tagResolver);
        return new Container(value, miniMessage, resolvers);
    }

    @Override
    public Container with(Collection<? extends TagResolver> resolvers) {
        return new Container(value, miniMessage, new ArrayList<>(resolvers));
    }


    public static final class Container implements WithTags<Container>, ComponentLike {

        private final String value;
        private final MiniMessage miniMessage;
        private final List<TagResolver> resolvers;

        private Container(String value, MiniMessage miniMessage, List<TagResolver> resolvers) {
            this.value = value;
            this.miniMessage = miniMessage;
            this.resolvers = resolvers;
        }

        @Override
        public @NotNull Component asComponent() {
            return this.miniMessage.deserialize(this.value,
                   TagResolver.resolver(resolvers)
            );

        }

        @Override
        public Container with(TagResolver tagResolver) {
            this.resolvers.add(tagResolver);
            return this;
        }

        @Override
        public Container with(Collection<? extends TagResolver> resolvers) {
            this.resolvers.addAll(resolvers);
            return this;
        }
    }




}

package org.gepron1x.clans.plugin.util.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.Arrays;
import java.util.Collection;

public interface Formatted<T extends Formatted<T>> { // i have no clue how to call this


    T with(TagResolver tagResolver);

    T with(String key, Tag tag);


    T with(Collection<? extends TagResolver> resolvers);


    default T with(TagResolver... resolvers) {
        return with(Arrays.asList(resolvers));
    }



    default T with(String key, ComponentLike like) {
        return with(Placeholder.component(key, like));
    }

    default T withMiniMessage(String key, String value) {
        return with(Placeholder.parsed(key, value));
    }


    default T with(String key, String value) {
        return with(key, Component.text(value));
    }

    default T with(String key, CharSequence sequence) {
        return with(key, sequence.toString());
    }

    default T with(String key, int value) {
        return with(key, Component.text(value));
    }

    default T with(String key, double value) {
        return with(key, Component.text(value));
    }

    default T with(String key, float value) {
        return with(key, Component.text(value));
    }

    default T with(String key, short value) {
        return with(key, Component.text(value));
    }

    default T with(String key, long value) {
        return with(key, Component.text(value));
    }

    default T with(String key, boolean value) {
        return with(key, Component.text(value));
    }

    default T with(String key, char value) {
        return with(key, Component.text(value));
    }

}

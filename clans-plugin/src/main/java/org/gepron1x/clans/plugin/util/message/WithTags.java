package org.gepron1x.clans.plugin.util.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.Collection;
import java.util.List;

public interface WithTags<T extends WithTags<T>> { // i have no clue how to call this


    T with(TagResolver tagResolver);

    default T with(String key, Tag tag) {
        return with(TagResolver.resolver(key, tag));
    }

    T with(Collection<? extends TagResolver> resolvers);


    default T with(TagResolver... resolvers) {
        return with(List.of(resolvers));
    }



    default T with(String key, ComponentLike like) {
        return with(key, Tag.inserting(like));
    }

    default T withMiniMessage(String key, String value) {
        return with(key, Tag.preProcessParsed(value));
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

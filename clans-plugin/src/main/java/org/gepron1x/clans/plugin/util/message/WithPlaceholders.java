package org.gepron1x.clans.plugin.util.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.placeholder.Placeholder;
import net.kyori.adventure.text.minimessage.placeholder.PlaceholderResolver;

import java.util.Collection;
import java.util.List;

public interface WithPlaceholders<T extends WithPlaceholders<T>> { // i have no clue how to call this


    T with(Placeholder<?> placeholder);

    T with(Iterable<? extends Placeholder<?>> placeholders);

    T with(Collection<? extends Placeholder<?>> templates);

    T with(PlaceholderResolver placeholderResolver);

    default T with(Placeholder<?>... templates) {
        return with(List.of(templates));
    }



    default T with(String key, ComponentLike like) {
        return with(Placeholder.component(key, like));
    }


    default T withMiniMessage(String key, String value) {
        return with(Placeholder.miniMessage(key, value));
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

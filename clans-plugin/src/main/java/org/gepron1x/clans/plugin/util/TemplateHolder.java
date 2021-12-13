package org.gepron1x.clans.plugin.util;

import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.Template;

import java.util.Collection;

public interface TemplateHolder<T extends TemplateHolder<T>> {
    T with(Template template);

    T with(Iterable<Template> templates);

    T with(Collection<Template> templates);

    default T with(String key, ComponentLike like) {
        return with(Template.template(key, like));
    }

    default T with(String key, String value) {
        return with(Template.template(key, value));
    }

    default T with(String key, int value) {
        return with(key, String.valueOf(value));
    }

    default T with(String key, double value) {
        return with(key, String.valueOf(value));
    }

    default T with(String key, float value) {
        return with(key, String.valueOf(value));
    }

    default T with(String key, short value) {
        return with(key, String.valueOf(value));
    }

    default T with(String key, long value) {
        return with(key, String.valueOf(value));
    }

    default T with(String key, boolean value) {
        return with(key, String.valueOf(value));
    }

    default T with(String key, char value) {
        return with(key, String.valueOf(value));
    }

}

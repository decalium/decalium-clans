/*
 * decalium-clans
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.api.chat.action;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.gepron1x.clans.api.chat.BooleanStateResolver;
import org.gepron1x.clans.api.chat.PrefixedTagResolver;

import java.util.Arrays;
import java.util.Collection;

public interface Formatted<T extends Formatted<T>> { // i have no clue how to call this

    default T with(String prefix, TagResolver resolver) {
        return with(PrefixedTagResolver.prefixed(resolver, prefix));
    }
    T with(TagResolver tagResolver);

    T with(String key, Tag tag);


    T with(Collection<? extends TagResolver> resolvers);


    default T with(TagResolver... resolvers) {
        return with(Arrays.asList(resolvers));
    }



    default T with(String key, ComponentLike like) {
        return with(key, like.asComponent());
    }

    default T with(String key, Component component) {
        return with(key, Tag.selfClosingInserting(component));
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

	default T booleanState(String key, boolean value) {
		return with(new BooleanStateResolver(key, value));
	}

}

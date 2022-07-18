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
package org.gepron1x.clans.api.chat;

import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PrefixedTagResolver implements TagResolver.WithoutArguments {
    private final String prefix;
    private final TagResolver.WithoutArguments parent;

    public static PrefixedTagResolver prefixed(TagResolver.WithoutArguments resolver, String prefix) {
        return new PrefixedTagResolver(prefix, resolver);

    }

    public PrefixedTagResolver(String prefix, TagResolver.WithoutArguments parent) {
        this.prefix = prefix;
        this.parent = parent;
    }


    @Override
    public @Nullable Tag resolve(@NotNull String name) {
        String underscored = underscored();
        if(!name.startsWith(underscored)) return null;
        return parent.resolve(name.substring(underscored.length()));
    }

    @Override
    public boolean has(@NotNull String name) {
        String underscored = underscored();
        if(!name.startsWith(underscored)) return false;
        return parent.has(name.substring(underscored.length()));
    }

    private String underscored() {
        return prefix + "_";
    }
}

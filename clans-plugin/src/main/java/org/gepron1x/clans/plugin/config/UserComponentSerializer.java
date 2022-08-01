/*
 * decalium-clans-rewrite
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans-rewrite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans-rewrite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans-rewrite. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.plugin.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.gepron1x.clans.plugin.util.MixedComponentSerializer;
import org.jetbrains.annotations.NotNull;

public enum UserComponentSerializer implements ComponentSerializer<Component, Component, String> {

    LEGACY(LegacyComponentSerializer.legacyAmpersand()),
    MINI_MESSAGE(MiniMessage.builder().tags(TagResolver.resolver(StandardTags.color(), StandardTags.decorations(), StandardTags.gradient(), StandardTags.rainbow())).build()),
    MIXED(new MixedComponentSerializer(MiniMessage.miniMessage(), LegacyComponentSerializer.legacyAmpersand()));


    private final ComponentSerializer<Component, ? extends Component, String> serializer;

    UserComponentSerializer(ComponentSerializer<Component, ? extends Component, String> serializer) {
        this.serializer = serializer;
    }

    @NotNull
    @Override
    public Component deserialize(@NotNull String input) {
        return this.serializer.deserialize(input);
    }

    @Override
    public @NotNull String serialize(@NotNull Component component) {
        return this.serializer.serialize(component);
    }
}

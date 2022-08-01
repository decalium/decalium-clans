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
package org.gepron1x.clans.plugin.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

public final class MixedComponentSerializer implements ComponentSerializer<Component, Component, String> {

    private final MiniMessage miniMessage;
    private final LegacyComponentSerializer legacySerializer;

    public MixedComponentSerializer(MiniMessage miniMessage, LegacyComponentSerializer legacySerializer) {

        this.miniMessage = miniMessage;
        this.legacySerializer = legacySerializer;
    }
    @Override
    public @NotNull Component deserialize(@NotNull String input) {
        input = miniMessage.serialize(legacySerializer.deserialize(input));
        return miniMessage.deserialize(input);
    }

    @Override
    public @NotNull String serialize(@NotNull Component component) {
        return miniMessage.serialize(component);
    }
}

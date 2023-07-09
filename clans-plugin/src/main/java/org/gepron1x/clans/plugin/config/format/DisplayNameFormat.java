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
package org.gepron1x.clans.plugin.config.format;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import space.arim.dazzleconf.annote.ConfKey;

import java.util.regex.Pattern;

import static space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;
import static space.arim.dazzleconf.annote.ConfDefault.DefaultString;

public interface DisplayNameFormat {
    @DefaultString("[a-zA-Z0-9а-яА-Я]")
    @ConfKey("allowed-tag-characters")
    Pattern allowedTagCharacters();

	@DefaultString("[a-z0-9]{3,8}")
	Pattern tagRegex();

    @DefaultInteger(3)
    @ConfKey("min-tag-size")
    int minTagSize();

    default String formatTag(Component component) {
        String text = PlainTextComponentSerializer.plainText().serialize(component);
        text = text.replace(' ', '_');
        Pattern allowedChars = allowedTagCharacters();
        StringBuilder builder = new StringBuilder();
        for(char c : text.toCharArray()) {
            if(allowedChars.matcher(String.valueOf(c)).matches()) {
                builder.append(c);
            }
        }
        return builder.substring(0, Math.min(builder.length(), 16));
    }

}

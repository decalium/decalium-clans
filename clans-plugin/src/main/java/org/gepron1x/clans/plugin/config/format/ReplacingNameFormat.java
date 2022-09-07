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
package org.gepron1x.clans.plugin.config.format;


import com.google.common.base.MoreObjects;

import java.util.Objects;
import java.util.regex.Pattern;

public final class ReplacingNameFormat implements DisplayNameFormat {

    private final Pattern allowedChars;
    private final char replacement;

    public ReplacingNameFormat(Pattern allowedChars, char replacement) {

        this.allowedChars = allowedChars;
        this.replacement = replacement;
    }
    @Override
    public boolean valid(String tag) {
        return true;
    }

    @Override
    public String formatTag(String tag) {
        StringBuilder builder = new StringBuilder(tag.length());
        for(char c : tag.toCharArray()) {
            char append = allowedChars.matcher(String.valueOf(c)).matches() ? c : replacement;
            builder.append(append);
        }

        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReplacingNameFormat that = (ReplacingNameFormat) o;
        return replacement == that.replacement && allowedChars.equals(that.allowedChars);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allowedChars, replacement);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("allowedChars", allowedChars)
                .add("replacement", replacement)
                .toString();
    }
}

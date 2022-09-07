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

public final class SizeCheckingFormat implements DisplayNameFormat {

    private final int minSize;
    private int maxSize;

    public SizeCheckingFormat(int minSize, int maxSize) {

        this.minSize = minSize;
        this.maxSize = maxSize;
    }
    @Override
    public boolean valid(String tag) {
        int len = tag.length();
        return len >= minSize && len <= maxSize;
    }

    @Override
    public String formatTag(String tag) {
        if(!valid(tag)) throw new IllegalArgumentException("Display name is too big");
        return tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SizeCheckingFormat that = (SizeCheckingFormat) o;
        return minSize == that.minSize && maxSize == that.maxSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minSize, maxSize);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("minSize", minSize)
                .add("maxSize", maxSize)
                .toString();
    }
}

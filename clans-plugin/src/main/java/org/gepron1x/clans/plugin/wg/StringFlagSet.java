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
package org.gepron1x.clans.plugin.wg;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import java.util.Map;
import java.util.Objects;

public final class StringFlagSet implements FlagSet {

    private final Map<String, String> flags;

    public StringFlagSet(Map<String, String> flags) {

        this.flags = flags;
    }
    @Override
    public void apply(ProtectedRegion region) {
        setFlags(region, flags);
    }

    @Override
    public void clear(ProtectedRegion region) {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        flags.keySet().stream().map(registry::get).filter(Objects::nonNull).forEach(f -> region.setFlag(f, null));

    }

    @Override
    public Map<String, ?> serialize() {
        return flags;
    }

    public static void setFlags(ProtectedRegion region, Map<String, String> values) {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        values.forEach((key, value) -> {
            Flag<?> flag = registry.get(key);
            if(flag == null) return;
            setFlag(region, flag, value);
        });
    }

    private static <V> void setFlag(ProtectedRegion region, Flag<V> flag, String value) {
        try {
            region.setFlag(flag, flag.parseInput(FlagContext.create().setInput(value).build()));
        } catch (InvalidFlagFormat e) {
            throw new RuntimeException(e);
        }
    }
}

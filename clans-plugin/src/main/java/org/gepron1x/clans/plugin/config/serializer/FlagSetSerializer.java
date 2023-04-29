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
package org.gepron1x.clans.plugin.config.serializer;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import org.gepron1x.clans.plugin.wg.FlagSet;
import org.gepron1x.clans.plugin.wg.FlagSetImpl;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

import java.util.Map;

public final class FlagSetSerializer implements ValueSerialiser<FlagSet> {
    @Override
    public Class<FlagSet> getTargetClass() {
        return FlagSet.class;
    }

    @Override
    public FlagSet deserialise(FlexibleType flexibleType) throws BadValueException {
		return new FlagSetImpl(flexibleType.getMap((key, value) -> {
			Flag<?> flag = WorldGuard.getInstance().getFlagRegistry().get(key.getString());
			if(flag == null) throw flexibleType.badValueExceptionBuilder().message("Unknown flag" + key.getString()).build();
			try {
				Object o = flag.parseInput(FlagContext.create().setInput(value.getString()).build());
				return Map.entry(flag, o);
			} catch (InvalidFlagFormat e) {
				throw flexibleType.badValueExceptionBuilder().cause(e).message("Failed to parse flag input for "+key.getString()).build();
			}
		}));
    }

    @Override
    public Object serialise(FlagSet value, Decomposer decomposer) {
        return value.serialize();
    }
}

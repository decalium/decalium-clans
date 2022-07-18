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

import org.gepron1x.clans.api.clan.member.ClanPermission;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

public final class ClanPermissionSerializer implements ValueSerialiser<ClanPermission> {
    @Override
    public Class<ClanPermission> getTargetClass() {
        return ClanPermission.class;
    }

    @Override
    public ClanPermission deserialise(FlexibleType flexibleType) throws BadValueException {
        return new ClanPermission(flexibleType.getString());
    }

    @Override
    public String serialise(ClanPermission value, Decomposer decomposer) {
        return value.value();
    }
}

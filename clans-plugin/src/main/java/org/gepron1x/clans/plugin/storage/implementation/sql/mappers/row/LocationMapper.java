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
package org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.gepron1x.clans.plugin.storage.implementation.sql.PrefixedRowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class LocationMapper extends PrefixedRowMapper<Location> {

    private static final double OFFSET = 0.5F;

    private static final String WORLD = "world", X = "x", Y= "y", Z= "z";

    private final Server server;

    public LocationMapper(@NotNull Server server, @Nullable String prefix) {
        super(prefix);
        this.server = server;
    }

    @Override
    public Location map(ResultSet rs, StatementContext ctx) throws SQLException {

        World world = server.getWorld(rs.getString(prefixed(WORLD)));
        int x = rs.getInt(prefixed(X));
        int y = rs.getInt(prefixed(Y));
        int z = rs.getInt(prefixed(Z));

        return new Location(world, x + OFFSET, y + OFFSET, z + OFFSET);
    }
}

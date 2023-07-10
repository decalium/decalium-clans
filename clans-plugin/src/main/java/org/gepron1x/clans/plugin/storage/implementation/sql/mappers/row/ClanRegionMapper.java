/*
 * decalium-clans
 * Copyright © 2023 George Pronyuk <https://vk.com/gpronyuk>
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
import org.bukkit.World;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.shield.ClanRegion;
import org.gepron1x.clans.api.shield.Shield;
import org.gepron1x.clans.plugin.shield.ShieldImpl;
import org.gepron1x.clans.plugin.shield.region.RegionImpl;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public final class ClanRegionMapper implements RowMapper<ClanRegion> {


	public ClanRegionMapper() {
	}

	@Override
	public ClanRegion map(ResultSet rs, StatementContext ctx) throws SQLException {
		Location location = new Location(
				ctx.findColumnMapperFor(World.class).orElseThrow().map(rs, "world", ctx),
				rs.getInt("x"),
				rs.getInt("y"),
				rs.getInt("z")
		);
		ClanReference reference = ctx.findColumnMapperFor(ClanReference.class).orElseThrow().map(rs, "clan_tag", ctx);
		Timestamp start = rs.getTimestamp("start");
		Timestamp end = rs.getTimestamp("end");
		Shield shield = start == null ? Shield.NONE : new ShieldImpl(start.toInstant(), end.toInstant());
		return new RegionImpl(rs.getInt("id"), reference, location, shield);
	}
}

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
package org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row;

import org.gepron1x.clans.api.shield.Shield;
import org.gepron1x.clans.plugin.shield.ShieldImpl;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public final class ShieldRowMapper implements RowMapper<Shield> {

    private static final String START = "start", END = "end";
    @Override
    public Shield map(ResultSet rs, StatementContext ctx) throws SQLException {
        ColumnMapper<Instant> mapper = ctx.findColumnMapperFor(Instant.class).orElseThrow();
        return new ShieldImpl(mapper.map(rs, START, ctx), mapper.map(rs, END, ctx));
    }
}

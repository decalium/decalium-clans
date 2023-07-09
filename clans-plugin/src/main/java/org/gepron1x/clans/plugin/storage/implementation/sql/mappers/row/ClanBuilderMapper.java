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

import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.decoration.CombinedDecoration;
import org.gepron1x.clans.plugin.storage.implementation.sql.PrefixedRowMapper;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class ClanBuilderMapper extends PrefixedRowMapper<DraftClan.Builder> {
    private static final String ID = "id", TAG = "tag", OWNER = "owner", DISPLAY_NAME = "display_name", DECORATION = "decoration";
    private final ClanBuilderFactory builderFactory;


    public ClanBuilderMapper(@Nullable String prefix, @NotNull ClanBuilderFactory builderFactory) {
        super(prefix);
        this.builderFactory = builderFactory;
    }


    @Override
    public DraftClan.Builder map(ResultSet rs, StatementContext ctx) throws SQLException {
        ColumnMapper<Component> componentMapper = ctx.findColumnMapperFor(Component.class).orElseThrow();
		ColumnMapper<CombinedDecoration> decorationMapper = ctx.findColumnMapperFor(CombinedDecoration.class).orElseThrow();
        return builderFactory.draftClanBuilder()
                .tag(rs.getString(prefixed(TAG)))
                .displayName(componentMapper.map(rs, prefixed(DISPLAY_NAME), ctx))
				.tagDecoration(decorationMapper.map(rs, prefixed(DECORATION), ctx));
    }
}

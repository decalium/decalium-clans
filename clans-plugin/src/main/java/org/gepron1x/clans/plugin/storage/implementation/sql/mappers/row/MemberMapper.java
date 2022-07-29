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

import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.plugin.storage.implementation.sql.PrefixedRowMapper;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public final class MemberMapper extends PrefixedRowMapper<ClanMember> {
    private static final String UNIQUE_ID = "uuid", ROLE = "role";
    private final ClanBuilderFactory builderFactory;
    private final RoleRegistry roleRegistry;

    public MemberMapper(@NotNull ClanBuilderFactory builderFactory, @NotNull RoleRegistry roleRegistry, @Nullable String prefix) {
        super(prefix);
        this.builderFactory = builderFactory;
        this.roleRegistry = roleRegistry;
    }
    @Override
    public ClanMember map(ResultSet rs, StatementContext ctx) throws SQLException {
        ColumnMapper<UUID> uuidMapper = ctx.findColumnMapperFor(UUID.class).orElseThrow();
        return builderFactory.memberBuilder().uuid(uuidMapper.map(rs, prefixed(UNIQUE_ID), ctx))
                .role(roleRegistry.value(rs.getString(prefixed(ROLE))).orElseThrow())
                .build();
    }
}

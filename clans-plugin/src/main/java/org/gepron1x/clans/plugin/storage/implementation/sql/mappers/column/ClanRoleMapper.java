package org.gepron1x.clans.plugin.storage.implementation.sql.mappers.column;

import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class ClanRoleMapper implements ColumnMapper<ClanRole> {

    private final RoleRegistry roleRegistry;

    public ClanRoleMapper(@NotNull RoleRegistry roleRegistry) {

        this.roleRegistry = roleRegistry;
    }
    @Override
    public ClanRole map(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
        return roleRegistry.role(r.getString(columnNumber));
    }
}

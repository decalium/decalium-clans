package org.gepron1x.clans.plugin.storage.implementation.sql.mappers.column;

import org.gepron1x.clans.plugin.util.UuidUtil;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public final class UuidMapper implements ColumnMapper<UUID> {
    @Override
    public UUID map(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
        return UuidUtil.fromByteArray(r.getBytes(columnNumber));
    }
}

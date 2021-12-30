package org.gepron1x.clans.plugin.storage.implementation.sql.mappers.column;

import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;
import space.arim.omnibus.util.UUIDUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public final class UuidMapper implements ColumnMapper<UUID> {
    @Override
    public UUID map(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
        return UUIDUtil.fromByteArray(r.getBytes(columnNumber));
    }
}

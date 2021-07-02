package com.manya.clans.storage.converters.uuid;

import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UuidMapper implements ColumnMapper<UUID> {
    public static UuidMapper INSTANCE = new UuidMapper();
    @Override
    public UUID map(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
        ByteBuffer buf = ByteBuffer.wrap(r.getBytes(columnNumber));
        long most = buf.getLong();
        long least = buf.getLong();
        return new UUID(most, least);
    }
}

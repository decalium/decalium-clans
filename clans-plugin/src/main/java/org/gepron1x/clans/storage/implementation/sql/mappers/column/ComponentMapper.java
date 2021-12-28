package org.gepron1x.clans.storage.implementation.sql.mappers.column;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class ComponentMapper implements ColumnMapper<Component> {
    @Override
    public Component map(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
        return GsonComponentSerializer.gson().deserialize(r.getString(columnNumber));
    }
}

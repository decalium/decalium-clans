package org.gepron1x.clans.storage.implementation.sql.mappers.column;

import org.gepron1x.clans.api.statistic.StatisticType;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class StatisticTypeMapper implements ColumnMapper<StatisticType> {
    @Override
    public StatisticType map(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
        return new StatisticType(r.getString(columnNumber));
    }
}

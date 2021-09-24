package org.gepron1x.clans.storage.mappers.row;

import org.gepron1x.clans.statistic.StatisticRegistry;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StatisticRowMapper implements RowMapper<StatisticRow> {
    private static final String CLAN_TAG = "clan_tag", STATISTIC_TYPE = "statistic_type", VALUE = "value";
    private final StatisticRegistry statTypes;

    public StatisticRowMapper(StatisticRegistry statTypes) {
        this.statTypes = statTypes;
    }
    @Override
    public StatisticRow map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new StatisticRow(rs.getString(CLAN_TAG), statTypes.get(rs.getString(STATISTIC_TYPE)), rs.getInt(VALUE));
    }
}

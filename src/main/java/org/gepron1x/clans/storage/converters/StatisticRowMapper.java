package org.gepron1x.clans.storage.converters;

import org.gepron1x.clans.statistic.StatisticType;
import net.kyori.adventure.util.Index;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StatisticRowMapper implements RowMapper<StatisticRow> {
    private static final String CLAN_TAG = "clan_tag", STATISTIC_TYPE = "statistic_type", VALUE = "value";
    private final Index<String, StatisticType> statTypes;

    public StatisticRowMapper(Index<String, StatisticType> statTypes) {

        this.statTypes = statTypes;
    }
    @Override
    public StatisticRow map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new StatisticRow(rs.getString(CLAN_TAG), statTypes.value(rs.getString("statistic_type")), rs.getInt(VALUE));
    }
}

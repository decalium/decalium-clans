package org.gepron1x.clans.plugin.storage.implementation.sql.argument;

import org.gepron1x.clans.api.statistic.StatisticType;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;

import java.sql.Types;


public final class StatisticTypeArgumentFactory extends AbstractArgumentFactory<StatisticType> {
     StatisticTypeArgumentFactory() {
        super(Types.VARCHAR);
    }

    @Override
    protected Argument build(StatisticType value, ConfigRegistry config) {
        return (position, statement, ctx) -> statement.setString(position, value.name());
    }
}

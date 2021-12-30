package org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row;

import net.kyori.adventure.text.Component;
import org.gepron1x.clans.plugin.clan.ClanBuilder;
import org.gepron1x.clans.plugin.storage.implementation.sql.PrefixedRowMapper;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class ClanBuilderMapper extends PrefixedRowMapper<ClanBuilder> {
    private static final String ID = "id", TAG = "tag", OWNER = "owner", DISPLAY_NAME = "display_name";


    public ClanBuilderMapper(@Nullable String prefix) {
        super(prefix);
    }


    @Override
    public ClanBuilder map(ResultSet rs, StatementContext ctx) throws SQLException {
        ColumnMapper<Component> componentMapper = ctx.findColumnMapperFor(Component.class).orElseThrow();
        return new ClanBuilder(rs.getInt(prefixed(ID)))
                .tag(rs.getString(prefixed(TAG)))
                .displayName(componentMapper.map(rs, prefixed(DISPLAY_NAME), ctx));
    }
}

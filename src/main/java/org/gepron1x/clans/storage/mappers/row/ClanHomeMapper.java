package org.gepron1x.clans.storage.mappers.row;

import org.gepron1x.clans.clan.home.ClanHome;
import org.gepron1x.clans.storage.Mappers;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClanHomeMapper implements RowMapper<ClanHome> {
    private static final String
            NAME = "name",
            OWNER = "owner",
            DISPLAY_NAME = "display_name",
            ICON = "icon",
            LOCATION = "location";


    @Override
    public ClanHome map(ResultSet rs, StatementContext ctx) throws SQLException {
        return ClanHome.builder()
                .name(rs.getString(NAME))
                .displayName(Mappers.COMPONENT.map(rs, DISPLAY_NAME, ctx))
                .owner(Mappers.UUID.map(rs, OWNER, ctx))
                .icon(Mappers.ITEM_STACK.map(rs, ICON, ctx))
                .location(Mappers.LOCATION.map(rs, LOCATION, ctx))
                .build();
    }
}

package org.gepron1x.clans.storage;

import org.gepron1x.clans.storage.converters.component.ComponentMapper;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.storage.converters.uuid.UuidMapper;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClanMapper implements RowMapper<Clan> {
    public static final String TAG = "tag", DISPLAY_NAME = "display_name", CREATOR_UUID = "creator_uuid";
    @Override
    public Clan map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new Clan(rs.getString(TAG),
                UuidMapper.INSTANCE.map(rs, CREATOR_UUID, ctx),
                ComponentMapper.INSTANCE.map(rs, DISPLAY_NAME, ctx));
    }
}

package org.gepron1x.clans.storage.mappers.row;

import org.gepron1x.clans.DecaliumClans;
import org.gepron1x.clans.clan.ClanBuilder;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.storage.Mappers;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClanMapper implements RowMapper<ClanBuilder> {
    public static final String TAG = "tag", DISPLAY_NAME = "display_name", CREATOR_UUID = "creator_uuid";
    private final DecaliumClans plugin;

    public ClanMapper(DecaliumClans plugin) {

        this.plugin = plugin;
    }
    @Override
    public ClanBuilder map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new ClanBuilder(rs.getString(TAG))
                .displayName(Mappers.COMPONENT.map(rs, DISPLAY_NAME, ctx))
                .creator(new ClanMember(Mappers.UUID.map(rs, CREATOR_UUID, ctx), plugin.getOwnerRole()));
    }
}

package org.gepron1x.clans.storage.mappers.row;

import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.storage.Mappers;
import org.gepron1x.clans.util.registry.ClanRoleRegistry;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClanMemberMapper implements RowMapper<ClanMember> {
    private static final String UUID = "uuid", ROLE = "role";
    private final ClanRoleRegistry roles;

    public ClanMemberMapper(ClanRoleRegistry roles) {
        this.roles = roles;
    }
    @Override
    public ClanMember map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new ClanMember(
                Mappers.UUID.map(rs, UUID, ctx),
                roles.get(rs.getString(ROLE))
        );
    }
}

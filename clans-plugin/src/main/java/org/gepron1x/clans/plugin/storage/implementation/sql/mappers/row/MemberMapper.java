package org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row;

import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.plugin.storage.implementation.sql.PrefixedRowMapper;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static java.util.Objects.*;

public class MemberMapper extends PrefixedRowMapper<ClanMember> {
    private static final String UNIQUE_ID = "uuid", ROLE = "role";
    private final DecaliumClansApi api;
    public MemberMapper(@NotNull DecaliumClansApi api, @Nullable String prefix) {
        super(prefix);
        this.api = api;
    }
    @Override
    public ClanMember map(ResultSet rs, StatementContext ctx) throws SQLException {
        ColumnMapper<UUID> uuidMapper = ctx.findColumnMapperFor(UUID.class).orElseThrow();
        return api.memberBuilder().uuid(uuidMapper.map(rs, prefixed(UNIQUE_ID), ctx))
                .role(requireNonNull(api.getRoles().value(rs.getString(prefixed(ROLE)))))
                .build();
    }
}

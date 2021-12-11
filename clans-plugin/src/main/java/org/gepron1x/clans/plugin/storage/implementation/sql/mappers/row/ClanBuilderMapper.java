package org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row;

import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.plugin.clan.ClanImpl;
import org.gepron1x.clans.plugin.storage.implementation.sql.PrefixedRowMapper;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ClanBuilderMapper extends PrefixedRowMapper<Clan.Builder> {
    private static final String TAG = "tag", OWNER = "owner", DISPLAY_NAME = "display_name";
    private final DecaliumClansApi api;


    public ClanBuilderMapper(@NotNull DecaliumClansApi api, @Nullable String prefix) {
        super(prefix);
        this.api = api;
    }


    @Override
    public Clan.Builder map(ResultSet rs, StatementContext ctx) throws SQLException {
        ColumnMapper<Component> componentMapper = ctx.findColumnMapperFor(Component.class).orElseThrow();
        ColumnMapper<UUID> uuidMapper = ctx.findColumnMapperFor(UUID.class).orElseThrow();
        return api.clanBuilder()
                .tag(rs.getString(prefixed(TAG)))
                .owner(uuidMapper.map(rs, prefixed(OWNER), ctx))
                .displayName(componentMapper.map(rs, prefixed(DISPLAY_NAME), ctx));
    }
}

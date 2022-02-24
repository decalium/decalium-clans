package org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row;

import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.plugin.storage.implementation.sql.PrefixedRowMapper;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class ClanBuilderMapper extends PrefixedRowMapper<DraftClan.Builder> {
    private static final String ID = "id", TAG = "tag", OWNER = "owner", DISPLAY_NAME = "display_name";
    private final ClanBuilderFactory builderFactory;


    public ClanBuilderMapper(@Nullable String prefix, @NotNull ClanBuilderFactory builderFactory) {
        super(prefix);
        this.builderFactory = builderFactory;
    }


    @Override
    public DraftClan.Builder map(ResultSet rs, StatementContext ctx) throws SQLException {
        ColumnMapper<Component> componentMapper = ctx.findColumnMapperFor(Component.class).orElseThrow();
        return builderFactory.draftClanBuilder()
                .tag(rs.getString(prefixed(TAG)))
                .displayName(componentMapper.map(rs, prefixed(DISPLAY_NAME), ctx));
    }
}

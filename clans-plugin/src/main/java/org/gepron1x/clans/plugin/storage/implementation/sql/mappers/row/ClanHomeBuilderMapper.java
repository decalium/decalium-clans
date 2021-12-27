package org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.plugin.storage.implementation.sql.PrefixedRowMapper;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public final class ClanHomeBuilderMapper extends PrefixedRowMapper<ClanHome.Builder> {

    private static final String NAME = "name", CREATOR = "creator", DISPLAY_NAME = "display_name", ICON = "icon";
    private final ClanBuilderFactory builderFactory;

    public ClanHomeBuilderMapper(@NotNull ClanBuilderFactory builderFactory, @Nullable String prefix) {
        super(prefix);
        this.builderFactory = builderFactory;

    }
    @Override
    public ClanHome.Builder map(ResultSet rs, StatementContext ctx) throws SQLException {
        ColumnMapper<ItemStack> itemStackMapper = ctx.findColumnMapperFor(ItemStack.class).orElseThrow();
        ColumnMapper<Component> componentMapper = ctx.findColumnMapperFor(Component.class).orElseThrow();
        ColumnMapper<UUID> uuidMapper = ctx.findColumnMapperFor(UUID.class).orElseThrow();

        return builderFactory.homeBuilder()
                .name(rs.getString(prefixed(NAME)))
                .creator(uuidMapper.map(rs, prefixed(CREATOR), ctx))
                .displayName(componentMapper.map(rs, prefixed(DISPLAY_NAME), ctx))
                .icon(itemStackMapper.map(rs, prefixed(ICON), ctx));
    }
}

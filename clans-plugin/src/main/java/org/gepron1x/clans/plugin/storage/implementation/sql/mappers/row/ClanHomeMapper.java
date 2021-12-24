package org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.plugin.storage.implementation.sql.PrefixedRowMapper;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public final class ClanHomeMapper extends PrefixedRowMapper<ClanHome> {

    private static final String NAME = "name", CREATOR = "creator", DISPLAY_NAME = "display_name", ICON = "icon";
    private static final String X = "x", Y = "y", Z = "z", WORLD = "world";
    private final Server server;
    private final DecaliumClansApi api;

    public ClanHomeMapper(@NotNull Server server, @NotNull DecaliumClansApi api, @Nullable String prefix) {
        super(prefix);
        this.server = server;
        this.api = api;

    }
    @Override
    public ClanHome map(ResultSet rs, StatementContext ctx) throws SQLException {
        ColumnMapper<ItemStack> itemStackMapper = ctx.findColumnMapperFor(ItemStack.class).orElseThrow();
        ColumnMapper<Component> componentMapper = ctx.findColumnMapperFor(Component.class).orElseThrow();
        ColumnMapper<UUID> uuidMapper = ctx.findColumnMapperFor(UUID.class).orElseThrow();
        World world = server.getWorld(rs.getString(prefixed(WORLD)));
        int x = rs.getInt(prefixed(X));
        int y = rs.getInt(prefixed(Y));
        int z = rs.getInt(prefixed(Z));


        return api.homeBuilder()
                .name(rs.getString(prefixed(NAME)))
                .creator(uuidMapper.map(rs, prefixed(CREATOR), ctx))
                .displayName(componentMapper.map(rs, prefixed(DISPLAY_NAME), ctx))
                .icon(itemStackMapper.map(rs, prefixed(ICON), ctx))
                .location(new Location(world, x, y, z))
                .build();
    }
}

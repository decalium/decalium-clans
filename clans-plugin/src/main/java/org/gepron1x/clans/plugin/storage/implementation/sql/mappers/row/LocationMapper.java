package org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.gepron1x.clans.plugin.storage.implementation.sql.PrefixedRowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class LocationMapper extends PrefixedRowMapper<Location> {

    private static final double OFFSET = 0.5F;

    private static final String WORLD = "world", X = "x", Y= "y", Z= "z";

    private final Server server;

    public LocationMapper(@NotNull Server server, @Nullable String prefix) {
        super(prefix);
        this.server = server;
    }

    @Override
    public Location map(ResultSet rs, StatementContext ctx) throws SQLException {

        World world = server.getWorld(rs.getString(prefixed(WORLD)));
        int x = rs.getInt(prefixed(X));
        int y = rs.getInt(prefixed(Y));
        int z = rs.getInt(prefixed(Z));

        return new Location(world, x + OFFSET, y + OFFSET, z + OFFSET);
    }
}

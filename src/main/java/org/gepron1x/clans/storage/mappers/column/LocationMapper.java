package org.gepron1x.clans.storage.mappers.column;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.gepron1x.clans.util.UuidUtil;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class LocationMapper implements ColumnMapper<Location> {

    @Override
    public Location map(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
        ByteBuffer buffer = ByteBuffer.wrap(r.getBytes(columnNumber));

        UUID uuid = getUuid(buffer);
        double x = buffer.getDouble();
        double y = buffer.getDouble();
        double z = buffer.getDouble();
        float yaw = buffer.getFloat();
        float pitch = buffer.getFloat();

        return new Location(Bukkit.getWorld(uuid), x, y, z, yaw, pitch);
    }
    private UUID getUuid(ByteBuffer buffer) {
        return UuidUtil.fromByteArray(get(buffer, UuidUtil.BYTES));
    }
    private byte[] get(ByteBuffer buf, int size) {
        byte[] bytes = new byte[size];
        buf.get(bytes);
        return bytes;
    }
}

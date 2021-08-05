package org.gepron1x.clans.storage.argument;

import org.bukkit.Location;
import org.gepron1x.clans.util.UuidUtil;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;

import java.nio.ByteBuffer;
import java.sql.Types;

public class LocationArgumentFactory extends AbstractArgumentFactory<Location> {
    private static final int UUID_SIZE = 16;
    private static final int LOCATION_SIZE = UUID_SIZE + Double.BYTES * 3 + Float.BYTES * 2;
    public LocationArgumentFactory() {
        super(Types.BINARY);
    }

    @Override
    protected Argument build(Location value, ConfigRegistry config) {

        return ((position, statement, ctx) -> {
            ByteBuffer buffer = ByteBuffer.allocate(LOCATION_SIZE);

            buffer.put(UuidUtil.toByteArray(value.getWorld().getUID()));
            buffer.putDouble(value.getX());
            buffer.putDouble(value.getY());
            buffer.putDouble(value.getZ());
            buffer.putFloat(value.getYaw());
            buffer.putFloat(value.getPitch());
            statement.setBytes(position, buffer.array());
        });
    }
}

package org.gepron1x.clans.storage.argument;

import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;

import java.nio.ByteBuffer;
import java.sql.Types;
import java.util.UUID;

public class UuidArgumentFactory extends AbstractArgumentFactory<UUID> {

    public UuidArgumentFactory() {
        super(Types.BINARY);
    }

    @Override
    protected Argument build(UUID value, ConfigRegistry config) {

        return ((position, statement, ctx) -> {
            ByteBuffer buf = ByteBuffer.allocate(16);
            buf.putLong(value.getMostSignificantBits());
            buf.putLong(value.getLeastSignificantBits());
            statement.setBytes(position, buf.array());
        });
    }
}

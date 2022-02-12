package org.gepron1x.clans.plugin.storage.implementation.sql.argument;

import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;
import space.arim.omnibus.util.UUIDUtil;

import java.sql.Types;
import java.util.UUID;

public final class UuidArgumentFactory extends AbstractArgumentFactory<UUID> {

    public UuidArgumentFactory() {
        super(Types.BINARY);
    }

    @Override
    protected Argument build(UUID value, ConfigRegistry config) {
        return (position, statement, ctx) -> statement.setBytes(position, UUIDUtil.toByteArray(value));
    }
}

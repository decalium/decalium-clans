package org.gepron1x.clans.plugin.storage.implementation.sql.argument;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;

import java.sql.Types;

public final class ComponentArgumentFactory extends AbstractArgumentFactory<Component> {

    public ComponentArgumentFactory() {
        super(Types.VARCHAR);
    }

    @Override
    protected Argument build(Component value, ConfigRegistry config) {
        return (position, statement, ctx) -> statement.setString(position, GsonComponentSerializer.gson().serialize(value));
    }
}

package org.gepron1x.clans.storage.argument;

import net.kyori.adventure.text.Component;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;

import java.sql.Types;

import static net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson;

public class ComponentArgumentFactory extends AbstractArgumentFactory<Component> {

    public ComponentArgumentFactory() {
        super(Types.BLOB);
    }

    @Override
    protected Argument build(Component value, ConfigRegistry config) {
        return ((position, statement, ctx) -> statement.setString(position, gson().serialize(value)));
    }
}

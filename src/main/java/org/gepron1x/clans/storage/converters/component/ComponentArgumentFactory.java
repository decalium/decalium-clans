package org.gepron1x.clans.storage.converters.component;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;

import java.sql.Types;

import static net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.*;

public class ComponentArgumentFactory extends AbstractArgumentFactory<Component> {
    /**
     * Constructs an {@link ArgumentFactory} for type {@code T}.
     *
     * @param sqlType the {@link Types} constant to use when the argument value is {@code null}.
     */
    public ComponentArgumentFactory() {
        super(Types.BLOB);
    }

    @Override
    protected Argument build(Component value, ConfigRegistry config) {
        return ((position, statement, ctx) -> statement.setString(position, gson().serialize(value)));
    }
}

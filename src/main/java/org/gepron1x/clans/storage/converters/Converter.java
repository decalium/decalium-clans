package org.gepron1x.clans.storage.converters;


import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public interface Converter<T> {
    T map(ResultSet r, int columnNumber, StatementContext ctx);
    void parseArgument(int position, PreparedStatement statement, StatementContext ctx, T value);
    int getType();
    default void register(Jdbi jdbi) {
        jdbi.registerColumnMapper(this::map);
        jdbi.registerArgument(new ConverterArgumentFactory<>(this));
    }
    class ConverterArgumentFactory<T> extends AbstractArgumentFactory<T> {

        private final Converter<T> converter;
        private ConverterArgumentFactory(Converter<T> converter) {
            super(converter.getType());
            this.converter = converter;
        }
        @Override
        protected Argument build(T value, ConfigRegistry config) {
            return ((position, statement, ctx) -> converter.parseArgument(position, statement, ctx, value));
        }
    }
}

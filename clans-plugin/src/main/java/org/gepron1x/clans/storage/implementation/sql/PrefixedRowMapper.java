package org.gepron1x.clans.storage.implementation.sql;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PrefixedRowMapper<T> implements RowMapper<T> {
    protected final String prefix;

    public PrefixedRowMapper(@Nullable String prefix) {
        this.prefix = prefix == null ? "" : prefix + "_";
    }

    public PrefixedRowMapper() {
        this(null);
    }

    protected String prefixed(@NotNull String value) {
        return prefix + value;
    }


}

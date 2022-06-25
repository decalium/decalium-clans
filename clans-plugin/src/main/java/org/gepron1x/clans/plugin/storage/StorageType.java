package org.gepron1x.clans.plugin.storage;

import org.jdbi.v3.core.Handle;

import java.util.function.Consumer;

public enum StorageType {
    H2(org.h2.Driver.class.getName(), handle -> handle.execute("SHUTDOWN")),
    MYSQL("com.mysql.cj.jdbc.Driver"),
    POSTGRESQL("");

    private final String driverClassName;
    private final Consumer<Handle> onDisable;

    StorageType(String driverClassName, Consumer<Handle> onDisable) {
        this.driverClassName = driverClassName;
        this.onDisable = onDisable;
    }

    StorageType(String driverClassName) {
        this(driverClassName, h -> {});
    }

    public void disable(Handle handle) {
        this.onDisable.accept(handle);
    }

    public String driverClassName() {
        return driverClassName;
    }
}

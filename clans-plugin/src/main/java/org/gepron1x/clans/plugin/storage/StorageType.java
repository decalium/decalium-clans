package org.gepron1x.clans.plugin.storage;

import org.jdbi.v3.core.Handle;

import java.util.function.Consumer;

public enum StorageType {
    H2(handle -> handle.execute("SHUTDOWN")),
    MYSQL,
    POSTGRESQL;

    private final Consumer<Handle> onDisable;

    StorageType(Consumer<Handle> onDisable) {
        this.onDisable = onDisable;
    }

    StorageType() {
        this.onDisable = h -> {};
    }

    public void disable(Handle handle) {
        this.onDisable.accept(handle);
    }
}

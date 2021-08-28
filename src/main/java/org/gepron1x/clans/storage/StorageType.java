package org.gepron1x.clans.storage;


import org.jdbi.v3.core.Jdbi;

import java.util.function.Consumer;

public enum StorageType {
    MYSQL("com.mysql.jdbc.Driver"),
    H2(jdbi -> jdbi.useHandle(handle -> handle.execute("SHUTDOWN")), org.h2.Driver.class.getName());


    private final Consumer<Jdbi> onDisable;
    private final String driverName;

    StorageType(Consumer<Jdbi> onDisable, String driverName) {
        this.onDisable = onDisable;
        this.driverName = driverName;
    }
    StorageType(String driverName) {
        this(j -> {}, driverName);
    }

    public void onDisable(Jdbi jdbi) {
        onDisable.accept(jdbi);
    }
    public String getDriverName() {
        return driverName;
    }
}

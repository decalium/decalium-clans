package org.gepron1x.clans.storage.dao;

import org.bukkit.Location;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface LocationDao {

    @SqlUpdate("""
            CREATE TABLE IF NOT EXISTS locations
            (
            `id` BIGINT NOT NULL,
            `world` BINARY(16),
            `x` DOUBLE NOT NULL,
            `y` DOUBLE NOT NULL,
            `z` DOUBLE NOT NULL,
            `yaw` FLOAT NOT NULL,
            `pitch` FLOAT NOT NULL,
            PRIMARY KEY(`id`)
            )
            """)
    void createTable();

    @SqlUpdate("INSERT INTO location (`id`, `x`, `y`, `z`, `yaw`, `pitch`) VALUES(:id, :location.getX, :location.getY, :location.getZ, :location.getYaw, :location.getPitch)")
    void insert(long id, Location location);


}

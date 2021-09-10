package org.gepron1x.clans.storage.dao;


import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.home.ClanHome;
import org.jdbi.v3.sqlobject.config.ValueColumn;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Map;


public interface ClanHomeDao {
    @SqlUpdate("""
            CREATE TABLE IF NOT EXISTS homes
            (
            `clan` VARCHAR(16) NOT NULL UNIQUE,
            `owner` BINARY(16) NOT NULL UNIQUE,
            `name` VARCHAR(32) NOT NULL UNIQUE,
            `display_name` JSON,
            `icon` BLOB,
            `location` BIGINT NOT NULL,
            PRIMARY KEY(`name`)
            )
            """)
    void createTable();
    @SqlUpdate("""
            INSERT INTO homes (`clan`, `owner`, `name`, `display_name`, `icon`, `location`)
            VALUES (
            :clan.getTag,
            :home.getOwner
            :home.getName,
            :home.getDisplayName,
            :home.getLocation
            )
            """)
    void addHome(@BindMethods Clan clan, @BindMethods ClanHome home);
    @SqlUpdate("DELETE FROM homes WHERE `name`=:home.getName")
    void removeHome(@BindMethods ClanHome home);

    @SqlUpdate("DELETE FROM homes WHERE `clan`=:clan.getTag")
    void removeHomes(@BindMethods Clan clan);

    @SqlQuery("SELECT * FROM homes")
    @ValueColumn("clan")
    Map<ClanHome, String> homes();



}

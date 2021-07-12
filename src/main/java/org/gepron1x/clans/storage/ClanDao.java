package org.gepron1x.clans.storage;


import org.gepron1x.clans.clan.Clan;
import net.kyori.adventure.text.Component;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.customizer.BindMethodsList;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Collection;
import java.util.List;


public interface ClanDao {




    @SqlUpdate("""
            CREATE TABLE IF NOT EXISTS clans
            (`tag` VARCHAR(16) NOT NULL UNIQUE,
            `creator_uuid` BINARY(16) NOT NULL UNIQUE,
            `display_name` JSON NOT NULL,
            PRIMARY KEY(`tag`))
            """)
    void createTable();
    @SqlUpdate("DELETE FROM clans WHERE `tag` NOT IN (<ignored>)")
    void clearClans(@BindMethodsList(methodNames = {"getTag"}) Collection<Clan> ignored);
    @SqlUpdate("""
            INSERT INTO clans
            (`tag`, `creator_uuid`, `display_name`)
            VALUES <clans>
            ON DUPLICATE KEY UPDATE
            `creator_uuid`=VALUES(`creator_uuid`)
            `display_name`=VALUES(`display_name`)
            """)
    void updateClans(@BindMethodsList(methodNames = {"getTag", "getCreator", "getDisplayName"}) Collection<Clan> clans);

    @SqlUpdate("INSERT INTO clans (`tag`, `creator_uuid`, `display_name`) VALUES (:getTag, :getCreator, :getDisplayName)")
    void insertClan(@BindMethods Clan clan);

    @SqlUpdate("DELETE FROM clans WHERE `tag`=:getTag")
    void removeClan(Clan clan);

    @SqlQuery("SELECT * FROM clans")
    List<Clan> loadClans();

    @SqlUpdate("UPDATE clans SET `display_name`=:displayName WHERE `tag`=:clan.getTag")
    void setDisplayName(@BindMethods Clan clan, Component displayName);

}

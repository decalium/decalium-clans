package org.gepron1x.clans.storage.dao;


import org.gepron1x.clans.clan.Clan;
import net.kyori.adventure.text.Component;
import org.gepron1x.clans.clan.ClanBuilder;
import org.jdbi.v3.sqlobject.customizer.Bind;
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

    @SqlUpdate("INSERT INTO clans (`tag`, `creator_uuid`, `display_name`) VALUES (:getTag, :getCreator.getUniqueId, :getDisplayName)")
    void insertClan(@BindMethods Clan clan);

    @SqlUpdate("DELETE FROM clans WHERE `tag`=:getTag")
    void removeClan(@BindMethods Clan clan);

    @SqlQuery("SELECT * FROM clans")
    List<ClanBuilder> getClans();

    @SqlUpdate("UPDATE clans SET `display_name`=:displayName WHERE `tag`=:clan.getTag")
    void setDisplayName(@BindMethods Clan clan, @Bind Component displayName);

}

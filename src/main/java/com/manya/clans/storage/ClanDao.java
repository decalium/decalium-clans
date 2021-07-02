package com.manya.clans.storage;


import com.manya.clans.clan.Clan;
import net.kyori.adventure.text.Component;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;


public interface ClanDao {

    @SqlUpdate("CREATE TABLE IF NOT EXISTS clans (`tag` VARCHAR(16) NOT NULL UNIQUE, `creator_uuid` BINARY(16) NOT NULL UNIQUE, `display_name` JSON NOT NULL, PRIMARY KEY(`tag`))")
    void createTable();

    @SqlUpdate("INSERT INTO clans (`tag`, `creator_uuid`, `display_name`) VALUES (:getTag, :getCreator, :getDisplayName)")
    void insertOrUpdateClan(@BindMethods Clan clan);

    @SqlUpdate("DELETE FROM clans WHERE `tag`=:clan.getTag")
    void removeClan(@BindMethods("clan") Clan clan);

    @SqlQuery("SELECT * FROM clans")
    List<Clan> getClans();

    @SqlUpdate("UPDATE clans SET `display_name`=:name WHERE `tag`=:clan.getTag")
    void setDisplayName(@BindMethods("clan") Clan clan, @Bind("name") Component displayName);

}

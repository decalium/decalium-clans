package org.gepron1x.clans.storage.dao;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.home.ClanHome;
import org.gepron1x.clans.events.Property;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.text.MessageFormat;

public interface ClanHomeDao extends PropertyDao {
    @SqlUpdate("""
            CREATE TABLE IF NOT EXISTS homes
            (
            `clan` VARCHAR(16) NOT NULL UNIQUE,
            `owner` BINARY(16) NOT NULL UNIQUE,
            `name` VARCHAR(32) NOT NULL UNIQUE,
            `display_name` JSON,
            `icon` BLOB,
            `location` BIGINT NOT NULL,
            PRIMARY KEY(`id`)
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

    @SqlUpdate("UPDATE homes SET `display_name`=:displayName WHERE `name`=:home.getName")
    void setDisplayName(@BindMethods ClanHome home, @Bind Component displayName);

    @SqlUpdate("UPDATE homes SET `icon`=:icon WHERE `name`=:home.getName")
    void setIcon(@BindMethods ClanHome home, @Bind ItemStack icon);

    @SqlUpdate("UPDATE homes SET `location`=:location WHERE `name`=:home.getName")
    void setLocation(@BindMethods ClanHome home, @Bind Location location);

    @Override
    default void updateProperty(Property<?, ?> property, Object target, Object value) {
        getHandle().createUpdate(MessageFormat.format("UPDATE homes SET `{0}`=:value WHERE `name`=:name", property.getName()))
                .bind("value", value)
                .bind("name", ((ClanHome) target).getName()).execute();
    }
}

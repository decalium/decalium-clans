package org.gepron1x.clans.storage.dao;

import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.clan.member.role.ClanRole;
import org.gepron1x.clans.events.Property;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.sqlobject.SqlObject;
import org.jdbi.v3.sqlobject.config.ValueColumn;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.text.MessageFormat;
import java.util.Map;
import java.util.UUID;

public interface ClanMemberDao extends PropertyDao {
    @SqlUpdate("CREATE TABLE IF NOT EXISTS members (" +
            "`uuid` BINARY(16) NOT NULL UNIQUE, " +
            "`clan` VARCHAR(16) NOT NULL UNIQUE, " +
            "`role` VARCHAR(16) NOT NULL, " +
            "PRIMARY KEY(`uuid`)" +
            ")")
    void createTable();
    @SqlUpdate("INSERT INTO members (`uuid`, `clan`, `role`) VALUES (:member.getUniqueId, :clan.getTag, :member.getRole.getName)")
    void addMember(@BindMethods ClanMember member, @BindMethods Clan clan);

    default void removeMember(ClanMember member) {
        removeMember(member.getUniqueId());
    }
    @SqlUpdate("DELETE FROM members WHERE `uuid`=:uuid")
    void removeMember(@Bind UUID uuid);

    @ValueColumn("clan")
    @SqlQuery("SELECT * FROM members")
    Map<ClanMember, String> loadMembers();
    @SqlUpdate("UPDATE members SET `role`=:role.getName WHERE `uuid`=:member.getUniqueId")
    void setRole(@BindMethods ClanMember member, @BindMethods ClanRole role);

    @SqlUpdate("DELETE FROM members WHERE `clan`=:clan.getTag")
    void clearMembers(@BindMethods Clan clan);


    @Override
    default void updateProperty(Property<?, ?> property, Object target, Object value) {
        getHandle().createUpdate(MessageFormat.format("UPDATE members SET `{0}`=:value WHERE `uuid`=:uuid", property.getName()))
                .bind("value", value)
                .bind("uuid", ((ClanMember) target).getUniqueId())
                .execute();
    }
}

package com.manya.clans.storage;

import com.manya.clans.clan.Clan;
import com.manya.clans.clan.member.ClanMember;
import org.jdbi.v3.sqlobject.config.KeyColumn;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Map;
import java.util.UUID;

public interface ClanMemberDao {
    @SqlUpdate("CREATE TABLE IF NOT EXISTS members (" +
            "`uuid` BINARY(16) NOT NULL UNIQUE, " +
            "`name` VARCHAR(16) NOT NULL UNIQUE, " +
            "`clan` VARCHAR(16) NOT NULL UNIQUE, " +
            "`role` VARCHAR(16) NOT NULL, " +
            "PRIMARY KEY(`uuid`)" +
            ")")
    void createTable();
    @SqlUpdate("INSERT INTO members (`uuid`, `name`, `clan`, `role`) VALUES (`:member.getUniqueId`, `:member.getName`, `:clan.getTag`, `:member.getRole.getName`)")
    void addMember(@BindMethods("member") ClanMember member, @BindMethods("clan") Clan clan);

    default void removeMember(ClanMember member) {
        removeMember(member.getUniqueId());
    }
    @SqlUpdate("DELETE FROM members WHERE `uuid`=:uuid")
    void removeMember(@Bind("uuid") UUID uuid);

    @KeyColumn("clan")
    @SqlQuery("SELECT * FROM members")
    Map<String, ClanMember> loadMembers();
    @SqlUpdate("DELETE FROM members WHERE `clan`=:clan.getTag")
    void clearMembers(@BindMethods("clan") Clan clan);
}

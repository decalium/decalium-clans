package org.gepron1x.clans.plugin.storage.implementation.sql.editor;

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.api.editor.MemberEdition;
import org.intellij.lang.annotations.Language;
import org.jdbi.v3.core.Handle;
import org.jetbrains.annotations.NotNull;

public final class SqlMemberEdition implements MemberEdition {
    @Language("SQL")
    private static final String UPDATE_ROLE = "UPDATE `members` SET `role`=? WHERE `uuid`=? AND `clan_id`=?";
    private final Handle handle;
    private final Clan clan;
    private final ClanMember member;

    public SqlMemberEdition(@NotNull Handle handle, @NotNull Clan clan, @NotNull ClanMember member) {

        this.handle = handle;
        this.clan = clan;
        this.member = member;
    }

    @Override
    public MemberEdition setRole(@NotNull ClanRole role) {
        handle.createUpdate(UPDATE_ROLE)
                .bind(1, member.getUniqueId())
                .bind(2, clan.getId())
                .bind(0, role)
                .execute();
        return this;
    }
}

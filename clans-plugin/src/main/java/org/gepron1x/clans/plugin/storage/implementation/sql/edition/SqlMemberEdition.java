package org.gepron1x.clans.plugin.storage.implementation.sql.edition;

import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.api.edition.MemberEdition;
import org.intellij.lang.annotations.Language;
import org.jdbi.v3.core.Handle;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class SqlMemberEdition implements MemberEdition {
    @Language("SQL")
    private static final String UPDATE_ROLE = "UPDATE `members` SET `role`=? WHERE `uuid`=? AND `clan_id`=?";
    private final Handle handle;
    private final int clanId;
    private final UUID memberId;

    public SqlMemberEdition(@NotNull Handle handle, int clanId, @NotNull UUID memberId) {

        this.handle = handle;
        this.clanId = clanId;
        this.memberId = memberId;
    }

    @Override
    public MemberEdition setRole(@NotNull ClanRole role) {
        handle.createUpdate(UPDATE_ROLE)
                .bind(1, memberId)
                .bind(2, clanId)
                .bind(0, role)
                .execute();
        return this;
    }
}

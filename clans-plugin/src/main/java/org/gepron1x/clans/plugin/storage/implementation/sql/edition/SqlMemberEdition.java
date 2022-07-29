/*
 * decalium-clans
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.plugin.storage.implementation.sql.edition;

import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.api.edition.member.MemberEdition;
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
    public MemberEdition appoint(@NotNull ClanRole role) {
        handle.createUpdate(UPDATE_ROLE)
                .bind(1, memberId)
                .bind(2, clanId)
                .bind(0, role)
                .execute();
        return this;
    }
}

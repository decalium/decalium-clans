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
package org.gepron1x.clans.plugin.storage.implementation.sql.common;

import org.gepron1x.clans.api.clan.member.ClanMember;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.statement.PreparedBatch;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public final class SavableMembers implements Savable {

    private static final String INSERT_MEMBERS = "INSERT INTO members (clan_id, uuid, role) VALUES (?, ?, ?)";

    private final int clanId;
    private final Collection<? extends ClanMember> members;

    public SavableMembers(int clanId, Collection<? extends ClanMember> members) {
        this.clanId = clanId;
        this.members = members;
    }


    public SavableMembers(int clanId, ClanMember member) {
        this(clanId, Collections.singleton(member));
    }
    @Override
    public int execute(Handle handle) {
        PreparedBatch batch = handle.prepareBatch(INSERT_MEMBERS);
        for(ClanMember member : this.members) {
            batch.add(this.clanId, member.uniqueId(), member.role());
        }
        return Arrays.stream(batch.execute()).sum();
    }
}

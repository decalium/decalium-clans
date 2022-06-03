package org.gepron1x.clans.plugin.storage.implementation.sql.common;

import org.gepron1x.clans.api.clan.member.ClanMember;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.statement.PreparedBatch;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public final class SavableMembers implements Savable {

    private static final String INSERT_MEMBERS = "INSERT INTO members (clan_id, uuid, role) VALUES (?, ?, ?)";

    private final Handle handle;
    private final int clanId;
    private final Collection<? extends ClanMember> members;

    public SavableMembers(Handle handle, int clanId, Collection<? extends ClanMember> members) {
        this.handle = handle;
        this.clanId = clanId;
        this.members = members;
    }


    public SavableMembers(Handle handle, int clanId, ClanMember member) {
        this(handle, clanId, Collections.singleton(member));
    }
    @Override
    public int execute() {
        PreparedBatch batch = this.handle.prepareBatch(INSERT_MEMBERS);
        for(ClanMember member : this.members) {
            batch.add(this.clanId, member.uniqueId(), member.role());
        }
        return Arrays.stream(batch.execute()).sum();
    }
}

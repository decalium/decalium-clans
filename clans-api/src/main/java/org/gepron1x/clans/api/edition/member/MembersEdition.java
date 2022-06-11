package org.gepron1x.clans.api.edition.member;

import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.Members;
import org.gepron1x.clans.api.edition.RegistryEdition;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

public interface MembersEdition extends RegistryEdition<UUID, ClanMember, MemberEdition, Members> {

    @Override
    MembersEdition add(ClanMember value);

    @Override
    MembersEdition add(Collection<ClanMember> values);

    @Override
    MembersEdition remove(UUID key);

    @Override
    MembersEdition edit(UUID key, Consumer<MemberEdition> consumer);
}

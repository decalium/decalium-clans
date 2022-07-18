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

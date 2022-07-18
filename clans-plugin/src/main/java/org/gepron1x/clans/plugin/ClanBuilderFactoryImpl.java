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
package org.gepron1x.clans.plugin;

import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.plugin.clan.ClanHomeImpl;
import org.gepron1x.clans.plugin.clan.DraftClanImpl;
import org.gepron1x.clans.plugin.clan.member.ClanMemberImpl;
import org.gepron1x.clans.plugin.clan.member.ClanRoleImpl;
import org.jetbrains.annotations.NotNull;

public final class ClanBuilderFactoryImpl implements ClanBuilderFactory {
    @Override
    public @NotNull DraftClan.Builder draftClanBuilder() {
        return DraftClanImpl.builder();
    }

    @Override
    public @NotNull ClanMember.Builder memberBuilder() {
        return ClanMemberImpl.builder();
    }

    @Override
    public @NotNull ClanHome.Builder homeBuilder() {
        return ClanHomeImpl.builder();
    }

    @Override
    public @NotNull ClanRole.Builder roleBuilder() {
        return ClanRoleImpl.builder();
    }
}

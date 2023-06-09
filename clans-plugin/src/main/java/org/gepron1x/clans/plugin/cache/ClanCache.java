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
package org.gepron1x.clans.plugin.cache;

import com.google.common.base.MoreObjects;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ClanCache {
    private final Map<String, CachingClan> clanMap = new ConcurrentHashMap<>();
    private final Map<UUID, CachingClan> userClanMap = new ConcurrentHashMap<>();

    public ClanCache() {

    }

    @Nullable
    public CachingClan getUserClan(@NotNull UUID uuid) {
        return userClanMap.get(uuid);

    }

    public @NotNull @UnmodifiableView Collection<Clan> getClans() {
        return Collections.unmodifiableCollection(clanMap.values());
    }



    public boolean isCached(@NotNull String tag) {
        return clanMap.containsKey(tag);
    }

    @Nullable
    public CachingClan getClan(@NotNull String tag) {
        return clanMap.get(tag);
    }


    public void cacheClan(CachingClan clan) {
        clanMap.put(clan.tag(), clan);
        for(ClanMember member : clan.members()) {
            userClanMap.put(member.uniqueId(), clan);
        }
    }

	public void cacheClan(UUID user, CachingClan clan) {
		userClanMap.put(user, clan);
	}

	public void removeClanEntry(UUID user) {
		userClanMap.remove(user);
	}


    public void removeClan(String tag) {
        Clan clan = clanMap.remove(tag);
        if(clan == null) return;
        for(UUID uuid : clan.memberMap().keySet()) userClanMap.remove(uuid);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("cachedClans", clanMap.values())
                .toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClanCache clanCache = (ClanCache) o;
        return clanMap.equals(clanCache.clanMap) && userClanMap.equals(clanCache.userClanMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clanMap, userClanMap);
    }
}

package org.gepron1x.clans.plugin;

import com.google.common.base.MoreObjects;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.plugin.storage.IdentifiedDraftClanImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ClanCacheImpl {
    private final Map<String, IdentifiedDraftClanImpl> clanMap = new ConcurrentHashMap<>();
    private final Map<Integer, DraftClan> idClanMap = new ConcurrentHashMap<>();
    private final Map<UUID, IdentifiedDraftClanImpl> userClanMap = new ConcurrentHashMap<>();

    public ClanCacheImpl() {

    }

    @Nullable
    public IdentifiedDraftClanImpl getUserClan(@NotNull UUID uuid) {
        return userClanMap.get(uuid);

    }

    public @NotNull @UnmodifiableView Collection<DraftClan> getClans() {
        return Collections.unmodifiableCollection(idClanMap.values());
    }



    public boolean isCached(@NotNull String tag) {
        return clanMap.containsKey(tag);
    }

    @Nullable
    public IdentifiedDraftClanImpl getClan(@NotNull String tag) {
        return clanMap.get(tag);
    }

    public @Nullable DraftClan getClan(int id) {
        return idClanMap.get(id);
    }

    public void cacheClan(int id, DraftClan clan) {
        idClanMap.put(id, clan);
        IdentifiedDraftClanImpl identifiedDraftClan = new IdentifiedDraftClanImpl(id, clan);
        clanMap.put(clan.tag(), identifiedDraftClan);
        for(ClanMember member : clan.members()) {
            userClanMap.put(member.uniqueId(), identifiedDraftClan);
        }
    }


    public void removeClan(int id) {
        DraftClan clan = idClanMap.remove(id);
        if(clan == null) return;
        clanMap.remove(clan.tag());
        for(UUID uuid : clan.memberMap().keySet()) userClanMap.remove(uuid);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("cachedClans", clanMap.values())
                .toString();
    }


}

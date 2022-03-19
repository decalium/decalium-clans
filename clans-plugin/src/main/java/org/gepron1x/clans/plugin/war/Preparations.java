package org.gepron1x.clans.plugin.war;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Preparations {

    private final Map<UUID, ClanWarPreparation> preparationMap = new HashMap<>();


    public void remove(UUID uuid) {
        preparationMap.remove(uuid);
    }

    public ClanWarPreparation getPreparation(UUID uuid) {
        return preparationMap.get(uuid);
    }

    public void createPreparation(UUID uuid, Clan clan) {
        ClanWarTeam.Builder builder = new ClanWarTeam.Builder().clan(clan).addMember(uuid);

    }
}

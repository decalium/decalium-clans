package org.gepron1x.clans.plugin.migration;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.gepron1x.clans.plugin.migration.adapter.*;

public class ClanAdapterFactory implements TypeAdapterFactory {

    private final DecaliumClansApi api;
    private final Server server;

    public ClanAdapterFactory(DecaliumClansApi api, Server server) {
        this.api = api;
        this.server = server;
    }
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {

        Class<? super T> rawType = type.getRawType();
        TypeAdapter<?> adapter = null;
        if(Clan.class.isAssignableFrom(rawType)) {
            adapter = new ClanAdapter(gson, api);
        } else if(ClanMember.class.isAssignableFrom(rawType)) {
            adapter = new ClanMemberAdapter(gson, api);
        } else if(ClanHome.class.isAssignableFrom(rawType)) {
            adapter = new ClanHomeAdapter(gson, api);
        } else if(StatisticType.class.isAssignableFrom(rawType)) {
            adapter = new StatisticTypeAdapter();
        } else if(ItemStack.class.isAssignableFrom(rawType)) {
            adapter = new ItemStackAdapter();
        } else if(Location.class.isAssignableFrom(rawType)) {
            adapter = new LocationAdapter(server);
        }
        @SuppressWarnings("unchecked")
        TypeAdapter<T> typeAdapter = (TypeAdapter<T>) adapter;


        return typeAdapter;
    }

}

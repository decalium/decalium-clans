package org.gepron1x.clans.plugin.migration.adapter;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.gepron1x.clans.plugin.clan.ClanBuilder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public final class ClanAdapter extends TypeAdapter<Clan> {
    private static final Type CLAN_MEMBER_COLLECTION = TypeToken.getParameterized(Collection.class, ClanMember.class).getType();
    private static final Type CLAN_HOME_COLLECTION = TypeToken.getParameterized(Collection.class, ClanHome.class).getType();
    private static final Type STATISTIC_MAP = TypeToken.getParameterized(Map.class, StatisticType.class, Integer.class).getType();
    private static final String ID = "id", TAG = "tag", OWNER = "owner", DISPLAY_NAME = "display_name", MEMBERS = "members", HOMES = "homes", STATISTICS = "statistics";
    private final Gson gson;

    public ClanAdapter(Gson gson) {
        this.gson = gson;

    }
    @Override
    public void write(JsonWriter out, Clan value) throws IOException {
        out.beginObject();

        out.name(ID).value(value.getId());

        out.name(TAG).value(value.getTag());

        out.name(OWNER);
        gson.toJson(value.getOwner(), UUID.class, out);

        out.name(DISPLAY_NAME);
        gson.toJson(value.getDisplayName(), Component.class, out);

        out.name(MEMBERS);
        gson.toJson(value.getMembers(), CLAN_MEMBER_COLLECTION, out);

        out.name(HOMES);
        gson.toJson(value.getHomes(), CLAN_HOME_COLLECTION, out);

        out.name(STATISTICS);
        gson.toJson(value.getStatistics(), STATISTIC_MAP, out);

        out.endObject();
    }

    @Override
    public Clan read(JsonReader in) throws IOException {
        boolean idExists = false;
        ClanBuilder builder = new ClanBuilder(Integer.MIN_VALUE);
        in.beginObject();
        while(in.hasNext()) {
            String fieldName = in.nextName();
            switch(fieldName) {
                case ID -> { builder.id(in.nextInt()); idExists = true; }
                case TAG -> builder.tag(in.nextString());
                case OWNER -> builder.owner(gson.fromJson(in, UUID.class));
                case DISPLAY_NAME -> builder.displayName(gson.fromJson(in, Component.class));
                case MEMBERS -> builder.members(gson.fromJson(in, CLAN_MEMBER_COLLECTION));
                case HOMES -> builder.homes(gson.fromJson(in, CLAN_HOME_COLLECTION));
                case STATISTICS -> builder.statistics(gson.fromJson(in, STATISTIC_MAP));
            }

        }
        in.endObject();
        Preconditions.checkArgument(idExists, "id should be present in json");

        return builder.build();
    }
}

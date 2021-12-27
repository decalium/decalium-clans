package org.gepron1x.clans.plugin.migration.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.api.clan.member.ClanMember;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public final class ClanMemberAdapter extends TypeAdapter<ClanMember> {

    private static final String UNIQUE_ID = "uuid", ROLE = "role";

    private final Gson gson;
    private final RoleRegistry roleRegistry;
    private final ClanBuilderFactory builderFactory;

    public ClanMemberAdapter(Gson gson, ClanBuilderFactory builderFactory, RoleRegistry roleRegistry) {
        this.builderFactory = builderFactory;
        this.gson = gson;
        this.roleRegistry = roleRegistry;
    }
    @Override
    public void write(JsonWriter out, ClanMember value) throws IOException {
        out.beginObject();
        out.name(UNIQUE_ID);
        gson.toJson(value.getUniqueId(), UUID.class, out);
        out.name(ROLE).value(value.getRole().getName());

        out.endObject();

    }

    @Override
    public ClanMember read(JsonReader in) throws IOException {
        ClanMember.Builder builder = builderFactory.memberBuilder();
        in.beginObject();
        while(in.hasNext()) {
            String fieldName = in.nextName();
            switch(fieldName) {
                case UNIQUE_ID -> builder.uuid(gson.fromJson(in, UUID.class));
                case ROLE -> builder.role(Objects.requireNonNull(roleRegistry.getRole(in.nextString()), "no role with name found"));
            }
        }
        in.endObject();
        return builder.build();
    }
}

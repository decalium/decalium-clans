package org.gepron1x.clans.migration.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.clan.ClanHome;

import java.io.IOException;
import java.util.UUID;

public final class ClanHomeAdapter extends TypeAdapter<ClanHome> {
    private static final String NAME = "name", CREATOR = "creator", DISPLAY_NAME = "display_name", ICON = "icon", LOCATION = "location";
    private final Gson gson;
    private final ClanBuilderFactory builderFactory;

    public ClanHomeAdapter(Gson gson, ClanBuilderFactory builderFactory) {
        this.builderFactory = builderFactory;
        this.gson = gson;
    }
    @Override
    public void write(JsonWriter out, ClanHome value) throws IOException {

        out.beginObject();

        out.name(NAME).value(value.getName());

        out.name(CREATOR);
        gson.toJson(value.getCreator(), UUID.class, out);

        out.name(DISPLAY_NAME);
        gson.toJson(value.getDisplayName(), Component.class, out);

        ItemStack icon = value.getIcon();

        if(icon != null) {
            out.name(ICON);
            gson.toJson(icon, ItemStack.class, out);
        }

        out.name(LOCATION);
        gson.toJson(value.getLocation(), Location.class, out);

        out.endObject();

    }

    @Override
    public ClanHome read(JsonReader in) throws IOException {
        ClanHome.Builder builder = builderFactory.homeBuilder();
        in.beginObject();
        while(in.hasNext()) {
            String fieldName = in.nextName();
            switch (fieldName) {
                case NAME -> builder.name(in.nextString());
                case CREATOR -> builder.creator(gson.fromJson(in, UUID.class));
                case DISPLAY_NAME -> builder.displayName(gson.fromJson(in, Component.class));
                case ICON -> builder.icon(gson.fromJson(in, ItemStack.class));
                case LOCATION -> builder.location(gson.fromJson(in, Location.class));
            }
        }
        in.endObject();
        return builder.build();
    }
}

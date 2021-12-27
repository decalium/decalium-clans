package org.gepron1x.clans.plugin.migration.adapter;

import com.google.common.base.Preconditions;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

import java.io.IOException;

public final class LocationAdapter extends TypeAdapter<Location> {
    private static final String WORLD = "world", X = "x", Y = "y", Z = "z", YAW = "yaw", PITCH = "pitch";
    private final Server server;

    public LocationAdapter(Server server) {
        this.server = server;
    }
    @Override
    public void write(JsonWriter out, Location value) throws IOException {
        out.beginObject();
        out.name(WORLD).value(value.getWorld().getName());

        out.name(X).value(value.getX());
        out.name(Y).value(value.getY());
        out.name(Z).value(value.getZ());
        float pitch = value.getPitch();
        float yaw = value.getYaw();

        if(pitch != 0) out.name(PITCH).value(pitch);
        if(yaw != 0) out.name(YAW).value(yaw);

        out.endObject();


    }

    @Override
    public Location read(JsonReader in) throws IOException {

        String worldName = null;
        double x = Double.NaN;
        double y = Double.NaN;
        double z = Double.NaN;
        float pitch = 0;
        float yaw = 0;

        in.beginObject();
        while(in.hasNext()) {
            String fieldName = in.nextName();
            switch(fieldName) {
                case WORLD -> worldName = in.nextString();
                case X -> x = in.nextDouble();
                case Y -> y = in.nextDouble();
                case Z -> z = in.nextDouble();
                case PITCH -> pitch = (float) in.nextDouble();
                case YAW -> yaw = (float) in.nextDouble();
            }
        }
        in.endObject();

        World world = worldName == null ? null : server.getWorld(worldName);
        Preconditions.checkState(!Double.isNaN(x), "x cannot be null");
        Preconditions.checkState(!Double.isNaN(y), "y cannot be null");
        Preconditions.checkState(!Double.isNaN(z), "z cannot be null");



        return new Location(world, x, y, z, pitch, yaw);
    }
}

package org.gepron1x.clans.plugin.migration.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.gepron1x.clans.api.statistic.StatisticType;

import java.io.IOException;

public final class StatisticTypeAdapter extends TypeAdapter<StatisticType> {

    @Override
    public void write(JsonWriter out, StatisticType value) throws IOException {
        out.value(value.name());

    }

    @Override
    public StatisticType read(JsonReader in) throws IOException {
        return new StatisticType(in.nextString());
    }
}
